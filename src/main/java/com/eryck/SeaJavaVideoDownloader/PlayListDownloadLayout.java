package com.eryck.SeaJavaVideoDownloader;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class PlayListDownloadLayout extends VBox {
    public enum QualityPressetEnum{
        BEST_QUALITY(Strings.gIns().BEST_QUALITY),
        DATA_SAVING(Strings.gIns().DATA_SAVING),
        AUDIO_ONLY(Strings.gIns().AUDIO_ONLY_IF_POSSIBLE);
        private final String text;
        QualityPressetEnum(String text){
            this.text=text;
        }

        public String toString(){
            return text;
        }
    }

    static class MediaElement extends VBox {
        MediaElement(PlayListDownloadLayout parent, PlayListInfo.Entry entry, DlpController dlpCtl, AppConfigs configs, int index){
            setVisible(false);
            url=entry.url;
            this.index=index;
            conteiner = new HBox();
            selection = new CheckBox();
            thumbConteiner=new Pane();
            title = new Label(Strings.gIns().LOADING);
            title.setWrapText(false);
            setMaxWidth(Double.MAX_VALUE);
            List<String> cmd = new ArrayList<>();
            cmd.add("-j");
            cmd.addAll(List.of(configs.getArgs(entry.url)));
            cmd.add(entry.url);
            formats = new ComboBox<>();
            dlpCtl.fetchMediaMetadataAsync(cmd,
                    mediaInfo -> {
                        info=mediaInfo;
                        Platform.runLater(() -> {
                            for(MediaInfo.FormatInfo info : mediaInfo.formats){
                                if(info.ext!=null && !info.ext.equalsIgnoreCase("mhtm") && !info.ext.equalsIgnoreCase("mhtml")) {
                                    formats.getItems().add(info);
                                }
                            }
                            formats.setValue(mediaInfo.getBetterFormat());
                            title.setText(mediaInfo.title);
                            thumb = new ImageView(SeaJavaMain.imgLoader(mediaInfo.thumbnail));
                            thumb.setFitHeight(50);
                            thumb.setPreserveRatio(true);
                            thumbConteiner.getChildren().add(thumb);
                        });
                        loaded = true;
                        setVisible(true);
                        parent.rootContent.screenToLocal(parent.rootContent.getHeight(), 0);
                        parent.checkAllReady();
                    }, error -> {
                        failed=true;
                    });
            VBox configArea = new VBox();

            HBox formatConvertArea = new HBox(new Label(Strings.gIns().CONVERT_TO));
            conversionFormats=new ComboBox<>();
            conversionFormats.getItems().addAll(MediaInfo.MediaConvertEnum.values());
            conversionFormats.setValue(MediaInfo.MediaConvertEnum.NO_CONVERSION);
            formatConvertArea.getChildren().add(conversionFormats);

            configArea.getChildren().addAll(title, formats, formatConvertArea);
            BorderPane selectionConteiner = new BorderPane();
            selectionConteiner.setCenter(selection);
            selection.setSelected(true);
            conteiner.getChildren().addAll(selectionConteiner, thumbConteiner, configArea);
            getChildren().addAll(conteiner, new Separator());
            setFillWidth(true);
            selection.setOnAction(actionEvent -> {
                if(!selection.isSelected()) parent.selectAll.setSelected(false);
            });
        }

        CheckBox selection;
        ImageView thumb;
        MediaInfo info;
        boolean loaded = false;
        boolean failed = false;
        boolean downloadEnded;

        HBox conteiner;
        Pane thumbConteiner;
        Label title;
        ComboBox<MediaInfo.FormatInfo> formats;
        ComboBox<MediaInfo.MediaConvertEnum> conversionFormats;
        int index;
        public String url;
        CustomProgressBar progressBar;

        public void SetProgress(double progress, String text){
            if(progressBar==null){
                progressBar=new CustomProgressBar(Strings.gIns().STARTING);
                getChildren().add(progressBar);
            }
            progressBar.setProgress(progress);
            progressBar.setText(text);
        }

        public boolean download()
        {
            return selection.isSelected() && !failed && loaded;
        }

        public void SetDownloadDone(){
            downloadEnded=true;
        }

        public boolean isDownloaded(){
            return downloadEnded;
        }

        public void Reset(){
            downloadEnded=false;
            if(progressBar!=null) SetProgress(-1, Strings.gIns().STARTING);
        }
    }

    AppConfigs configs;
    DlpController dlpCtl;
    Stage stage;
    ScrollPane rootContent;
    VBox conteiner;
    List<MediaElement> medias = new ArrayList<>();
    CustomFileChooser downloadsDir;
    CheckBox selectAll;
    HomeScreen home;

    PlayListDownloadLayout(HomeScreen home, AppConfigs configs, DlpController dlpCtl, Stage stage){
        this.configs=configs;
        this.dlpCtl=dlpCtl;
        this.stage=stage;
        this.home=home;
    }
    VBox downloadArea;

    public void PrepareData(PlayListInfo infos){
        setVisible(false);
        conteiner=new VBox();
        rootContent=new ScrollPane();
        getChildren().addAll(AddHeader());
        getChildren().add(rootContent);
        rootContent.setContent(conteiner);
        for(int i=0; i<infos.entries.size(); i++){
            PlayListInfo.Entry entry = infos.entries.get(i);
            AddElement(entry, i+1);
        }
        AddDownloadOptions();
    }

    public boolean checkAllReady(){
        home.status.toFront();
        for(int i =0; i < medias.size(); i++){
            MediaElement mediaElement = medias.get(i);
            if(!mediaElement.loaded) {
                home.status.SetText(Strings.gIns().LOADING+" "+(i+1)+"/"+medias.size());
                return false;
            }
        }
        home.status.SetAnimation(HomeScreen.StatusPane.AnimationEnum.HIDDEN, "");
        setVisible(true);
        toFront();
        return true;
    }

    VBox AddHeader(){ //Healder of playlist downloader
        VBox optionsList = new VBox();

        BorderPane headerLayout = new BorderPane();
        //Select/desselect all items
        HBox selectAllLayout = new HBox();
        selectAll = new CheckBox();
        selectAll.setSelected(true);
        Label selectAllLabel = new Label(Strings.gIns().SELECT_ALL);
        selectAllLayout.getChildren().addAll(selectAll, selectAllLabel);
        headerLayout.setLeft(selectAllLayout);
        selectAll.setOnAction(actionEvent -> {
            for(MediaElement element : medias){
                element.selection.setSelected(selectAll.isSelected());
            }
        });

        //Select quality preset
        HBox qualityPresetLayout = new HBox();
        Label qualityLabel = new Label(Strings.gIns().QUALITY_PRESET);
        ComboBox<QualityPressetEnum> quality = new ComboBox<>();
        quality.getItems().addAll(QualityPressetEnum.values());
        qualityPresetLayout.getChildren().addAll(qualityLabel, quality);
        headerLayout.setRight(qualityPresetLayout);
        optionsList.getChildren().add(headerLayout);
        quality.setValue(QualityPressetEnum.BEST_QUALITY);
        quality.setOnAction(actionEvent -> {
            for(MediaElement element : medias){
                switch (quality.getValue()){
                    case AUDIO_ONLY -> element.formats.setValue(element.info.getAudioOnlyFormat());
                    case BEST_QUALITY -> element.formats.setValue(element.info.getBetterFormat());
                    case DATA_SAVING -> element.formats.setValue(element.info.getEconomicalFormat());
                }
            }
        });

        //Converssion prefference
        HBox converssionPressetArea = new HBox();
        Label convertLabel = new Label(Strings.gIns().CONVERT_TO);
        ComboBox<MediaInfo.MediaConvertEnum> convert = new ComboBox<>();
        convert.getItems().addAll(MediaInfo.MediaConvertEnum.values());
        converssionPressetArea.getChildren().addAll(convertLabel, convert);
        convert.setValue(MediaInfo.MediaConvertEnum.NO_CONVERSION);
        convert.setOnAction(actionEvent -> {
            for (MediaElement element : medias){
                element.conversionFormats.setValue(convert.getValue());
            }
        });

        optionsList.getChildren().add(converssionPressetArea);
        return optionsList;
    }

    void AddDownloadOptions() {
        if(downloadArea==null){
            downloadArea = new VBox();
            getChildren().add(downloadArea);
        }

        HBox folderSelectorLayout = new HBox();
        Label dowloadFolderL = new Label(Strings.gIns().DOWNLOAD_DIR);
        folderSelectorLayout.getChildren().add(dowloadFolderL);
        File downloadsPath = configs.getDowloadsDir();
        downloadsDir = new CustomFileChooser("...", downloadsPath.getAbsolutePath(), stage, null);
        downloadsDir.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(downloadsDir, Priority.ALWAYS);
        folderSelectorLayout.getChildren().add(downloadsDir);
        downloadsDir.setText(downloadsPath.getAbsolutePath());
        downloadArea.getChildren().add(folderSelectorLayout);

        Button downloadBtn = createDownloadBtn();
        downloadArea.getChildren().add(downloadBtn);
    }

    private Button createDownloadBtn() {
        Button downloadBtn = new Button(Strings.gIns().DOWNLOAD);
        downloadBtn.setMaxWidth(Double.MAX_VALUE);
        downloadBtn.setOnAction(actionEvent -> {
            downloadArea.getChildren().clear();
            for (MediaElement value : medias){
                value.Reset();
            }
            Future<?>[] actions = StartDownload();
            if(actions==null) return;
            Platform.runLater(() -> {
                Button cancelBtn = new Button(Strings.gIns().CANCEL);
                cancelBtn.setOnAction(actionEvent1 -> {
                    for(Future<?> action : actions) action.cancel(true);
                    downloadArea.getChildren().clear();
                    AddDownloadOptions();
                });
                cancelBtn.setMaxWidth(Double.MAX_VALUE);
                downloadArea.getChildren().add(cancelBtn);
            });
        });
        return downloadBtn;
    }

    Future<?>[] StartDownload(){
        Gson gson = new Gson();
        CustomProgressBar downloadStatusBar = new CustomProgressBar("Starting...");
        List<Future> downloadsList = new ArrayList<>();
        File dowloadsDir = downloadsDir.getContent();
        if (downloadsDir==null){
            new Alert(Alert.AlertType.ERROR, Strings.gIns().INSERT_VALID_DW_FOLDER).show();
            return null;
        }
        for(MediaElement value : medias) {
            List<String> args = new java.util.ArrayList<>(List.of(configs.getArgs(value.url)));
            MediaInfo.MediaConvertEnum convert = value.conversionFormats.getValue();
            String format_id = value.formats.getValue().format_id;
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
            if(value.download() && !value.failed){
                downloadsList.add(dlpCtl.StartDownload(dowloadsDir, format_id, value.url, args.toArray(new String[0]), progress -> {
                    if (progress.startsWith("PROGRESS:")) {
                        DlpController.DownloadStatus status = gson.fromJson(progress.substring(9), DlpController.DownloadStatus.class);
                        Platform.runLater(() ->
                        {
                            if(status.eta.contains("N/A")){
                                value.SetProgress(status.getPercent(), Strings.gIns().CONVERTING);
                            }else {
                                value.SetProgress(status.getPercent(), status.getProgressText());
                                UpdateProgressBar(downloadStatusBar);
                            }
                        });
                    }
                }, object ->
                        Platform.runLater(() -> {
                            value.progressBar.setText(Strings.gIns().DONE);
                            value.SetDownloadDone();
                            CheckForAllEnd();
                        })));
            }
        }
        downloadArea.getChildren().add(downloadStatusBar);
        return downloadsList.toArray(new Future<?>[0]);
    }

    void CheckForAllEnd(){
        boolean allend = true;
        for (MediaElement value : medias){
            if((value.progressBar==null || !value.isDownloaded()) && value.download()){
                allend=false;
            }
        }
        if(allend){
            downloadArea.getChildren().clear();
            AddDownloadOptions();
            SeaJavaMain.showDownloadsDone(); //Show downloads done pop-up
        }
    }

    void UpdateProgressBar(CustomProgressBar downloadStatusBar){
        double allProgressValue = 0;
        int totlalDownloads = 0;
        for(MediaElement value : medias){
            if(value.download()) totlalDownloads++;
            if(value.progressBar!=null) {
                double percent = value.progressBar.getProgress();
                allProgressValue += percent;
            }
        }
        double finalPercent = allProgressValue / totlalDownloads;
        downloadStatusBar.setProgress(finalPercent);
        downloadStatusBar.setText(Strings.gIns().DOWNLOADING + finalPercent + "%");
    }

    void AddElement(PlayListInfo.Entry entry, int index){
        try {
            MediaElement media = new MediaElement(this, entry, dlpCtl, configs, index);
            conteiner.getChildren().add(media);
            medias.add(media);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Reset(){
        getChildren().clear();
        medias.clear();
    }

    public void SetFailedScreen(){

    }
}