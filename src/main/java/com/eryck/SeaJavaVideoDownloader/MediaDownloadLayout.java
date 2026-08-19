package com.eryck.SeaJavaVideoDownloader;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public class MediaDownloadLayout extends VBox {
    AppConfigs configs;
    ObservableList<Node> conteiner;
    ComboBox<MediaInfo.FormatInfo> formats;
    ComboBox<MediaInfo.MediaConvertEnum> conversionFormats;
    CustomFileChooser downloadsDir;
    Stage stage;
    VBox downloadArea;
    DlpController dlpCtl;

    MediaDownloadLayout(AppConfigs configs, DlpController dlpCtl, Stage stage, boolean isPlayListElement){
        this.configs = configs;
        this.stage = stage;
        this.dlpCtl = dlpCtl;
        conteiner=getChildren();
    }

    public void PrepareData(MediaInfo mediaData, String url){
        Reset();
        toFront();
        Image thumb = SeaJavaMain.imgLoader(mediaData.thumbnail);
        ImageView thumbView = new ImageView(thumb);
        thumbView.setFitWidth(320);
        thumbView.setFitHeight(320);
        thumbView.setPreserveRatio(true);
        BorderPane thumbConteiner = new BorderPane();
        thumbConteiner.setCenter(thumbView);
        conteiner.add(thumbConteiner);
        conteiner.add(new Separator());
        ShowInfos(mediaData);
        ShowOptions(mediaData, url);
    }

    void ShowInfos(MediaInfo mediaData){
        AddInfo(Strings.gIns().TITLE, mediaData.title);
        AddInfo(Strings.gIns().CHANNEL, mediaData.uploader);
    }

    void ShowOptions(MediaInfo mediaData, String url){
        //Format Selector
        HBox lineInfo = new HBox();
        ObservableList<Node> lineConteiner = lineInfo.getChildren();
        Label formatL = new Label(Strings.gIns().FORMAT);
        formats=new ComboBox<>();
        for(MediaInfo.FormatInfo info : mediaData.formats){
            if(info.ext!=null && !info.ext.equalsIgnoreCase("mhtm") && !info.ext.equalsIgnoreCase("mhtml") && (info.fps==null || info.fps>0)) {
                formats.getItems().add(info);
            }
        }
        formats.setValue(mediaData.getBetterFormat());
        lineConteiner.add(formatL);
        lineConteiner.add(formats);

        HBox converssionLine = new HBox(new Label(Strings.gIns().CONVERT_TO));
        conversionFormats=new ComboBox<>();
        conversionFormats.getItems().addAll(MediaInfo.MediaConvertEnum.values());
        conversionFormats.setValue(MediaInfo.MediaConvertEnum.NO_CONVERSION);
        converssionLine.getChildren().add(conversionFormats);
        conteiner.addAll(lineInfo, converssionLine);
        //End Format Selector

        //Downloads folder selector
        HBox downloadsArea = new HBox();
        ObservableList<Node> downloadsConteiner = downloadsArea.getChildren();
        Label dowloadFolderL = new Label(Strings.gIns().DOWNLOAD_DIR);
        downloadsConteiner.add(dowloadFolderL);

        File downloadsPath = configs.getDowloadsDir();
        downloadsDir = new CustomFileChooser("...", downloadsPath.getAbsolutePath(), stage, null);
        downloadsDir.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(downloadsDir, Priority.ALWAYS);
        downloadsDir.setText(downloadsPath.toString());
        downloadsConteiner.add(downloadsDir);

        conteiner.add(downloadsArea);
        conteiner.add(new Separator());
        //End Downloads folder selector

        //Download button
        downloadArea = new VBox();
        Button downloadBtn = new Button(Strings.gIns().DOWNLOAD);
        downloadBtn.setMaxWidth(Double.MAX_VALUE);
        downloadBtn.setOnAction(actionEvent -> {
            Gson gson = new Gson();
            downloadArea.getChildren().clear();
            CustomProgressBar downloadStatusBar = new CustomProgressBar(Strings.gIns().STARTING);
            downloadStatusBar.setMaxWidth(Double.MAX_VALUE);
            downloadArea.getChildren().add(downloadStatusBar);

            List<String> args = new java.util.ArrayList<>(List.of(configs.getArgs(url)));
            MediaInfo.MediaConvertEnum convert = conversionFormats.getValue();
            String format_id = formats.getValue().format_id;
            if(convert != MediaInfo.MediaConvertEnum.NO_CONVERSION){
                args.add("--ffmpeg-location");
                args.add(new File("deps/bin").getAbsolutePath());
                if(convert.isAudio()){
                    args.add("-x");
                    args.add("--audio-quality");
                    args.add("0");
                    format_id="";
                }
                args.add(convert.getTag());
                args.add(convert.getExt());
            }
            if (downloadsDir==null){
                new Alert(Alert.AlertType.ERROR, Strings.gIns().INSERT_VALID_DW_FOLDER).show();
                return;
            }
            Future<?> action =
                    dlpCtl.StartDownload(downloadsDir.getContent(), format_id, url, args.toArray(new String[0]), progress -> {
                    if (progress.startsWith("PROGRESS:")) {
                        DlpController.DownloadStatus status = gson.fromJson(progress.substring(9), DlpController.DownloadStatus.class);
                            Platform.runLater(() ->
                            {
                                downloadStatusBar.setProgress(status.getPercent());
                                downloadStatusBar.setText(status.getProgressText());
                            });
                        }
                    }, object ->
                        Platform.runLater(() -> {
                            downloadArea.getChildren().clear();
                            downloadArea.getChildren().add(downloadBtn);
                            SeaJavaMain.showDownloadsDone(); //Show downloads done pop-up
                        }));
            Button cancelBtn = new Button(Strings.gIns().CANCEL);
            cancelBtn.setOnAction(actionEvent1 -> {
                action.cancel(true);
            });
            cancelBtn.setMaxWidth(Double.MAX_VALUE);
            downloadArea.getChildren().add(cancelBtn);
        });
        downloadArea.getChildren().add(downloadBtn);
        conteiner.add(downloadArea);
        //End Download button
    }

    void AddInfo(String infoKeyStr, String infoValueStr) {
        HBox lineInfo = new HBox();
        ObservableList<Node> lineConteiner = lineInfo.getChildren();

        Label infoKey = new Label(infoKeyStr+infoValueStr);
        infoKey.setWrapText(true);
        lineConteiner.add(infoKey);

        conteiner.add(lineInfo);
        conteiner.add(new Separator());
    }

    public void Reset(){
        conteiner.clear();
    }
}