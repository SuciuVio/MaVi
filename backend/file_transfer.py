from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from models import db, FileTransfer, User
import os
from datetime import datetime
from werkzeug.utils import secure_filename

file_bp = Blueprint('file', __name__, url_prefix='/api/files')

# Configuration
UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif', 'doc', 'docx', 'zip', 'rar'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# Create upload folder if it doesn't exist
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

# Initiate file transfer
@file_bp.route('/transfer/init', methods=['POST'])
@jwt_required()
def init_file_transfer():
    """Initiate a file transfer request"""
    sender_id = get_jwt_identity()
    data = request.get_json()
    
    if not data or not data.get('receiver_id') or not data.get('filename') or not data.get('file_size'):
        return jsonify({'message': 'receiver_id, filename, and file_size are required'}), 400
    
    receiver_id = data.get('receiver_id')
    filename = data.get('filename')
    file_size = data.get('file_size')
    
    # Check if receiver exists
    receiver = User.query.get(receiver_id)
    if not receiver:
        return jsonify({'message': 'Receiver not found'}), 404
    
    # Cannot send to yourself
    if sender_id == receiver_id:
        return jsonify({'message': 'Cannot send file to yourself'}), 400
    
    try:
        file_transfer = FileTransfer(
            sender_id=sender_id,
            receiver_id=receiver_id,
            filename=secure_filename(filename),
            file_size=file_size,
            status='pending'
        )
        db.session.add(file_transfer)
        db.session.commit()
        
        return jsonify({
            'message': 'File transfer initiated',
            'data': file_transfer.to_dict()
        }), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error initiating file transfer'}), 500


# Upload file
@file_bp.route('/upload/<int:transfer_id>', methods=['POST'])
@jwt_required()
def upload_file(transfer_id):
    """Upload file data for a transfer"""
    sender_id = get_jwt_identity()
    
    # Check if transfer exists and belongs to sender
    transfer = FileTransfer.query.get(transfer_id)
    if not transfer:
        return jsonify({'message': 'Transfer not found'}), 404
    
    if transfer.sender_id != sender_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    if 'file' not in request.files:
        return jsonify({'message': 'No file provided'}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({'message': 'No file selected'}), 400
    
    try:
        filename = secure_filename(file.filename)
        file_path = os.path.join(UPLOAD_FOLDER, f"{transfer_id}_{filename}")
        file.save(file_path)
        
        transfer.file_path = file_path
        transfer.status = 'completed'
        db.session.commit()
        
        return jsonify({
            'message': 'File uploaded successfully',
            'data': transfer.to_dict()
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error uploading file'}), 500


# Download file
@file_bp.route('/download/<int:transfer_id>', methods=['GET'])
@jwt_required()
def download_file(transfer_id):
    """Download a file from a completed transfer"""
    receiver_id = get_jwt_identity()
    
    transfer = FileTransfer.query.get(transfer_id)
    if not transfer:
        return jsonify({'message': 'Transfer not found'}), 404
    
    if transfer.receiver_id != receiver_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    if transfer.status != 'completed':
        return jsonify({'message': 'File transfer not completed'}), 400
    
    if not os.path.exists(transfer.file_path):
        return jsonify({'message': 'File not found'}), 404
    
    try:
        from flask import send_file
        return send_file(transfer.file_path, as_attachment=True)
    except Exception as e:
        return jsonify({'message': 'Error downloading file'}), 500


# Get transfer status
@file_bp.route('/transfer/<int:transfer_id>', methods=['GET'])
@jwt_required()
def get_transfer_status(transfer_id):
    """Get status of a file transfer"""
    user_id = get_jwt_identity()
    
    transfer = FileTransfer.query.get(transfer_id)
    if not transfer:
        return jsonify({'message': 'Transfer not found'}), 404
    
    # Check if user is sender or receiver
    if transfer.sender_id != user_id and transfer.receiver_id != user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    return jsonify(transfer.to_dict()), 200


# Get all incoming file transfers
@file_bp.route('/incoming', methods=['GET'])
@jwt_required()
def get_incoming_files():
    """Get all incoming file transfers"""
    receiver_id = get_jwt_identity()
    
    transfers = FileTransfer.query.filter_by(receiver_id=receiver_id).all()
    
    return jsonify([transfer.to_dict() for transfer in transfers]), 200


# Get all outgoing file transfers
@file_bp.route('/outgoing', methods=['GET'])
@jwt_required()
def get_outgoing_files():
    """Get all outgoing file transfers"""
    sender_id = get_jwt_identity()
    
    transfers = FileTransfer.query.filter_by(sender_id=sender_id).all()
    
    return jsonify([transfer.to_dict() for transfer in transfers]), 200


# Cancel file transfer
@file_bp.route('/transfer/<int:transfer_id>/cancel', methods=['PUT'])
@jwt_required()
def cancel_transfer(transfer_id):
    """Cancel a file transfer"""
    user_id = get_jwt_identity()
    
    transfer = FileTransfer.query.get(transfer_id)
    if not transfer:
        return jsonify({'message': 'Transfer not found'}), 404
    
    # Only sender can cancel
    if transfer.sender_id != user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    
    if transfer.status == 'completed':
        return jsonify({'message': 'Cannot cancel completed transfer'}), 400
    
    try:
        transfer.status = 'failed'
        db.session.commit()
        
        return jsonify({
            'message': 'Transfer cancelled',
            'data': transfer.to_dict()
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'message': 'Error cancelling transfer'}), 500
