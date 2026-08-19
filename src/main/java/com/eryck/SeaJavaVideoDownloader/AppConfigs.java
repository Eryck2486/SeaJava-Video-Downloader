package com.eryck.SeaJavaVideoDownloader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppConfigs {
    private PlayerClientEnum custom_player_client = PlayerClientEnum.NONE;
    private CookieModeEnum coockie_mode = CookieModeEnum.NONE;
    private JSRuntimeEnum js_runtime = JSRuntimeEnum.QUICKJS;
    private String js_runtime_path = "";
    private String coockie_value = "";
    private String custom_downloads_dir = "";
    private String ffmpeg_path = "";
    private int process_count = 4;
    private Strings.LanguagesEnum user_language;

    public static AppConfigs ReadConfig(){
        Gson gson = new Gson();
        File configFile = new File("config.json");
        String json = "";
        if(configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile);
            BufferedReader reader = new BufferedReader(fileReader)){
                StringBuilder st = new StringBuilder();
                String line;
                while ((line=reader.readLine())!=null) {
                    st.append(line);
                }
                json=st.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!json.isEmpty()){
            return gson.fromJson(json, AppConfigs.class);
        }
        return new AppConfigs();
    }

    public String ToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void SaveConfigs() {
        File configFile = new File("config.json");
        String json = ToJson();
        if(!json.isEmpty()){
            if(!configFile.exists()) {
                try {
                    if(configFile.createNewFile()) return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                FileWriter writer = new FileWriter(configFile);
                writer.write(json);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else System.out.println("Json vazio");
    }

    public File getDowloadsDir(){
        if(custom_downloads_dir!=null && !custom_downloads_dir.isEmpty()) {
            File customDownloadsDir = new File(custom_downloads_dir);
            if (customDownloadsDir.exists()) return customDownloadsDir;
        }

        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");

        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            return downloadsDir;
        }

        return new File(userHome);
    }

    public enum WebSiteEnum {
        @SerializedName("0")
        YOU_TUBE(0, "youtube.com", "YouTube"),
        @SerializedName("1")
        TWITCH(1, "twitch.tv", "Twitch"),
        @SerializedName("2")
        RUMBLE(2, "rumble.com", "Rumble");

        private final int id;
        private final String name;
        private final String platform;

        WebSiteEnum(int id, String platform, String name)
        {
            this.id = id;
            this.platform=platform;
            this.name = name;
        }

        public int getID(){
            return  id;
        }

        public String toString()
        {
            return name;
        }

        public String getPlatform(){
            return platform;
        }
    }

    public enum BrowserEnum {
        @SerializedName("0")
        FIREFOX(0, "firefox", "Firefox"),
        @SerializedName("1")
        CHROME(1, "chrome", "Google Chrome"),
        @SerializedName("2")
        BRAVE(2, "brave", "Brave"),
        @SerializedName("3")
        EDGE(3, "edge", "Microsoft Edge"),
        @SerializedName("4")
        OPERA(4, "opera", "Opera"),
        @SerializedName("5")
        VIVALDI(5, "vivaldi", "Vivaldi");


        private final int id;
        private final String value;
        private final String name;
        BrowserEnum(int id, String value, String name){
            this.id=id;
            this.value=value;
            this.name=name;
        }

        public String getValue(){
            return value;
        }

        public String toString(){
            return name;
        }
    }

    public enum CookieModeEnum {
        @SerializedName("0")
        NONE(0, "none", "No cookies"),
        @SerializedName("1")
        FROM_BROWSER(1, "browser", "From browse"),
        @SerializedName("2")
        FROM_FILE(2, "file", "From file");

        private final int id;
        private final String value;
        private final String name;
        CookieModeEnum(int id, String value, String name)
        {
            this.id = id;
            this.value = value;
            this.name=name;
        }

        public int getID(){
            return  id;
        }

        public String getValue()
        {
            return value;
        }

        public String toString(){
            return name;
        }
    }

    public enum PlayerClientEnum {
        @SerializedName("0")
        NONE(0, "none", "default"),
        @SerializedName("1")
        ANDROID(1, "android", "Android"),
        @SerializedName("2")
        IOS(2, "ios", "Apple IOS"),
        @SerializedName("3")
        TV(3, "tv", "TV"),
        @SerializedName("4")
        WEB(4, "web", "WEB");

        private final int id;
        private final String value;
        private final String name;
        PlayerClientEnum(int id, String value, String name)
        {
            this.id=id;
            this.value=value;
            this.name=name;
        }

        public String toString(){
            return name;
        }
    }

    public enum JSRuntimeEnum {
        @SerializedName("0")
        DENO(0, "deno","Deno"),
        @SerializedName("1")
        NODE(1, "node","NodeJS"),
        @SerializedName("2")
        QUICKJS(2, "quickjs","Quick JS");

        private final String name;
        private final String value;
        private final int id;
        JSRuntimeEnum(int id, String value, String name){
            this.id=id;
            this.value=value;
            this.name=name;
        }

        public String getValue(){
            return value;
        }

        public String toString() {
            return name;
        }
    }

    public String[] getArgs(String url)
    {
        WebSiteEnum websiteNum = null;
        for(WebSiteEnum chkPlatform : WebSiteEnum.values()){
            if(url.contains(chkPlatform.getPlatform())){
                websiteNum=chkPlatform;
                break;
            }
        }
        List<String> args = new ArrayList<>();
        args.add("--trim-filenames");
        args.add("120");
        if(websiteNum!=null) LoadExtractorArgs(args, websiteNum);
        LoadJSRuntime(args);
        LoadCookiesArgs(args);
        LoadFFMpegLocation(args);
        return args.toArray(new String[0]);
    }

    void LoadFFMpegLocation(List<String> args){
        args.add("--ffmpeg-location");
        if(ffmpeg_path.isEmpty()) args.add("deps/bin");
    }

    void LoadJSRuntime(List<String> args){
        args.add("--js-runtimes");
        StringBuilder st = new StringBuilder(js_runtime.getValue());
        if(!js_runtime_path.isEmpty()){
            st.append(':').append(js_runtime_path);
        }
        args.add(st.toString());
    }

    void LoadExtractorArgs(List<String> args, WebSiteEnum webSite) {
        StringBuilder sb = new StringBuilder();
        boolean containsExtrcatorArgs = false;
        if(custom_player_client!=PlayerClientEnum.NONE)
        {
            containsExtrcatorArgs=true;
            sb.append("player_client=");
            sb.append(custom_player_client.value);
        }
        if(containsExtrcatorArgs){
            args.add("--extractor-args");
            String platformPrefix="";
            switch (webSite)
            {
                case YOU_TUBE ->
                        platformPrefix="youtube:";
                case TWITCH ->
                        platformPrefix="twitch:";
            }
            args.add(platformPrefix+sb);
        }
    }

    void LoadCookiesArgs(List<String> args){
        if(custom_player_client==PlayerClientEnum.NONE ||
            custom_player_client==PlayerClientEnum.IOS
        ) {
            switch (coockie_mode) {
                case FROM_FILE -> {
                    args.add("--cookies");
                    args.add(coockie_value);
                }
                case FROM_BROWSER -> {
                    args.add("--cookies-from-browser");
                    args.add(coockie_value);
                }
            }
        }
    }

    public void SetCookieConfig(CookieModeEnum mode, String value){
        coockie_mode = mode;
        coockie_value = value;
    }

    public CookieModeEnum getCookieMode(){
        return coockie_mode;
    }

    public String getCookieValue(){
        return coockie_value;
    }
    public void SetCustomClient(AppConfigs.PlayerClientEnum client)
    {
        custom_player_client=client;
    }

    public PlayerClientEnum getCustomPlayerClient(){
        return custom_player_client;
    }

    public void setProcess_count(int count){
        process_count=count;
    }

    public int getProcessCount(){
        return process_count;
    }

    public void setJSRuntime(JSRuntimeEnum runtime, String jsRuntimePath){
         js_runtime=runtime;
         js_runtime_path=jsRuntimePath;
    }

    public JSRuntimeEnum getJSRuntime(){
        return js_runtime;
    }

    public String getJSRuntimePath(){
        return js_runtime_path;
    }

    public void setUserLanguage(Strings.LanguagesEnum language){
        this.user_language=language;
    }

    public Strings.LanguagesEnum getUserLanguage(){
        return user_language;
    }

    public void setFfmpegPath(String path){
        ffmpeg_path=path;
    }

    public String getFFMpegPath(){
        return ffmpeg_path;
    }

    public void setCustomDownloadsPath(String downloads_dir){
        custom_downloads_dir=downloads_dir;
    }

    public String getCustomDownloadsDir(){
        return custom_downloads_dir;
    }
}
