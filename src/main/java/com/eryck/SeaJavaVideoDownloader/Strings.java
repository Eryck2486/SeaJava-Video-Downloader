package com.eryck.SeaJavaVideoDownloader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class Strings {
    private static Strings instance;
    public static String langsPath = "/com/eryck/SeaJavaVideoDownloader/languages/";
    public enum StringsEnum{

    }

    private LanguagesEnum langResource;
    private Strings(LanguagesEnum langResource){
        setLangRes(langResource);
    }

    private Strings(){
    }

    private void setLangRes(LanguagesEnum langResource){
        this.langResource=langResource;
    }

    public LanguagesEnum getLangRes(){
        return langResource;
    }

    public enum LanguagesEnum{
        @SerializedName("en-us")
        EN_US("en-US.json", "credits-en-US.txt", "en-US", "English United States"),
        @SerializedName("pt-br")
        PT_BR("pt-BR.json", "credits-pt-BR.txt","pt-BR", "Portugês do Brasil");
        private final String fileName;
        private final String creditsFileName;
        private final String langName;
        private final String BCP47;
        LanguagesEnum(String fileName, String aboutFileName, String BCP47, String langName){
            this.fileName =fileName;
            this.creditsFileName =aboutFileName;
            this.BCP47=BCP47;
            this.langName =langName;
        }

        public String toString(){
            return langName;
        }

        public String getJson(){
            String result = "";
            try (InputStream in = SeaJavaMain.class.getResourceAsStream(langsPath+fileName)) {
                if (in == null) {
                    return result;
                }
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    StringBuilder st = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null) {
                        st.append(line);
                    }
                    result=st.toString();
                }
            } catch (Exception ignored) {
            }
            return result;
        }
        public String getTag(){
            return BCP47;
        }

        public String getCreditsText(){
            String result = "";
            try (InputStream in = SeaJavaMain.class.getResourceAsStream(langsPath+ creditsFileName)) {
                if (in == null) {
                    return result;
                }
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    StringBuilder st = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null) {
                        st.append(line);
                    }
                    result=st.toString();
                }
            } catch (Exception ignored) {
            }
            return result;
        }
    }

    public static synchronized Strings gIns(AppConfigs... configs){ //Simplified getInstance
        if(instance==null){
            if(configs!=null){
                Strings.LanguagesEnum userLang = configs[0].getUserLanguage();
                if(userLang!=null){
                    instance=getLanguage(userLang);
                }else{
                    String tagLanguage = Locale.getDefault().toLanguageTag();
                    if(tagLanguage.equals(LanguagesEnum.EN_US.getTag())) instance = new Strings(LanguagesEnum.EN_US);
                    for (Strings.LanguagesEnum lang : Strings.LanguagesEnum.values()){
                        if(lang.getTag().equals(tagLanguage)){
                            configs[0].setUserLanguage(lang);
                            configs[0].SaveConfigs();
                            instance=getLanguage(lang);
                        }
                    }
                }
            }else instance=new Strings(LanguagesEnum.EN_US);
        }
        return instance;
    }

    public static Strings getLanguage(Strings.LanguagesEnum ulang) {
        Strings lang;
        try {
            Gson gson = new Gson();
            lang = gson.fromJson(ulang.getJson(), Strings.class);
            lang.setLangRes(ulang);
        } catch (JsonSyntaxException | NullPointerException e) {
            e.printStackTrace();
            lang = new Strings(LanguagesEnum.EN_US);
        }
        return lang;
    }

    public String
    SEARCH_TIP = "Insert your video or playlist link:",
    SEARCH = "Search",
    SETTINGS = "Settings",
    ABOUT = "About",
    SEARCH_ERROR="Search error!",
    SEARCH_ERROR_TIP="Your search has failed, please check the link and try again.",
    LOADING="Loading...",
    ERROR="Error!",
    BACK="<- Back",
    APPLY="Apply",
    FAKE_CLIENT="Fake client: ",
    FROM_BROWSER="From browser: ",
    FROM_FILE="From file: ",
    NO_COOKIES="No cookies",
    LOAD_COOKIE_MODE="Load cookie mode:",
    PATH="Path: ",
    PATH_TIP="if empty uses OS installed tool.",
    JS_RUNTIME="JS Runtime: ",
    SIMULTANEOUS_DOWN="Simultaneous downloads: ",
    DOWNLOADING ="Downloading...",
    DOWNLOAD="Download",
    DOWNLOAD_DIR="Downloads dir: ",
    ALL_DW_FINISH="All downloas has finished.",
    DONE="Done.",
    CANCEL="Cancel",
    STARTING="Starting...",
    CONVERT_TO="Convert to: ",
    IGNORING_FORMAT="Ignoring 1 inválid format.",
    NO_CONVERSION="No conversion",
    TITLE="Title: ",
    CHANNEL="Channer: ",
    FORMAT="Format: ",
    EXCEPTION_DETAILS="=== EXCEPTION DETAILS ===\n",
    EXIT_CODE="Exit code: ",
    COMMAND_EXECUTED="Command executed:\n",
    OUTPUT_LOG="=== OUTPUT LOG (STDERR) ===\n",
    NO_LOGS_RETURNED="(No logs returned)",
    RUNNING_CMD="Running command: ",
    DW_STATUS_DOWLOADING="Downloading: ",
    DETAILS_ERR="Details of error:",
    CUSTOM_DW_PATH="Default downloads dir: ",
    CUSTOM_DW_PATH_TIP="If empty uses default downloads folder of system.",
    COOKIE_FILE_TIP="Insert your cookie file path:",
    SELECT_FOLDER="Click to select a folder",
    SELECT_FILE="Click to select a file",
    INSERT_VALID_DW_FOLDER="Please insert a valid Download directory!.",
    BEST_QUALITY="Best quality",
    DATA_SAVING="Data saving",
    AUDIO_ONLY_IF_POSSIBLE="Audio only if possible",
    QUALITY_PRESET="Quality preset: ",
    SELECT_ALL="Select all",
    DEV_BY_FORMAT ="Developed by %s.\n\nAn open-source desktop application for downloading media files.\nClick 'Show Details' below to view third-party libraries and licenses.",
    TP_LIBS_LICENCES="Third-Party Libraries & Open Source Licenses:";
}
