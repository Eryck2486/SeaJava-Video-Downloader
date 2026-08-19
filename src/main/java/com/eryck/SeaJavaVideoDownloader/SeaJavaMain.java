package com.eryck.SeaJavaVideoDownloader;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class SeaJavaMain extends Application {
    public static void main(String[] args) {
        launch(SeaJavaMain.class, args);
    }

    AppConfigs configs;
    Stage primaryStage;
    DlpController dlpCtl;
    HomeScreen homeScreen;
    SettingsScreen settingsScreen;
    StackPane root;
    ObservableList<Node> stackConteiner;

    static Image imgLoader(String url){
        System.out.println("Loading image from URL: "+url);
        if(url.endsWith(".webp")){
            try {
                URL imgUrl = URI.create(url).toURL();
                BufferedImage bimg = ImageIO.read(imgUrl);
                if(bimg!=null)
                {
                    return SwingFXUtils.toFXImage(bimg, null);
                }
            } catch (IOException e) {
                return new Image(url.replace(".webp", ".jpeg"));
            }
        }else {
            System.out.println("ThumbURL: "+url);
            return new Image(url, true);
        }
        return null;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        configs=AppConfigs.ReadConfig();
        Strings.gIns(configs);
        root = new StackPane();
        root.getStylesheets().add(new File("themes/standard-dark.css").toURI().toURL().toExternalForm());
        Scene scene = new Scene(root, 600, 600);
        dlpCtl = new DlpController("", configs);
        dlpCtl.TryUpdate();

        primaryStage.setTitle("SeaJava");
        primaryStage.setScene(scene);
        primaryStage.show();

        stackConteiner = root.getChildren();
        setHomeScreen();

        primaryStage.setOnCloseRequest(windowEvent -> {
            dlpCtl.Abort();
        });
    }

    void setHomeScreen() {
        if(homeScreen==null){
            homeScreen = new HomeScreen(this);
        }
        homeScreen.Set();
    }

    public void setSettingsScreen(){
        if(settingsScreen==null){
            settingsScreen = new SettingsScreen(this);
        }
        settingsScreen.Set();
    }

    public static void showDownloadsDone(){
        Alert allDoneAlert = new Alert(Alert.AlertType.INFORMATION, Strings.gIns().ALL_DW_FINISH, ButtonType.CLOSE);
        allDoneAlert.setTitle(Strings.gIns().ALL_DW_FINISH);
        allDoneAlert.setHeaderText(null);
        allDoneAlert.setContentText(Strings.gIns().ALL_DW_FINISH);
        allDoneAlert.show();
    }
}