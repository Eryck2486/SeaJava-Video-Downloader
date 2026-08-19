package com.eryck.SeaJavaVideoDownloader;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class CustomProgressBar extends StackPane {
    ProgressBar progressBar;
    Label progressText;
    CustomProgressBar(String text)
    {
        progressBar=new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressText=new Label(text);
        getChildren().addAll(progressBar, progressText);
    }

    public void setText(String text){
        progressText.setText(text);
    }

    public void setProgress(double progress){
        progressBar.setProgress(progress/100.0);
    }

    public double getProgress(){
        return progressBar.getProgress()*100.0;
    }

    public String getText(){
        return progressText.getText();
    }
}