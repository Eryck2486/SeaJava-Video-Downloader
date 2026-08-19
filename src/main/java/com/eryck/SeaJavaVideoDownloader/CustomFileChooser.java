package com.eryck.SeaJavaVideoDownloader;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class CustomFileChooser extends HBox {
    TextField pathArea;
    Button chooserBtn;
    boolean isDirectory = false;

    CustomFileChooser(String text, String startDir, Stage stage, String[] extension){
        super();
        pathArea = new TextField(startDir);
        pathArea.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(pathArea, Priority.ALWAYS);
        FileChooser.ExtensionFilter tmpfilter = null;
        if(extension!=null) tmpfilter = new FileChooser.ExtensionFilter(extension[0], extension[1]);
        FileChooser.ExtensionFilter filter = tmpfilter;
        isDirectory = (filter==null);
        chooserBtn = new Button("...");
        if(startDir!=null) ChangePath(new File(startDir));
        if(isDirectory){
            if(text==null || text.isEmpty()){
                chooserBtn.setText(Strings.gIns().SELECT_FOLDER);
            }else chooserBtn.setText(text);
            chooserBtn.setOnAction(actionEvent -> {
                DirectoryChooser folderChooser = new DirectoryChooser();
                if(selectedFile()!=null && selectedFile().isDirectory()) folderChooser.setInitialDirectory(selectedFile());
                File selected = folderChooser.showDialog(stage);
                if(selected!=null){
                    ChangePath(selected);
                }
            });
        } else {
            if(text==null || text.isEmpty()){
                chooserBtn.setText(Strings.gIns().SELECT_FILE);
            }else chooserBtn.setText(text);
            chooserBtn.setOnAction(actionEvent -> {
                FileChooser fileChooser = new FileChooser();
                if(selectedFile()!=null) fileChooser.setInitialDirectory(selectedFile());
                fileChooser.setSelectedExtensionFilter(filter);
                File selected = fileChooser.showOpenDialog(stage);
                if(selected!=null){
                    ChangePath(selected);
                }
            });
        }
        setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if(db.hasFiles()){
                ChangePath(db.getFiles().getFirst());
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
            dragEvent.consume();
        });
        getChildren().addAll(pathArea,chooserBtn);

    }

    File selectedFile(){
        return new File(pathArea.getText());
    }

    private void ChangePath(File file){
        if(isDirectory && file.isDirectory()) {
            pathArea.setText(file.getAbsolutePath());
        }else if(!isDirectory && file.isFile()) {
            pathArea.setText(file.getAbsolutePath());
        }
    }

    public void setText(String text){
        pathArea.setText(text);
    }

    public File getContent(){
        return selectedFile();
    }

    String getContentAsString(){
        return pathArea.getText();
    }

    public void setPromptText(String text){
        pathArea.setPromptText(text);
    }
}
