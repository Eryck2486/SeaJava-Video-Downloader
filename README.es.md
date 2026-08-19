[![Português](https://img.shields.io/badge/Language-Português-green.svg)](README.pt.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](README.md)
[![Español](https://img.shields.io/badge/Language-Español-yellow.svg)](README.es.md)
# Descargador de vídeos SeaJava

El **Descargador de vídeos SeaJava** es una aplicación sencilla y eficiente desarrollada en JavaFX con el objetivo de permitir la descarga de vídeos y contenido multimedia desde diversas plataformas.

---

### 🌐 Plataformas compatibles (probadas)
- <img src="assets/youtube.png" width="16" height="16" valign="middle"> **YouTube**
- <img src="assets/facebook.png" width="16" height="16" valign="middle"> **Facebook**
- <img src="assets/instagram.png" width="16" height="16" valign="middle"> **Instagram**
- <img src="assets/tiktok.png" width="16" height="16" valign="middle"> **TikTok**
- <img src="assets/rumble.png" width="16" height="16" valign="middle"> **Rumble**

---

## 📋 Requisitos del sistema

- **Sistema operativo:** Windows o Linux
- **Conexión:** Acceso a Internet
- **Almacenamiento:** 635 MB de espacio libre en disco
- **Java:** [Java 21.0 o superior](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

---

## 🖼️ Capturas de pantalla

|      <p align="center"><b>Pantalla de inicio</b></p>       |      <p align="center"><b>Descargas multimedia</b></p>       |
|:----------------------------------------------------------:|:------------------------------------------------------------:|
| <img src="assets/screenshots/screenshot0.png" width="380"> |  <img src="assets/screenshots/screenshot1.png" width="380">  |

|         <p align="center"><b>Configuración</b></p>         | <p align="center"><b>Descargar lista de reproducción</b></p>  |
|:----------------------------------------------------------:|:-------------------------------------------------------------:|
| <img src="assets/screenshots/screenshot2.png" width="380"> |  <img src="assets/screenshots/screenshot3.png" width="380">   |

## Cómo usar:

En muchos sistemas operativos, simplemente haga doble clic en el archivo `.jar` descargado para abrir la aplicación.

Si el programa no se abre automáticamente o si desea monitorear los registros de inicio a través de la terminal, ejecute:

``bash
java -jar SeaJavaVideoDownloader-x.x.x.jar

``

El primer inicio puede tardar un poco dependiendo de su conexión a internet, ya que se descargarán las dependencias en esta etapa.

Nota sobre las cookies: En la mayoría de las plataformas, se requieren cookies de sesión para descargar contenido restringido/privado. En Windows, los navegadores basados ​​en Chromium (como Microsoft Edge, Opera y Google Chrome) podrían no funcionar para la extracción automática de cookies debido a las restricciones de seguridad del sistema. Recomendamos usar Mozilla Firefox.

## Dependencias de código abierto

Este software se basa en excelentes proyectos comunitarios de código abierto:

| Proyecto                   | Licencia                           | Descripción                                                                     | Enlaces                                                                |
|:---------------------------|:-----------------------------------|:--------------------------------------------------------------------------------|:-----------------------------------------------------------------------|
| JavaFX                     | GPLv2 con excepción de classpath   | Plataforma de interfaz gráfica para Java.                                       | https://openjfx.io/ https://github.com/openjdk/jfx                     |
| Google Gson                | Licencia Apache 2.0                | Serialización y deserialización de objetos Java a JSON.                         | https://github.com/google/gson                                         |
| FFmpeg                     | LGPL v2.1+ / GPL v2+               | Marco de trabajo para manipular, convertir y multiplexar audio y vídeo.         | https://ffmpeg.org/download.html https://github.com/BtbN/FFmpeg-Builds |
| yt-dlp                     | Sin licencia (Dominio público)     | Herramienta de línea de comandos para descargar contenido multimedia de la web. | https://github.com/yt-dlp/yt-dlp                                       |
| TwelveMonkeys ImageIO      | Licencia BSD de 3 cláusulas        | Decodificación nativa de imágenes WebP en Java puro.                            | https://github.com/haraldk/TwelveMonkeys                               |
| XZ para Java (por Tukaani) | Dominio público                    | Biblioteca para manipular y descomprimir archivos .xz.                          | https://tukaani.org/xz/java.html                                       |
| QuickJS                    | Licencia MIT                       | Motor JavaScript ligero e integrable.                                           | https://bellard.org/quickjs/ https://github.com/quickjs-ng/quickjs     |