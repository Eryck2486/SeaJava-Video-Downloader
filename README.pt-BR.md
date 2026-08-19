# Sobre o SeaJava Video Downloader
- Este é um app simples que tem o objetivo possibilitar o download de vídeos de diverssos sites.
- As plataformas que já foram testadas são:
    - <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABJklEQVR4Ae2WpVaFQRSFcaeS4QVwIjTiLbgk7D1w9xeg4FQcOu6acHeXtNnDmsFd7in/WetLI+f7dbYDAFEsAUvgoeAQ5k5iSQFpIH1kkMyQFXJMTsgpwTuc6jlHes2M3qNP71mge7g76DLNg8gygZ1QckGmuSdZJ7Azq8RDCSQTCJGgBCoFBWqVQLegQJcSmPnWIr9oICMXcIr4C4FJJbD9rUX+NtzX+CIQlfVbgS0lcP4jAVNt/UCA7acCZ0oAPxfQdXMLFNUBvpHflvgbAVM7h0B6zrfeD3kB+Ucg/xLKf4biPyLJX3GH9GFUogRSBQUSTSBZEwskQpFs1USyl6E0gRSRJtJPhsnci1B69oVQeqzXzOo9+kmj3juBeDw2BkSxBCyBO+9s03HRLVCoAAAAAElFTkSuQmCC" width="10" height="10"> - YouTube
    - <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAXVBMVEVHcEwHZv8HaP8IZ/8IZ/8IZv8IaP8IZ/8HZv8Haf8IZ/8IaP8IZ/8AXv8AV/8AYv+SqP/P1//m6//e5f9wlP////+tvP9jiv82c/8IZ/8Iaf+fsf////////8AW/+wrobhAAAAH3RSTlMAM4bC6v1h7v8Z3UL///////7/////////wRiFwenBhp/Z0gAAAOhJREFUeAFtk0UWwzAMBWWHFHOZe/9bBn9Nr7Mdg5AiQjZt17WNFFTTDxwZ+kKNigvUmJw2XGF0dByx1jlnPTPDjiYqDofj8XQ4L9bsL6vozpedq13/3eJk4G+XXPIac8zBHUs5EIl48Q4Tws3ziiAZ5f7j3Vq7O5b0iOGE1Z0cRxpqS3mwSbbU4VHndukW8GwHiQ/BzUO2KceIw7MIqJBHyIZk/WwKSv6KcD/frlsqt/P5joBELJ+3iNZD8YDC/82zz1pWS5U1u5bmmY1JLdMQmVoaOIxmIdVYDXWUxVAD8Xp/FvnN1mEG0f0e5ixC4ikAAAAASUVORK5CYII=" width="10" height="10"/> - Facebook
    - <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAABsFBMVEVHcEx6Gft+F/x0FPyBDPORB+WnAt69AN/IANzSANrVANDpEKtpGv52FvjhANfhAMnbBLfVBJGHGP2YF/ueAu+zAPDrALTtA6quMNrKWuPhYtzvVMHvILzuAMbMf+z+3/3/////9//vd9v5AbryAqWuGvrxr9371vf2tePwp9/vnuD/+//+ArP9AJ395vrTRNPucMLwAo7HG/fxFM39wPHtPZ38AoXjGfD6E7TsJ6rzI5PzV6P7AXX9FpDtocvxZJ79AGTwrb39EGf8InX87PPxHGv+E3j8H1vugJX7KlLxZJH1u8v7M1X2Kz78M0XwbF79Kmbyu6/9OTD9QUPwWGf//vPrQlr0NGj7AFn0imzySCL7Szb4yb/7QiT8UBL+Wi3+7Nn1BEbzz6b5Ww79ZiL0dUf11bD8eyj6aAH+dg/+bh3zFUX93OjymjH9ign8hQT5hRj8fQ32t2f/+djypEz5kAL22539dwf+mQL5ogD3zmHzn4r4Go77Yyv/qgT+sAL5rwD5vzL1yVr6mAf9tgD9vQD/xAD/KnD2lwD8ygD+OGP/LGv+rQ3+uQj/ywL7VDI4+UxjAAAAkHRSTlMAXMb/////////xl0Kz///zgv//////8r//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////8X/////xl3//////8/////OC//PClzG/8UUwjnzAAACFUlEQVR4ATXQRbobRxhG4fO16q8moZmZPQkz0wayg3CygHBGnmYjwTV4FpqamRl1o3u7pWqVoR+fYb3FAiQBQiDaAaQJiK4WW6JFARiS7joe2qBdPRbCAAR4IY0AabxMiMdJC32gZuLavYqylVaHnf97pEjLYdndZVNSqXWC3CSfZVpCO+FenvR1ru9py6tq2XjYgEt6vd5Y6OyOVeSPIr87jGl515GbM9gTwrmd1zfk1coLbKqqtbfWhpDkARLg5kSpbbjVc9fMriVcXXHVYhR4F2DVvXnfWHVZscDGcVWzjDUX8JBcv25Aj3hH2bIs63gtAeAczm3qULFeGtc79Hg8W1gW/GMmyX2xBsyvjGnTeO/LKsrTlvg8AbwZ2MNnQbuoRUscDM3s+XoQ0rSXb9IGvwbKNeAcdCFTTE7tL8edcnZ4v6DTrizLEnDOFt46fCeJtw/vKO9bTouNBCMzW2kfjA4fHr22rDJPQQEhcWb95+f2qPnw5ZeHsTFTsVWdawF9bFBN1khiiQ7TGaGKydG3J+fdPX0hMv35oQTUSmrqWZiMDu7ZfkL39FU69+jX58tVwC0ANz3I0yHM45K+qXxHqL7wH096vlx/YbF2Cy/rp/sFXkKqIIN7JPfnVNVD/FscmHgvQBCJoY6LnXo6C1X96ncOK2fQfkqDXIwsRj+DN4SAn1U5wGDWxDidNqGe7r9zAB4A0j29NbySqvYAAAAASUVORK5CYII=" width="10" height="10"/> - Instagram
    - <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAw1BMVEUAAAAAAAAFT1IAQUNBub6u5eexxcpyQ0wIAABaxsv5////9/nGj5oyAAD////84eeEYmvm4uN4f4A5P0MAFBXx+vu019khAAD60tr37vDlmqpXAAsALzAKc3gpo6hjpqr/6O3idYqcbHTSrLP6iJ9rABOc5Ofi+vvJYnXLbn8/AAI9HyWWIDhRz9TH9PffY3oDAAC99vjhSGe7KEenACiF4+f/1961PVaB09bFkZswjJGwuLr//f7xqbYYHh/9tsTiL1Yh8R0/AAAAAXRSTlP+GuMHfQAAANJJREFUeAG90kUCg1AMBNDiPtTd3d2V+5+quMOS2b6vSXI5IjnZIElSyUgzLMcnoSBKspKMUPMpiEKxVE5EVKr5WgTLBkr1BtBsBbHd6fZKOvYHw1F9PAkgSU9njXlNx8Vyxa2XfmxvtoC0M1GH/d6P3QPU8fFkY/Ar7Q0ag3ONIOKQvOB60i0WqSnUG6FnI6rzMJbvkB76zo5+wk0JIf8EGq9ndzN7f9bRrlQBcTYTcT22iAh+X9Ib7/dPm8Q1u5Zvfpq7c4uIQ56otVrLjEYzJX+4mxcG80U+CwAAAABJRU5ErkJggg==" width="10" height="10"/> - TikTok
    - <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAWlBMVEX////3+/Pa7cuIyUd6wyeYz2TF46yDxj6Fx0J/xTWr14Ty+e2Cxju43Znl8tp9xC/s9uTU6sKm1X3M5raPy1Tb7cy+4KF7wyv7/fmd0W3u9+fI5LCw2oy13JPxjEMuAAAAqklEQVR4Ac2RRxLDMAgAUUwEuPfu/38zI5wmjXxO9rp04LeYa3VLEO/WxB0SMwmmWUQKsZJjASFlxS8kqQNpRdPUEja+bHOX0+aiGjtP9sQsA8A4kVov1yVObo+sVyvfsnLyE8fT91ATM1egaGZVxjLn5cwMe1YrwCans+G0lOwkrO6Ab1KXQDkrOIJHJ/wid/t6ZPhSmM4QckysL2triGAFcdkMXGDgH3gAEe4JPSXZypIAAAAASUVORK5CYII=" height="10" width="10"/> - Rumble

