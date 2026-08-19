module SeaJava.YT.Downloader {
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;
    requires com.twelvemonkeys.imageio.webp;
    requires com.twelvemonkeys.imageio.core;
    requires com.twelvemonkeys.common.lang;
    uses javax.imageio.spi.ImageWriterSpi;
    uses javax.imageio.spi.ImageReaderSpi;
    exports com.eryck.SeaJavaVideoDownloader;
    opens com.eryck.SeaJavaVideoDownloader to com.google.gson, javafx.fxml, javafx.graphics, javafx.web;
}