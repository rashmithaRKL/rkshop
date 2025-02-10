# Men's Store - Android E-commerce App

A modern e-commerce Android application for men's clothing built with Kotlin and Java, featuring Firebase and MySQL integration.

## Features

- Product browsing and searching
- Shopping cart management
- User authentication
- Order tracking
- Profile management with Google Maps integration
- Real-time inventory updates
- Secure payment processing

## Tech Stack

- **Languages**: Kotlin, Java
- **Architecture**: MVVM
- **Database**: Firebase Realtime Database, MySQL
- **Dependencies**:
  - Android Navigation Component
  - Google Maps SDK
  - Firebase Authentication
  - Firebase Firestore
  - Kotlin Coroutines
  - LiveData & ViewModel
  - Material Design Components

## Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/mens-store.git
```

2. Add your Google Maps API key in `local.properties`:
```properties
MAPS_API_KEY=your_google_maps_api_key_here
```

3. Add your Firebase configuration file:
- Download `google-services.json` from Firebase Console
- Place it in the `app` directory

4. Build and run the project in Android Studio

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/mensstore/app/
│   │   │   ├── data/
│   │   │   │   ├── models/
│   │   │   │   └── repositories/
│   │   │   └── ui/
│   │   │       ├── profile/
│   │   │       ├── cart/
│   │   │       └── home/
│   │   └── res/
│   │       ├── layout/
│   │       ├── navigation/
│   │       └── values/
│   └── test/
└── build.gradle
```

## Requirements

- Android Studio Arctic Fox or later
- Android SDK 23 or higher
- Google Maps API key
- Firebase project setup

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
