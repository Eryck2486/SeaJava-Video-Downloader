package com.eryck.SeaJavaVideoDownloader;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceErrorAlert extends Alert {

    public StackTraceErrorAlert(ExecutorException error, String title, String msg) {
        super(AlertType.ERROR);
        setTitle(title);
        setContentText(msg);
        TextArea traceArea = new TextArea();
        traceArea.setEditable(false);
        traceArea.setWrapText(true);
        double max = Double.MAX_VALUE;
        traceArea.setMaxHeight(max);
        traceArea.setMaxWidth(max);
        traceArea.setText(getStackTraceAsString(error)+"\n"+error.getFormattedDetails());
        GridPane.setHgrow(traceArea, Priority.ALWAYS);
        GridPane.setVgrow(traceArea, Priority.ALWAYS);
        GridPane expandPane = new GridPane();
        expandPane.setMaxWidth(Double.MAX_VALUE);
        expandPane.add(new Label(Strings.gIns().DETAILS_ERR), 0, 0);
        expandPane.add(traceArea, 0, 1);
        getDialogPane().setExpandableContent(expandPane);
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
