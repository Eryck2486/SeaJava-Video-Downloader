
[![Português](https://img.shields.io/badge/Language-Português-green.svg)](README.pt.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README.md)
[![Español](https://img.shields.io/badge/Language-Español-yellow.svg)](README.es.md)
# SeaJava Video Downloader

**SeaJava Video Downloader** is a simple and efficient application developed using JavaFX, designed to enable the downloading of videos and media from various platforms.

---

### 🌐 Supported Platforms (Tested)
- <img src="assets/youtube.png" width="16" height="16" valign="middle"> **YouTube**
- <img src="assets/facebook.png" width="16" height="16" valign="middle"> **Facebook**
- <img src="assets/instagram.png" width="16" height="16" valign="middle"> **Instagram**
- <img src="assets/tiktok.png" width="16" height="16" valign="middle"> **TikTok**
- <img src="assets/rumble.png" width="16" height="16" valign="middle"> **Rumble**

---

## 📋 System Requirements

- **Operating System:** Windows or Linux
- **Connection:** Internet access
- **Storage:** 635 MB of free disk space
- **Java:** [Java 21.0 or higher](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

---

## 🖼️ Screenshots

|          <p align="center"><b>Main Screen</b></p>          | <p align="center"><b>Media Downloads</b></p> |
|:----------------------------------------------------------:|:---:|
| <img src="assets/screenshots/screenshot0.png" width="380"> | <img src="assets/screenshots/screenshot1.png" width="380"> |

|           <p align="center"><b>Settings</b></p>            | <p align="center"><b>Playlist Download</b></p> |
|:----------------------------------------------------------:|:---:|
| <img src="assets/screenshots/screenshot2.png" width="380"> | <img src="assets/screenshots/screenshot3.png" width="380"> | ## How to use:

On most operating systems, simply **double-click** the downloaded `.jar` file to launch the application.

If the program does not open automatically or if you want to monitor startup logs via the terminal, run:

```bash
java -jar SeaJavaVideoDownloader-x.x.x.jar
```
The initial launch may take some time depending on your internet connection, as dependencies are downloaded during this stage.

Note on Cookies: On most platforms, session cookies are required to download restricted or private content. On Windows, Chromium-based browsers (such as Microsoft Edge, Opera, and Google Chrome) may not work for automatic cookie extraction due to native system security restrictions. We recommend using Mozilla Firefox.

## Open Source Dependencies

This software is built upon excellent projects from the open-source community:

| Project                  | License                         | Description                                                     | Links                                                                  |
|:-------------------------|:--------------------------------|:----------------------------------------------------------------|:-----------------------------------------------------------------------|
| JavaFX                   | GPLv2 with Classpath Exception  | GUI platform for Java.                                          | https://openjfx.io/ https://github.com/openjdk/jfx                     |
| Google Gson              | Apache License 2.0              | Serialization and deserialization of Java objects to JSON.      | https://github.com/google/gson                                         |
| FFmpeg                   | LGPL v2.1+ / GPL v2+            | Audio and video manipulation, conversion, and muxing framework. | https://ffmpeg.org/download.html https://github.com/BtbN/FFmpeg-Builds |
| yt-dlp                   | Unlicensed (Public Domain)      | Command-line tool for downloading web media.                    | https://github.com/yt-dlp/yt-dlp                                       |
| TwelveMonkeys ImageIO    | BSD 3-Clause License            | Native WebP image decoding in pure Java.                        | https://github.com/haraldk/TwelveMonkeys                               |
| XZ for Java (by Tukaani) | Public Domain                   | Library for handling and decompressing .xz files.               | https://tukaani.org/xz/java.html                                       |
| QuickJS                  | MIT License                     | Lightweight and embeddable JavaScript engine.                   | https://bellard.org/quickjs/ https://github.com/quickjs-ng/quickjs     |