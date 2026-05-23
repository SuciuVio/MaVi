import os
from flask import Flask
from flask_jwt_extended import JWTManager
from models import db
from auth import auth_bp
from chat import chat_bp
from file_transfer import file_bp
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Create Flask app
app = Flask(__name__)

# Configuration
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///database.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JWT_SECRET_KEY'] = os.getenv('JWT_SECRET_KEY', 'your_jwt_secret_key')

# Initialize extensions
db.init_app(app)
jwt = JWTManager(app)

# Register blueprints
app.register_blueprint(auth_bp)
app.register_blueprint(chat_bp)
app.register_blueprint(file_bp)

# Create tables
with app.app_context():
    db.create_all()

# Root endpoint
@app.route('/api/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return {'message': 'MaVi Backend is running!'}, 200

# Error handlers
@app.errorhandler(404)
def not_found(e):
    return {'message': 'Endpoint not found'}, 404

@app.errorhandler(500)
def internal_error(e):
    return {'message': 'Internal server error'}, 500

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=os.getenv('FLASK_DEBUG', False))