## Requisitos:
   - Sistema operacional Windows ou Linux
   - Versão Java mais recente
   - Acesso a internet
   - 635MB de espaço livre em disco
   - Java SE 26.0 ou superior: https://www.oracle.com/java/technologies/javase/jdk26-archive-downloads.html

## Este software depende dos seguintes projetos de código aberto:

- 1. JavaFX
   - Licença: GPLv2 com exceção de classpath
   - Descrição: Plataforma de interface gráfica para Java.
   - Website: https://openjfx.io/
   - Repositório: https://github.com/openjdk/jfx

- 2. Google Gson
   - Licença: Apache License 2.0
   - Descrição: Biblioteca Java usada para converter objetos Java em JSON e vice-versa.
   - Repositório: https://github.com/google/gson

- 3. FFmpeg
   - Licença: LGPL v2.1+ / GPL v2+
   - Descrição: Solução completa e multiplataforma para gravar, converter e transmitir áudio/vídeo.
   - Site: https://ffmpeg.org/download.html
   - Repositório do FFmpeg para Windows: https://github.com/BtbN/FFmpeg-Builds

- 4. yt-dlp
   - Licença: Sem licença (Domínio Público)
   - Repositório: https://github.com/yt-dlp/yt-dlp

- 5. TwelveMonkeys ImageIO
   - License: BSD 3-Clause License
   - Description: Uma coleção de plugins e extensões para o ImageIO do Java, permite a decodificação de imagens WebPC em java puro.
   - Repository: https://github.com/haraldk/TwelveMonkeys

- 6. XZ para Java (por Tukaani)
   - Licença: Domínio Público
   - Site: https://tukaani.org/xz/java.html

- 7. QuickJS
   - Licença: Licença MIT
   - Descrição: Mecanismo Javascript pequeno e incorporável.
   - Site: https://bellard.org/quickjs/
   - Repositório: https://github.com/quickjs-ng/quickjs
