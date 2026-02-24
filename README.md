# 🎵 Musicfy — Android Music Player

> 🎧 A Modern & Beautifully Designed Android Music Player Application

---

## 📌 Overview

**Musicfy** is a sleek Android music player app built with **Android Studio & Java**. It allows users to browse, play, and manage songs directly from their device with a smooth and user-friendly interface.

This project demonstrates core Android development concepts including **MediaPlayer integration**, **RecyclerView**, **Room Database**, and **Material UI design**.

---

## ✨ Features

- 🎶 Browse all local songs from device storage
- ▶️ Play / Pause music with smooth controls
- ⏭ Next & Previous song navigation
- ⏱ Real-time SeekBar progress tracking
- 🖼 Display song title, artist & album artwork
- 📂 Create & manage playlists
- 🕒 Recently played songs section
- 🎨 Clean and modern Material UI

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Android Studio | Development Environment |
| Java | Programming Language |
| MediaPlayer API | Audio Playback |
| RecyclerView | Song Listing |
| Room / SQLite | Local Database |
| Material Design | UI Components |

---

## 🗄️ Database Structure

The app uses **Room (SQLite)** for local storage.

**Stores:**
- Playlists
- Recently played tracks

**Main Components:**
- `Entity` – Defines song model
- `DAO` – Handles database queries
- `Database Class` – Initializes Room database

---

## 📱 App Screens

1. **Splash Screen** – App launch with logo animation
2. **Registration Screen** – User sign up
3. **Home / Library Screen** – Displays all songs
4. **Music Player Screen** – Full playback controls with SeekBar

---

## 📂 Project Structure

```
MusicApp/
│
├── app/src/main/java/com/example/myapplication/
│   ├── MainActivity.java
│   ├── PlaySong.java
│   ├── Registration.java
│   ├── SongAdapter.java
│   └── SplashActivity.java
│
├── res/
│   ├── layout/
│   ├── drawable/
│   ├── values/
│   └── mipmap/
│
├── AndroidManifest.xml
└── build.gradle
```

---

## 🚀 Installation & Setup

### 🔹 Requirements
- Android Studio (Latest Version Recommended)
- Minimum SDK 21+
- Android Emulator or Physical Device

### 🔹 Steps to Run

1. **Clone the repository**
```bash
git clone https://github.com/AAbdullahRajput/Musicfy.git
```

2. **Open in Android Studio**

3. **Sync Gradle files**

4. **Click Run ▶**

---

## 🎯 Learning Outcomes

This project demonstrates:
- Android Activity & Fragment lifecycle
- Media playback handling with MediaPlayer API
- RecyclerView with custom adapter
- Local database integration (Room/SQLite)
- Clean UI/UX design with Material components

---

## 🔮 Future Improvements

- 🌐 Online streaming integration
- 🎵 Background playback service
- 🔔 Notification media controls
- 🌙 Dark mode support
- ❤️ Favorites / liked songs
- ☁️ Cloud sync

---

## 👨‍💻 Author

**Abdullah Rajput**
🌐 GitHub: [@AAbdullahRajput](https://github.com/AAbdullahRajput)

---

## ⭐ Support

If you like this project, please **star ⭐ the repository** on GitHub — it means a lot!
