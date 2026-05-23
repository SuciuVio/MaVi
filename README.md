# MaVi - Android Chat Application with P2P File Transfer

**MaVi** is a complete Android chat application featuring real-time messaging and peer-to-peer file transfer over local network.

## Features

- 💬 **Real-time Chat** - Text messaging between users
- 📁 **P2P File Transfer** - Direct file transfer over local network
- 🔐 **Authentication** - Username/Password login system
- 💾 **Local Storage** - All data stored locally with SQLite
- 📱 **Android Native** - Built with Kotlin and Android best practices

## Project Structure

```
MaVi/
├── backend/                 # Python Flask backend
│   ├── app.py             # Main Flask application
│   ├── models.py          # Database models
│   ├── auth.py            # Authentication logic
│   ├── chat.py            # Chat endpoints
│   ├── file_transfer.py   # File transfer logic
│   ├── requirements.txt    # Python dependencies
│   └── database.db        # SQLite database
│
├── android/               # Android application
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/mavi/
│   │   │   │   │   ├── ui/
│   │   │   │   │   ├── data/
│   │   │   │   │   ├── network/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── res/
│   │   ├── build.gradle
│   │   └── AndroidManifest.xml
│   └── settings.gradle
│
└── docs/                  # Documentation
    ├── API.md            # API endpoints
    ├── SETUP.md          # Setup guide
    └── ARCHITECTURE.md   # Architecture overview
```

## Tech Stack

### Backend
- **Framework**: Flask (Python)
- **Database**: SQLite
- **API**: REST API
- **Authentication**: JWT (Username/Password)

### Frontend (Android)
- **Language**: Kotlin
- **Database**: Room (Local storage)
- **Architecture**: MVVM + Clean Architecture
- **Networking**: Retrofit + OkHttp

## Getting Started

### Backend Setup (Step 1)
```bash
cd backend
pip install -r requirements.txt
python app.py
```

### Android Setup (Step 2)
```bash
cd android
./gradlew build
./gradlew run
```

## Documentation

- [API Documentation](./docs/API.md)
- [Setup Guide](./docs/SETUP.md)
- [Architecture](./docs/ARCHITECTURE.md)

## License

MIT License

---

**Status**: 🚧 Under Development - Follow the setup steps for progress
