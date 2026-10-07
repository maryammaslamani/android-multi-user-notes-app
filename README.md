# Multi-User Notes App

An Android notes application that allows multiple local users to create accounts and manage their own notes. The app was developed as the Android project for **ENCS5150 - Advanced Computer Systems Engineering Laboratory** at Birzeit University.

## Features

- Sign up, sign in, and logout
- Validation for email addresses, names, and passwords
- Remembered email using SharedPreferences
- Local user and note storage using SQLite
- Separate notes for each registered user
- Add, view, edit, and delete notes
- Mark and unmark notes as favorites
- Favorite-heart animation
- Search notes by title or content
- Sort notes alphabetically or by creation date
- Save the selected sorting preference
- Categorize notes with tags and filter by tag
- Send a note using an installed email application
- View and edit the user profile
- Navigation drawer and fragment-based screens

## Technologies

- Java
- Android SDK
- XML layouts
- SQLite
- SharedPreferences
- Gradle Kotlin DSL
- Material Components

## Project Structure

```text
app/src/main/
|-- java/com/example/notes/   # Activities, fragments, models, adapters, and database helpers
|-- res/                      # Layouts, icons, menus, animations, colors, and themes
`-- AndroidManifest.xml       # Application components and launcher configuration
```

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/maryammaslamani/android-multi-user-notes-app.git
   ```

2. Open the cloned folder in Android Studio.
3. Allow Android Studio to complete the Gradle synchronization.
4. Select an emulator or a physical Android device running Android 8.0 (API 26) or later.
5. Run the application.

The application stores its data locally on the Android device and does not require an external server or API.

## Contributors

- [Maryam Maslamani](https://github.com/maryammaslamani)
- [Waad Abu Rukab](https://github.com/WaadAbuRukab)

## Academic Context

This project was created for educational purposes as part of the ENCS5150 course.
