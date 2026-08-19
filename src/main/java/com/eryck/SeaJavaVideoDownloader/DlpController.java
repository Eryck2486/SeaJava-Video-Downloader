package com.eryck.SeaJavaVideoDownloader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class DlpController
{
    public static class ExecState{
        StringBuilder Output;
        StringBuilder ErrorOutput;
        int ExitStatus;
        Consumer<String> OnNewLine;
        ExecState(Consumer<String> OnNewLine){
            Output = new StringBuilder();
            ErrorOutput =new StringBuilder();
            this.OnNewLine = OnNewLine;
        }
        public void AddLine(String line){
            Output.append(line);
            if(OnNewLine!=null) OnNewLine.accept(line);
        }

        public void AddErrorLine(String line){
            System.out.println(line);
            ErrorOutput.append(line);
        }

        public void SetExitStatus(int exitStatus){
            ExitStatus = exitStatus;
        }

        int getExitStatus(){
            return ExitStatus;
        }

        String getOutput(){
            return Output.toString();
        }
        String getErrorOutput(){
            return ErrorOutput.toString();
        }
    }

    public static class DownloadStatus{
        @SerializedName("percent")
        public String percent;
        @SerializedName("speed")
        public String speed;
        @SerializedName("eta")
        public String eta;

        public String getProgressText(){
            return Strings.gIns().DW_STATUS_DOWLOADING +
                    percent +
                    " | " +
                    speed +
                    " | Eta: " +
                    eta;
        }

        public double getPercent(){
            return Double.parseDouble(percent.replace("%",""));
        }
    }

    String dlpbinary;
    Gson gson;
    private ExecutorService threadPool;
    AppConfigs configs;
    public DlpController(String ytdlpExec, AppConfigs configs){
        this.configs=configs;
        if(ytdlpExec.isEmpty()) SetDlpBinaryAndSetQuickJS();
        else dlpbinary = ytdlpExec;
        threadPool = Executors.newFixedThreadPool(configs.getProcessCount());
        gson = new Gson();
    }

    ExecState Executor(List<String> cmd, Consumer<String> onNewLine) throws ExecutorException {
        ExecState state = new ExecState(onNewLine);
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.environment().put("PYTHONIOENCODING", "utf-8");
            pb.environment().put("PYTHONUTF8", "1");
            System.out.println(Strings.gIns().RUNNING_CMD+String.join(" ", cmd));
            process = pb.start();
            final Process activeProcess = process;
            Thread errorThread = new Thread(() -> {
                try (BufferedReader errReader = activeProcess.errorReader()) {
                    String errLine;
                    while ((errLine = errReader.readLine()) != null) {
                        state.AddErrorLine(errLine);
                    }
                } catch (IOException ignored) {}
            });
            errorThread.setDaemon(true);
            errorThread.start();

            try (BufferedReader reader = process.inputReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    state.AddLine(line);
                }
            }
            state.SetExitStatus(process.waitFor());
            errorThread.join();
            if(state.getExitStatus()!=0) throw new ExecutorException("Exec command error.", cmd, state.getExitStatus(), state.getErrorOutput(), new IOException());
        }catch (InterruptedException e){
            if (process != null && process.isAlive()) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
            Thread.currentThread().interrupt();
        }catch (IOException e){
            throw new ExecutorException("Exec command error.", cmd, state.getExitStatus(), state.getErrorOutput(), e);
        }
        return state;
    }

    public Future<?> StartDownload(File downloadsFolder, String formatID, String url, String[] exArgs, Consumer<String> OnNelLine, Consumer<?> OnFinish)
    {
        return threadPool.submit(() -> {
            String outputTemplate = downloadsFolder.getAbsolutePath()+"/%(title)s.%(ext)s";
            List<String> cmd = new ArrayList<>();
            cmd.add(dlpbinary);
            cmd.addAll(List.of(exArgs));
            if(!formatID.isEmpty()){ //Adding best format for midia
                cmd.add("-f");
                boolean formatted = false;
                String formatCleanned = formatID;
                while (!formatted) {
                    try {
                        Long.parseLong(formatCleanned);
                        formatted=true;
                    } catch (NumberFormatException e) {
                        formatCleanned=formatCleanned.substring(0, formatCleanned.length() - 1);
                    }
                }
                StringBuilder formatBuilder = new StringBuilder(formatCleanned);
                formatBuilder.append("+ba/")
                        .append(formatCleanned)
                        .append("/b");
                cmd.add(formatBuilder.toString());
            }
            cmd.addAll(List.of(new String[]{
                    "-o",
                    outputTemplate,
                    "--newline",
                    "--progress-template",
                    "PROGRESS:{'percent':'%(progress._percent_str)s','speed':'%(progress._speed_str)s','eta':'%(progress._eta_str)s'}",
                    url
            }));
            ExecState state = null;
            try {
                state=Executor(cmd, OnNelLine);
            } catch (ExecutorException e) {
                if(state!=null){
                    Platform.runLater(() ->{
                        new StackTraceErrorAlert(
                                e,
                                "Update error!",
                                "A error occurred during update of yt-dlp");
                        Reset();
                    });
                }
            }
            OnFinish.accept(null);
        });
    }

    public void TryUpdate(){

        threadPool.submit (() -> {
            List<String> cmd = List.of(new String[]{dlpbinary, "-U"});
            ExecState state = null;
            try {
                state=Executor(cmd, null);
            } catch (ExecutorException e) {
                if(state!=null){
                    Platform.runLater(() ->{
                        new StackTraceErrorAlert(
                                e,
                                "Update error!",
                                "A error occurred during update of yt-dlp");
                    });
                }
            }
        });
    }

    public void fetchMediaMetadataAsync(List<String> args, Consumer<MediaInfo> onSucess, Consumer<Throwable> onFail)
    {
        Task<MediaInfo> task = new Task<>() {
            @Override
            protected MediaInfo call() throws ExecutorException {
                List<String> fullCmd = new ArrayList<>();
                fullCmd.add(dlpbinary);
                fullCmd.addAll(args);
                ExecState Exec = Executor(fullCmd, null);
                try {
                    return gson.fromJson(Exec.getOutput(), MediaInfo.class);
                }catch (com.google.gson.JsonSyntaxException e){
                    throw new ExecutorException("JSON parse error!"
                    ,fullCmd,
                            Exec.getExitStatus(),
                            Exec.getErrorOutput(),
                            e);
                }
            }
        };
        task.setOnSucceeded(e -> onSucess.accept(task.getValue()));
        task.setOnFailed(e -> onFail.accept(task.getException()));
        threadPool.submit(task);
    }

    public boolean isPlayList(String url){
        return url.contains("playlist?");
    }

    public void fetchPlayListMetadataAsync(List<String> args, Consumer<PlayListInfo> onSucess, Consumer<Throwable> onFail){
        Task<PlayListInfo> task = new Task<>() {
            @Override
            protected PlayListInfo call() throws ExecutorException {
                List<String> fullCmd = new ArrayList<>();
                fullCmd.add(dlpbinary);
                fullCmd.addAll(args);
                ExecState Exec = Executor(fullCmd, null);
                try {
                    return gson.fromJson(Exec.getOutput(), PlayListInfo.class);
                }catch (com.google.gson.JsonSyntaxException e){
                    throw new ExecutorException("JSON parse error!"
                            ,fullCmd,
                            Exec.getExitStatus(),
                            Exec.getErrorOutput(),
                            e);
                }
            }
        };
        task.setOnSucceeded(e -> onSucess.accept(task.getValue()));
        task.setOnFailed(e -> onFail.accept(task.getException()));
        threadPool.submit(task);
    }

    void SetDlpBinaryAndSetQuickJS()
    {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            dlpbinary = "deps/bin/yt-dlp.exe";
            if(configs.getJSRuntimePath()==null || configs.getJSRuntimePath().isEmpty()) {
                configs.setJSRuntime(AppConfigs.JSRuntimeEnum.QUICKJS,"deps/bin/qjs-windows-x86_64.exe");
                configs.SaveConfigs();
            }
        }else if (os.contains("nux") || os.contains("nix") || os.contains("aix")){
            if(configs.getJSRuntimePath()==null || configs.getJSRuntimePath().isEmpty()){
                configs.setJSRuntime(AppConfigs.JSRuntimeEnum.QUICKJS,"deps/bin/qjs-linux-x86_64");
                configs.SaveConfigs();
            }
            dlpbinary = "deps/bin/yt-dlp_linux";
        }
    }

    public void Abort(){
        threadPool.shutdownNow();
    }

    public void Reset(){
        Abort();
        threadPool=Executors.newFixedThreadPool(configs.getProcessCount());
    }
}
