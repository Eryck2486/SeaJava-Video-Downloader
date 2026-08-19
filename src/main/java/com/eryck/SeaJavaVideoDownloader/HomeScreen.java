package com.eryck.SeaJavaVideoDownloader;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends VBox {
    static class StatusPane extends BorderPane{
        BorderPane vlconteiner;
        ImageView animation;
        Label statusMsg;
        public enum AnimationEnum {
            LOADING("https://cdn.pixabay.com/animation/2023/08/11/21/18/21-18-05-265_256.gif"),
            ERROR("https://cdn.pixabay.com/photo/2017/02/12/21/29/false-2061131_960_720.png"),
            HIDDEN(null);

            private final String imageURL;
            AnimationEnum(String imageURL){
                this.imageURL=imageURL;
            }

            public Image getImage(){
                if(imageURL==null) return null;
                return new Image(imageURL);
            }
        }

        StatusPane(){
            animation = new ImageView();
            animation.setFitWidth(250);
            animation.setFitHeight(250);
            statusMsg = new Label();
            setCenter(animation);
            vlconteiner=new BorderPane();
            vlconteiner.setCenter(statusMsg);
            setBottom(vlconteiner);
        }

        public void SetAnimation(AnimationEnum animationRes, String message){
            animation.setImage(animationRes.getImage());
            statusMsg.setText(message);
        }

        public void SetText(String text){
            statusMsg.setText(text);
        }
    }

    SeaJavaMain main;
    AppConfigs configs;
    StackPane stackPane;
    ObservableList<Node> stackConteiner;
    ObservableList<Node> homeConteiner;
    PlayListDownloadLayout playListDownloadMenu;
    MediaDownloadLayout mediaDownloadMenu;
    DlpController dlpCtl;
    Stage primaryStage;
    StackPane mediaDownloadConteiner;
    StatusPane status;

    HomeScreen(SeaJavaMain main){
        AboutAlert aboutDialog = new AboutAlert("SeaJava YT Downloader", "1.0.0", "Eryck Rocumback");
        aboutDialog.show();
        this.main=main;
        this.dlpCtl=main.dlpCtl;
        this.primaryStage=main.primaryStage;
        this.configs=main.configs;
        stackPane=main.root;
        stackConteiner=stackPane.getChildren();
        stackConteiner.add(this);
        homeConteiner = getChildren();
        //Toolbar elements
        BorderPane toolBar = new BorderPane();
        Button settingsBtn = new Button(Strings.gIns().SETTINGS);
        settingsBtn.setOnAction(actionEvent -> {
            setVisible(false);
            main.setSettingsScreen();
        });
        Button aboutButton = new Button(Strings.gIns().ABOUT);
        aboutButton.setOnAction(actionEvent ->{
            aboutDialog.show();
        });
        toolBar.setLeft(aboutButton);
        toolBar.setRight(settingsBtn);
        homeConteiner.add(toolBar);
        //End toolbar elements

        //Search bar elements
        HBox searchBar = new HBox();
        ObservableList<Node> searchBConteiner = searchBar.getChildren();
        TextField searchInput = new TextField();
        searchInput.setPromptText(Strings.gIns().SEARCH_TIP);
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBConteiner.add(searchInput);

        Button searchButton = new Button(Strings.gIns().SEARCH);
        searchButton.setOnAction(actionEvent -> {
            Analyze(searchInput.getText());
        });
        searchBConteiner.add(searchButton);
        //End search bar elements
        homeConteiner.add(searchBar);
        mediaDownloadConteiner=new StackPane();
        status=new StatusPane();
        mediaDownloadConteiner.getChildren().add(status);
        homeConteiner.add(mediaDownloadConteiner);
    }

    public void Set(){
        setVisible(true);
        toFront();
        //End Search bar elements
    }

    public void Analyze(String url) {
        List<String> cmd = new ArrayList<>();
        status.SetAnimation(StatusPane.AnimationEnum.LOADING, Strings.gIns().LOADING);
        if(playListDownloadMenu == null){
            playListDownloadMenu = new PlayListDownloadLayout(this, configs, dlpCtl, primaryStage);
            mediaDownloadConteiner.getChildren().add(playListDownloadMenu);
        } else playListDownloadMenu.Reset();
        if (mediaDownloadMenu == null) {
            mediaDownloadMenu = new MediaDownloadLayout(configs, dlpCtl, primaryStage, false);
            mediaDownloadConteiner.getChildren().add(mediaDownloadMenu);
        } else mediaDownloadMenu.Reset();
        if(dlpCtl.isPlayList(url)){
            cmd.add("--flat-playlist");
            cmd.add("-J");
            cmd.addAll(List.of(configs.getArgs(url)));
            cmd.add(url);
            dlpCtl.fetchPlayListMetadataAsync(cmd,
                    playListInfo -> {
                        playListDownloadMenu.PrepareData(playListInfo);
                    },
                    error -> {
                        status.SetAnimation(StatusPane.AnimationEnum.ERROR, Strings.gIns().ERROR);
                        new StackTraceErrorAlert((ExecutorException) error, Strings.gIns().SEARCH_ERROR, Strings.gIns().SEARCH_ERROR_TIP).show();
                    });
        }else {
            cmd.add("-j");
            cmd.addAll(List.of(configs.getArgs(url)));
            cmd.add(url);
            dlpCtl.fetchMediaMetadataAsync(cmd,
                    mediaInfo -> {
                        status.SetAnimation(StatusPane.AnimationEnum.HIDDEN, null);
                        mediaDownloadMenu.PrepareData(mediaInfo, url);
                    }, error -> {
                        status.SetAnimation(StatusPane.AnimationEnum.ERROR, Strings.gIns().ERROR);
                        new StackTraceErrorAlert((ExecutorException) error, Strings.gIns().SEARCH_ERROR, Strings.gIns().SEARCH_ERROR_TIP).show();
                    });
        }
    }
}
