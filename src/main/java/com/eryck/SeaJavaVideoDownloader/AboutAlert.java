package com.eryck.SeaJavaVideoDownloader;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AboutAlert extends Alert {

    public AboutAlert(String appName, String appVersion, String authorName) {
        super(AlertType.INFORMATION);
        setTitle(Strings.gIns().ABOUT + " " + appName);
        setHeaderText(appName + " v" + appVersion);

        String mainText = String.format(
                Strings.gIns().DEV_BY_FORMAT,
                authorName
        );
        setContentText(mainText);

        String creditsText = buildCreditsText();

        TextArea creditsArea = new TextArea(creditsText);
        creditsArea.setEditable(false);
        creditsArea.setWrapText(true);
        creditsArea.setMaxWidth(Double.MAX_VALUE);
        creditsArea.setMaxHeight(Double.MAX_VALUE);

        creditsArea.setPrefRowCount(10);

        GridPane.setHgrow(creditsArea, Priority.ALWAYS);
        GridPane.setVgrow(creditsArea, Priority.ALWAYS);

        GridPane expandPane = new GridPane();
        expandPane.setMaxWidth(Double.MAX_VALUE);
        expandPane.setVgap(5);
        expandPane.add(new Label(Strings.gIns().TP_LIBS_LICENCES), 0, 0);
        expandPane.add(creditsArea, 0, 1);

        getDialogPane().setExpandableContent(expandPane);
    }

    private String buildCreditsText() {
        return Strings.gIns().getLangRes().getCreditsText();
    }
}