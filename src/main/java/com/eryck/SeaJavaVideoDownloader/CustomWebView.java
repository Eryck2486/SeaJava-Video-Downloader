package com.eryck.SeaJavaVideoDownloader;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CustomWebView extends Stage {

    Button saveButton;
    WebView webView;
    TextField websiteUrl;

    CustomWebView(String url, String title, Consumer<String> onSave){
        super();
        websiteUrl = new TextField(url);
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        setTitle(title);
        VBox webViewConteiner = new VBox();
        WebView webV = createWebViewContent(url);
        HBox toolBar = createWebviewTopBar(url, onSave);
        webViewConteiner.getChildren().addAll(toolBar, webV);
        Scene scene = new Scene(webViewConteiner, 900, 600);
        setScene(scene);
    }

    public static String getCookies(String pageUrl) {
        CookieHandler handler = CookieHandler.getDefault();
        String cookieText = "";
        if (!(handler instanceof CookieManager)) {
            System.err.println("CookieManager não está ativo no ambiente JavaFX.");
            return cookieText;
        }
        CookieManager cookieManager = (CookieManager) handler;
        try {
            URI uri = URI.create(pageUrl);
            Map<String, List<String>> headers = cookieManager.get(uri, Map.of());
            List<HttpCookie> cookies = cookieManager.getCookieStore().get(uri);
            StringWriter stw = new StringWriter();
            try (BufferedWriter writer = new BufferedWriter(stw)) {
                // Cabeçalho obrigatório do formato Netscape para o yt-dlp
                writer.write("# Netscape HTTP Cookie File\n");
                writer.write("# https://curl.haxx.se/rfc/cookie_spec.html\n");
                writer.write("# This is a generated file!  Do not edit.\n\n");
                String domain = uri.getHost();
                if (domain == null) return cookieText;
                // Garante que o domínio comece com ponto para cobrir subdomínios se necessário
                String formattedDomain = domain.startsWith(".") ? domain : "." + domain;
                for (HttpCookie cookie : cookies) {
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    String path = cookie.getPath() != null ? cookie.getPath() : "/";
                    boolean secure = cookie.getSecure();
                    // Se o cookie não tiver expiração definida, define um timestamp futuro padrão (1 ano)
                    long maxAge = cookie.getMaxAge();
                    long expires = (maxAge > 0) ? (System.currentTimeMillis() / 1000 + maxAge) : (System.currentTimeMillis() / 1000 + 31536000);

                    // Estrutura das 7 colunas separadas por TAB (\t):
                    // Domain | Include subdomains | Path | Secure | Expiration | Name | Value
                    writer.write(String.format("%s\tTRUE\t%s\t%s\t%d\t%s\t%s\n",
                            formattedDomain,
                            path,
                            secure ? "TRUE" : "FALSE",
                            expires,
                            name,
                            value
                    ));
                }
            }
            cookieText=stw.toString();
        } catch (IOException e) {
        }
        return cookieText;
    }

    HBox createWebviewTopBar(String url, Consumer<String> onSave){
        HBox barConteiner = new HBox();
        websiteUrl.onKeyPressedProperty().setValue( keyEvent -> {
            if(keyEvent.getCode()== KeyCode.ENTER){
                String siteURL = websiteUrl.getText();
                if(!siteURL.startsWith("https://")){
                    if(siteURL.startsWith("http://")){
                        siteURL= "https://" + siteURL.substring(7);
                    }else {
                        siteURL = "https://"+siteURL;
                    }
                }
                webView.getEngine().load(siteURL);
            }
        });
        websiteUrl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(websiteUrl, Priority.ALWAYS);
        Button backButton = new Button("<");
        WebHistory history = webView.getEngine().getHistory();
        backButton.setOnAction(actionEvent -> {
            if (history.getCurrentIndex() > 0) {
                history.go(-1);
            }
        });

        Button reloadBtn = new Button("Reload");
        reloadBtn.setOnAction( actionEvent -> {
            webView.getEngine().reload();
        });

        saveButton=new Button("Save");
        saveButton.setOnAction(actionEvent -> {
            onSave.accept(getCookies(websiteUrl.getText()));
        });

        barConteiner.getChildren().addAll(backButton, websiteUrl, reloadBtn, saveButton);
        return barConteiner;
    }


    WebView createWebViewContent(String url){
        webView = new WebView();
        WebEngine engine = webView.getEngine();
        initModality(Modality.APPLICATION_MODAL);
        engine.locationProperty().addListener((observable, oldURL, newURL) -> {
            if(newURL!=null){
                websiteUrl.setText(newURL);
            }
        });
        engine.load(url);
        return webView;
    }
}