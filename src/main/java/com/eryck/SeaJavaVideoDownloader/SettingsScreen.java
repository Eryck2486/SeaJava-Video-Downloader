package com.eryck.SeaJavaVideoDownloader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsScreen extends VBox {
    SeaJavaMain main;
    AppConfigs configs;
    StackPane stackPane;
    ObservableList<Node> stackConteiner;

    //Settings properties
    ComboBox<AppConfigs.PlayerClientEnum> CfgFakeCliPicker;
    ComboBox<AppConfigs.BrowserEnum> browserPicker;
    ComboBox<AppConfigs.JSRuntimeEnum> jsRuntime;
    CustomFileChooser cookieFilePath;
    ToggleGroup cookiesModeGroup;
    Slider downloadsLimit;
    CustomFileChooser jsRuntimePath;
    AppConfigs.CookieModeEnum cookieMode;
    CustomFileChooser customDownloadsDir;

    BorderPane TollBar(){
        BorderPane toolBar = new BorderPane();
        Button backBtn = new Button(Strings.gIns().BACK);
        backBtn.setOnAction(actionEvent -> {
            setVisible(false);
            main.setHomeScreen();
        });
        toolBar.setLeft(backBtn);
        Button applyBtn = new Button(Strings.gIns().APPLY);
        applyBtn.setOnAction(actionEvent -> {
            ApplyConfigs();
        });
        toolBar.setRight(applyBtn);
        return toolBar;
    }

    HBox FakeClient(){
        HBox clientsConfigArea = new HBox();
        ObservableList<Node> clientsConfigConteiner = clientsConfigArea.getChildren();
        clientsConfigConteiner.add(new Separator());
        Label cliLabel = new Label(Strings.gIns().FAKE_CLIENT);
        clientsConfigConteiner.add(cliLabel);
        CfgFakeCliPicker = new ComboBox<>();
        CfgFakeCliPicker.getItems().addAll(
                AppConfigs.PlayerClientEnum.NONE,
                AppConfigs.PlayerClientEnum.WEB,
                AppConfigs.PlayerClientEnum.ANDROID,
                AppConfigs.PlayerClientEnum.IOS,
                AppConfigs.PlayerClientEnum.TV
        );
        if(configs.getCustomPlayerClient()!=null)
        {
            CfgFakeCliPicker.setValue(configs.getCustomPlayerClient());
        }
        clientsConfigConteiner.add(CfgFakeCliPicker);
        return clientsConfigArea;
    }

    VBox CookiesArea(){
        VBox coockiesConfigArea = new VBox();
        ObservableList<Node> coockieAreaConteiner = coockiesConfigArea.getChildren();
        coockieAreaConteiner.add(new Separator());
        Label coockieLabel = new Label(Strings.gIns().LOAD_COOKIE_MODE);
        coockieAreaConteiner.add(coockieLabel);
        RadioButton[] coockiesModeButtons = {
                new RadioButton(Strings.gIns().FROM_BROWSER),
                new RadioButton(Strings.gIns().FROM_FILE),
                new RadioButton(Strings.gIns().NO_COOKIES)
        };
        cookieMode=configs.getCookieMode();
        coockiesModeButtons[0].setOnAction(actionEvent -> {
            cookieMode= AppConfigs.CookieModeEnum.FROM_BROWSER;
        });
        coockiesModeButtons[1].setOnAction(actionEvent -> {
            cookieMode= AppConfigs.CookieModeEnum.FROM_FILE;
        });
        coockiesModeButtons[2].setOnAction(actionEvent -> {
            cookieMode= AppConfigs.CookieModeEnum.NONE;
        });


        cookiesModeGroup = new ToggleGroup();
        for(RadioButton btn : coockiesModeButtons) btn.setToggleGroup(cookiesModeGroup);
        HBox browserSelector = new HBox();
        ObservableList<Node> browsersConteiner = browserSelector.getChildren();
        browsersConteiner.add(coockiesModeButtons[0]); //From browser
        browserPicker = new ComboBox<>();
        browserPicker.getItems().addAll(AppConfigs.BrowserEnum.FIREFOX,
                AppConfigs.BrowserEnum.CHROME,
                AppConfigs.BrowserEnum.BRAVE,
                AppConfigs.BrowserEnum.EDGE,
                AppConfigs.BrowserEnum.OPERA,
                AppConfigs.BrowserEnum.VIVALDI
        );

        cookieFilePath=new CustomFileChooser("...", null, main.primaryStage, null);
        cookieFilePath.setPromptText(Strings.gIns().COOKIE_FILE_TIP);
        HBox fileChosserLayout = new HBox();
        cookieFilePath.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cookieFilePath, Priority.ALWAYS);

        ObservableList<Node> fileChosserContainer = fileChosserLayout.getChildren();
        fileChosserContainer.add(coockiesModeButtons[1]); //From file
        fileChosserContainer.add(cookieFilePath);
        Button loginBtn = new Button("Login");
        fileChosserContainer.add(loginBtn);
        loginBtn.setOnAction(actionEvent -> {
            new CustomWebView("https://www.google.com/", "Login", cookieText -> {
                File cookieFile = new File("cookies.txt");
                try (FileWriter writer = new FileWriter(cookieFile);
                     BufferedWriter bw = new BufferedWriter(writer)){
                    bw.write(cookieText);
                    cookieFilePath.setText(cookieFile.getAbsolutePath());
                    ApplyConfigs();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).show();
        });

        if(configs.getCookieMode()!=null){
            switch (configs.getCookieMode()){
                case FROM_BROWSER -> {
                    coockiesModeButtons[0].setSelected(true);
                    if((AppConfigs.BrowserEnum.FIREFOX).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.FIREFOX);
                    else if((AppConfigs.BrowserEnum.CHROME).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.CHROME);
                    else if((AppConfigs.BrowserEnum.BRAVE).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.BRAVE);
                    else if((AppConfigs.BrowserEnum.EDGE).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.EDGE);
                    else if((AppConfigs.BrowserEnum.OPERA).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.OPERA);
                    else if((AppConfigs.BrowserEnum.VIVALDI).getValue().equals(configs.getCookieValue()))
                        browserPicker.setValue(AppConfigs.BrowserEnum.VIVALDI);
                }
                case FROM_FILE -> {
                    coockiesModeButtons[1].setSelected(true);
                    cookieFilePath.setText(configs.getCookieValue());
                }
                default -> coockiesModeButtons[2].setSelected(true);
            }
        }
        browsersConteiner.add(browserPicker);
        coockieAreaConteiner.add(browserSelector);


        coockieAreaConteiner.add(fileChosserLayout);
        coockieAreaConteiner.add(coockiesModeButtons[2]); //No cookies
        return coockiesConfigArea;
    }

    VBox DownloadsArea(){
        VBox downloadRoot = new VBox();
        HBox downloadCountLayout = new HBox(new Label(Strings.gIns().SIMULTANEOUS_DOWN));
        downloadsLimit=new Slider(1, 4, configs.getProcessCount());
        downloadsLimit.setMajorTickUnit(1);
        downloadsLimit.setSnapToTicks(true);
        Label downloadsLimitValue = new Label(String.valueOf(configs.getProcessCount()));
        downloadsLimit.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                downloadsLimitValue.setText(String.valueOf((int)downloadsLimit.getValue()));
            }
        });
        downloadCountLayout.getChildren().addAll(downloadsLimit, downloadsLimitValue);
        downloadRoot.getChildren().add(downloadCountLayout);

        HBox customPathConteiner = new HBox(new Label(Strings.gIns().CUSTOM_DW_PATH));
        String downloadsPath = configs.getCustomDownloadsDir();
        customDownloadsDir = new CustomFileChooser("...", downloadsPath, main.primaryStage, null);
        customDownloadsDir.setPromptText(Strings.gIns().CUSTOM_DW_PATH_TIP);
        HBox.setHgrow(customDownloadsDir, Priority.ALWAYS);
        customDownloadsDir.setMaxWidth(Double.MAX_VALUE);
        customPathConteiner.getChildren().add(customDownloadsDir);

        String currentPath = configs.getCustomDownloadsDir();
        if(currentPath!=null && !currentPath.isEmpty()){
            customDownloadsDir.setText(currentPath);
        }

        downloadRoot.getChildren().add(customPathConteiner);
        return downloadRoot;
    }

    VBox JSRuntimeArea(){
        VBox jsAreaConteiner = new VBox();
        HBox jsRuntimeLayout = new HBox(new Label(Strings.gIns().JS_RUNTIME));
        jsRuntime = new ComboBox<>();
        jsRuntime.getItems().addAll(
                AppConfigs.JSRuntimeEnum.DENO,
                AppConfigs.JSRuntimeEnum.NODE,
                AppConfigs.JSRuntimeEnum.QUICKJS
        );
        jsRuntimeLayout.getChildren().add(jsRuntime);
        jsAreaConteiner.getChildren().add(jsRuntimeLayout);
        if(configs.getJSRuntime()!=null){
            jsRuntime.setValue(configs.getJSRuntime());
        }
        HBox jsDirLayout = new HBox();
        String jsRuntimeDir = configs.getJSRuntimePath();
        jsRuntimePath = new CustomFileChooser("...", jsRuntimeDir, main.primaryStage,  null);
        jsRuntimePath.setPromptText(Strings.gIns().PATH_TIP);
        jsDirLayout.getChildren().addAll(new Label(Strings.gIns().PATH), jsRuntimePath);
        HBox.setHgrow(jsRuntimePath, Priority.ALWAYS);

        if(jsRuntimeDir!=null){
            jsRuntimePath.setText(jsRuntimeDir);
        }
        jsAreaConteiner.getChildren().add(jsDirLayout);
        return  jsAreaConteiner;
    }

    SettingsScreen(SeaJavaMain main){
        this.main=main;
        this.configs=main.configs;
        stackPane=main.root;
        stackConteiner=stackPane.getChildren();
        MakeSettingsScreen();
        stackConteiner.add(this);
    }

    public void MakeSettingsScreen(){
        ObservableList<Node> settingsConteiner = getChildren();
        //Toolbar elements
        settingsConteiner.addAll(TollBar(), new Separator());
        //End toolbar elements

        //Custom fake client area
        settingsConteiner.addAll(FakeClient(), new Separator());
        //End custom fake client area

        //Cookie settings
        settingsConteiner.addAll(CookiesArea(), new Separator());
        //End coockies settings

        //Downloads count config
        settingsConteiner.addAll(DownloadsArea(), new Separator());
        //End downloads count config

        //JS Runtime settings
        settingsConteiner.addAll(JSRuntimeArea(), new Separator());
        //End JS Runtime settings
    }

    public void Set(){
        setVisible(true);
        toFront();
    }

    public void ApplyConfigs() {
        //Fake client mode selection detection
        configs.SetCustomClient(CfgFakeCliPicker.getValue());

        //Coockie mode config
        switch (cookieMode)
        {
            case AppConfigs.CookieModeEnum.FROM_BROWSER:
                configs.SetCookieConfig(AppConfigs.CookieModeEnum.FROM_BROWSER, browserPicker.getValue().getValue());
                break;
            case AppConfigs.CookieModeEnum.FROM_FILE:
                configs.SetCookieConfig(AppConfigs.CookieModeEnum.FROM_FILE, cookieFilePath.getContentAsString());
                break;
            default:
                configs.SetCookieConfig(AppConfigs.CookieModeEnum.NONE, "");
                break;
        }
        configs.setCustomDownloadsPath(customDownloadsDir.getContentAsString());
        AppConfigs.JSRuntimeEnum selectedRuntime = jsRuntime.getValue();
        if(selectedRuntime!=null){
            configs.setJSRuntime(selectedRuntime, jsRuntimePath.getContentAsString());
        }
        configs.setProcess_count((int)downloadsLimit.getValue());
        configs.SaveConfigs();
    }
}
