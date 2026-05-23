from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from models import db, Message, User
from datetime import datetime

chat_bp = Blueprint('chat', __name__, url_prefix='/api/chat')

# Send message endpoint
@chat_bp.route('/messages/send', methods=['POST'])
@jwt_required()
def send_message():
    """Send a message to another user"""
    sender_id = get_jwt_identity()
    data = request.get_json()
    
    if not data or not data.get('receiver_id') or not data.get('content'):
        return jsonify({'message': 'receiver_id and content are required'}), 400
    
    receiver_id = data.get('receiver_id')
    content = data.get('content').strip()
    
    if not content:
        return jsonify({'message': 'Message content cannot be empty'}), 400
    
    # Check if receiver exists
    receiver = User.query.get(receiver_id)
    if not receiver:
        return jsonify({'message': 'Receiver not found'}), 404
    
    # Cannot send message to yourself
    if sender_id == receiver_id:
        return jsonify({'message': 'Cannot send message to yourself'}), 400
    
    try:
        message = Message(
            sender_id=sender_id,
            receiver_id=receiver_id,
            content=content
        )
        db.session.add(message)
        db.session.commit()
        
        return jsonify({
            'message': 'Message sent successfully',
            'data': message.to_dict()
        }), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error sending message'}), 500


# Get messages between two users
@chat_bp.route('/messages/<int:other_user_id>', methods=['GET'])
@jwt_required()
def get_messages(other_user_id):
    """Get all messages between current user and another user"""
    current_user_id = get_jwt_identity()
    
    # Check if other user exists
    other_user = User.query.get(other_user_id)
    if not other_user:
        return jsonify({'message': 'User not found'}), 404
    
    # Get messages
    messages = Message.query.filter(
        ((Message.sender_id == current_user_id) & (Message.receiver_id == other_user_id)) |
        ((Message.sender_id == other_user_id) & (Message.receiver_id == current_user_id))
    ).order_by(Message.timestamp).all()
    
    return jsonify([msg.to_dict() for msg in messages]), 200


# Get unread messages
@chat_bp.route('/messages/unread', methods=['GET'])
@jwt_required()
def get_unread_messages():
    """Get all unread messages for current user"""
    current_user_id = get_jwt_identity()
    
    unread_messages = Message.query.filter_by(
        receiver_id=current_user_id,
        is_read=False
    ).all()
    
    return jsonify([msg.to_dict() for msg in unread_messages]), 200


# Mark message as read
@chat_bp.route('/messages/<int:message_id>/read', methods=['PUT'])
@jwt_required()
def mark_as_read(message_id):
    """Mark a message as read"""
    current_user_id = get_jwt_identity()
    
    message = Message.query.get(message_id)
    if not message:
        return jsonify({'message': 'Message not found'}), 404
    
    # Only the receiver can mark as read
    if message.receiver_id != current_user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    try:
        message.is_read = True
        db.session.commit()
        
        return jsonify({
            'message': 'Message marked as read',
            'data': message.to_dict()
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error updating message'}), 500


# Delete message
@chat_bp.route('/messages/<int:message_id>', methods=['DELETE'])
@jwt_required()
def delete_message(message_id):
    """Delete a message"""
    current_user_id = get_jwt_identity()
    
    message = Message.query.get(message_id)
    if not message:
        return jsonify({'message': 'Message not found'}), 404
    
    # Only sender can delete their own message
    if message.sender_id != current_user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    try:
        db.session.delete(message)
        db.session.commit()
        
        return jsonify({'message': 'Message deleted successfully'}), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error deleting message'}), 500


# Get chat list (users I have chatted with)
@chat_bp.route('/conversations', methods=['GET'])
@jwt_required()
def get_conversations():
    """Get list of users current user has chatted with"""
    current_user_id = get_jwt_identity()
    
    # Get distinct users from messages
    conversations = db.session.query(User).join(
        Message, 
        (Message.sender_id == User.id) | (Message.receiver_id == User.id)
    ).filter(
        ((Message.sender_id == current_user_id) | (Message.receiver_id == current_user_id)) &
        (User.id != current_user_id)
    ).distinct().all()
    
    return jsonify([user.to_dict() for user in conversations]), 200
