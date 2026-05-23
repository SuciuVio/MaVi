# MaVi Setup Guide

## Prerequisites
- Python 3.8+
- pip (Python package manager)
- Android Studio (for Android development)
- Java 11+ (for Android development)
- Git

## Backend Setup (Python + Flask)

### 1. Clone the Repository
```bash
git clone https://github.com/SuciuVio/MaVi.git
cd MaVi/backend
```

### 2. Create Virtual Environment
```bash
python -m venv venv
```

### Activate Virtual Environment
**Windows:**
```bash
venv\Scripts\activate
```

**macOS/Linux:**
```bash
source venv/bin/activate
```

### 3. Install Dependencies
```bash
pip install -r requirements.txt
```

### 4. Configure Environment Variables
Edit the `.env` file and update the values:
```env
FLASK_ENV=development
FLASK_DEBUG=True
SECRET_KEY=your_secret_key_change_this_in_production
JWT_SECRET_KEY=your_jwt_secret_key_change_this_in_production
DATABASE_URL=sqlite:///database.db
PORT=5000
```

### 5. Run the Backend Server
```bash
python app.py
```

The server will start at `http://localhost:5000`

### Verify Backend is Running
```bash
curl http://localhost:5000/api/health
```

You should see:
```json
{
  "message": "MaVi Backend is running!"
}
```

---

## Android App Setup

### 1. Open Project in Android Studio
- Open Android Studio
- Select **File > Open**
- Navigate to the `MaVi/android` folder
- Click **Open**

### 2. Configure Server IP Address
Edit `android/app/src/main/java/com/mavi/network/ApiClient.kt`:
```kotlin
// Change this to your server IP/URL
const val BASE_URL = "http://192.168.1.100:5000/api/"  // Replace with your PC IP
```

### 3. Build and Run
- Connect an Android device via USB (or use Android emulator)
- Click **Run > Run 'app'**
- Select your device and click **OK**

---

## Database Setup

The database is automatically created when you run `app.py` for the first time.

To reset the database:
```bash
rm backend/database.db
python backend/app.py
```

---

## Testing the API

### Using cURL

#### Register a User
```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

#### Login
```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

#### Send a Message
```bash
curl -X POST http://localhost:5000/api/chat/messages/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"receiver_id":2,"content":"Hello!"}'
```

---

## Troubleshooting

### Backend Won't Start
- Check if port 5000 is already in use: `lsof -i :5000`
- Make sure all dependencies are installed: `pip install -r requirements.txt`

### Can't Connect to Backend from Android
- Verify your PC IP address: `ipconfig` (Windows) or `ifconfig` (macOS/Linux)
- Update the `BASE_URL` in `ApiClient.kt` with your correct IP
- Ensure both devices are on the same network

### Database Errors
- Delete `database.db` and restart the server
- Check permissions in the `backend` folder

---

## Next Steps

1. Register a user account in the app
2. Create another user to test messaging
3. Send messages between accounts
4. Test file transfer functionality

For more API details, see [API.md](./API.md)
