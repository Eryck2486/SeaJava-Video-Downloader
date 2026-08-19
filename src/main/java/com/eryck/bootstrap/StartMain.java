package com.eryck.bootstrap;

import org.tukaani.xz.XZInputStream;
import java.io.*;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StartMain {
    public static void RecursiveDelete(File file){
        if (file.isDirectory()) {
            File[] arquivos = file.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    RecursiveDelete(arquivo); // Deleta o conteúdo recursivamente
                }
            }
        }
        file.delete();
    }
    static File tmpFolder = new File("tmp");
    static String JavaFXPathName = "javafx-sdk-26.0.2";
    static String DwDep = "dowloaded_dep";

    static class Dependency {
        public enum FileCompressModeEnum{
            ZIP_FILE(".zip"),
            JAR_FILE(".jar"),
            TARBALL_XZ(".tar.xz"),
            TARBALL_GZ(".tar.gz"),
            XZ_FILE(".xz"),
            GZ_FILE(".gz"),
            TAR_FILE(".tar"),
            LINUX_BINARY_FILE(".bin"),
            WINDOWS_BINARY_FILE(".exe");

            private final String extension;
            FileCompressModeEnum(String extension){
                this.extension=extension;
            }

            public String getExtension(){
                return extension;
            }
        }

        URL depURL;
        String[][] move;
        File[] deps;
        FileCompressModeEnum cmode;
        String dependecyName;

        Dependency(String url,
                   String dependecyName,
                   String[][] copy, //Instruction to move extracted files/folders (relative to exec path)
                   String[] deps, //List of essential files (if any file is missing, the dependency will be deleted and downloaded again)
                   FileCompressModeEnum mode //Remote file compression mode
        ){
            try {
                depURL = URI.create(url).toURL();
                this.dependecyName = dependecyName;
                this.move = copy;
                List<File> parseFiles = new ArrayList<>();
                for (String file : deps)
                {
                    parseFiles.add(new File(file));
                }
                this.deps=parseFiles.toArray(new File[0]);
                this.cmode=mode;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void CheckAndDownload(){
            if(CheckDeps()) {
                File dest = new File(tmpFolder,DwDep+cmode.getExtension());
                if(dest.exists()) {
                    dest.delete();
                }
                dest.getParentFile().mkdirs();
                File fileDownloaded = StartDownload(dest);
                if(
                        cmode==FileCompressModeEnum.JAR_FILE
                        || cmode==FileCompressModeEnum.LINUX_BINARY_FILE
                        || cmode==FileCompressModeEnum.WINDOWS_BINARY_FILE
                ){
                    MoveFiles(fileDownloaded);
                }else if(fileDownloaded!=null){
                    FileExtractor(fileDownloaded, cmode);
                    if(tmpFolder!=null){
                        MoveFiles(null);
                    }
                }
                RecursiveDelete(tmpFolder);
            }
        }

        //Process list of files to move
        private void MoveFiles(File origin){
            for(String[] moveTmp : move){
                if(origin==null){
                    String dir;
                    if(moveTmp[0].equals(".")){
                        dir=DwDep;
                    }else{
                        dir=moveTmp[0];
                    }
                    origin = new File(tmpFolder.getPath(), dir);
                }
                if(origin.exists()) {
                    File destin = new File(moveTmp[1]);
                    destin.mkdirs();
                    if (destin.getPath().contains("deps/bin")) {
                        MakeExecutable(origin);
                    }
                    try {
                        Files.move(Paths.get(origin.getAbsolutePath()), Paths.get(destin.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    System.out.println("File "+origin+" not found, listed files:");
                    File parent = origin.getParentFile();
                    if(parent.exists()) {
                        File[] paths = parent.listFiles();
                        if(paths!=null) {
                            for (File f : paths) {
                                System.out.println(f.getName());
                            }
                        }
                    }
                }
                origin=null;
            }
        }

        public static void MakeExecutable(File file) {
            if (!file.exists()) return;
            try {
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);

                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_EXECUTE);

                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);

                Files.setPosixFilePermissions(file.toPath(), perms);
            } catch (UnsupportedOperationException e) {
                file.setExecutable(true, false);
            } catch (IOException e) {
                System.err.println("Error during apply POSIX permissions on file: " + file.getAbsolutePath());
            }
        }

        private void FileExtractor(File fileIn, FileCompressModeEnum mode){
            switch (mode){
                case ZIP_FILE -> {
                    ZipExtractor(fileIn, tmpFolder);
                }
                case TARBALL_XZ -> {
                    TXZExtractor(fileIn, tmpFolder);
                }
                case TARBALL_GZ -> {
                    TGZExtractor(fileIn, tmpFolder);
                }
            }
        }

        private File StartDownload(File dest) {
            try {
                URL currentUrl = depURL;
                HttpURLConnection conn;
                int status;

                // Loop to follow redirects
                while (true) {
                    conn = (HttpURLConnection) currentUrl.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setInstanceFollowRedirects(true);

                    status = conn.getResponseCode();

                    // If redirect status follow new link
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                            status == HttpURLConnection.HTTP_MOVED_PERM ||
                            status == HttpURLConnection.HTTP_SEE_OTHER ||
                            status == 307 || status == 308) {

                        String newUrl = conn.getHeaderField("Location");
                        currentUrl = new URL(newUrl);
                    } else {
                        break; // File found
                    }
                }

                if (status != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP response code: " + status);
                }

                // Save file
                try (InputStream in = conn.getInputStream()) {
                    if(!dest.exists()){
                        dest.mkdirs();
                        dest.createNewFile();
                    }
                    Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                return dest;

            } catch (IOException e) {
                throw new RuntimeException("Failed to download file from GitHub", e);
            }
        }

        private boolean CheckDeps(){
            if(depURL!=null) for(File dep : deps){
                if(!dep.exists()){
                    System.out.println(dep+" Not found., reinstalling "+dependecyName);
                    return true;
                }
            }
            System.out.println(dependecyName+": OK");
            return false;
        }

        private static void TXZExtractor(File fileIn, File outputFolder){
            File tarFile = XzExtractor(fileIn, outputFolder);
            TarExtractor(tarFile, outputFolder);
        }

        private static void TGZExtractor(File filein, File outputFolder){
            File tarFile = GzExtractor(filein, outputFolder);
            File tarExtractionFolder = new File(outputFolder, tarFile.getName().replace(FileCompressModeEnum.TAR_FILE.getExtension(),""));
            tarExtractionFolder.mkdirs();
            TarExtractor(tarFile, tarExtractionFolder);
        }

        private static void ZipExtractor(File fileIn, File destDir){
            try(ZipInputStream zipst = new ZipInputStream(new FileInputStream(fileIn))){
                destDir.mkdirs();
                for (ZipEntry zipEntry = zipst.getNextEntry(); zipEntry!=null; zipEntry=zipst.getNextEntry()){
                    File newFile = new File(destDir, zipEntry.getName());
                    if(zipEntry.isDirectory()){
                        newFile.mkdirs();
                    }else {
                        newFile.getParentFile().mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            zipst.transferTo(fos);
                        }
                    }
                    zipst.closeEntry();
                }
            } catch (IOException e) {
                throw new RuntimeException("Extraction error: "+fileIn, e);
            }
        }

        private static File XzExtractor(File inFile, File destDir){
            File finalFile = new File(destDir, inFile.getName().replace(FileCompressModeEnum.XZ_FILE.getExtension(), ""));
            try (FileInputStream fin = new FileInputStream(inFile); //Copy bytes of file
                 BufferedInputStream bf = new BufferedInputStream(fin); //Copy bytes of stream fin With buffer
                    XZInputStream fileIn = new XZInputStream(bf); //Extract XZ file
                 OutputStream tarOut = new BufferedOutputStream(new FileOutputStream(finalFile)) //Access to write on output tar file
            ) {
                fileIn.transferTo(tarOut); //Write buffers on tar file
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return finalFile;
        }

        private static File GzExtractor(File inFile, File destDir){
            File finalFile = new File(destDir, inFile.getName().replace(FileCompressModeEnum.GZ_FILE.getExtension(), ""));
            try {
                if(!finalFile.exists()) finalFile.createNewFile();
                try (InputStream fileIn = new BufferedInputStream(new GZIPInputStream(new FileInputStream(inFile)));
                     OutputStream out = new BufferedOutputStream(new FileOutputStream(finalFile));) {
                    fileIn.transferTo(out);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return finalFile;
        }

        private static void TarExtractor(File inFile, File destDir){
            try(InputStream tarStream = new FileInputStream(inFile)) {
                byte[] header = new byte[512];
                while (tarStream.readNBytes(header, 0, 512) == 512) {
                    if (header[0] == 0) break;

                    String name = new String(header, 0, 100).trim();
                    if (name.isEmpty()) continue;

                    String sizeOctal = new String(header, 124, 12).trim();
                    long size = sizeOctal.isEmpty() ? 0 : Long.parseLong(sizeOctal, 8);

                    byte type = header[156];

                    File outFile = new File(destDir, name);

                    if (type == '5' || name.endsWith("/")) {
                        outFile.mkdirs();
                    } else {
                        outFile.getParentFile().mkdirs();
                        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
                            long remaining = size;
                            byte[] buffer = new byte[8192];
                            while (remaining > 0) {
                                int bytesToRead = (int) Math.min(buffer.length, remaining);
                                int read = tarStream.read(bytesToRead > 0 ? buffer : buffer, 0, bytesToRead);
                                if (read == -1) break;
                                out.write(buffer, 0, read);
                                remaining -= read;
                            }
                        }

                        long padding = (512 - (size % 512)) % 512;
                        if (padding > 0) {
                            tarStream.skipNBytes(padding);
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Erro de stream");
                throw new RuntimeException();
            }
        }
    }

    private static void VerifyAndDownload(PlatformOSEnum os) {
        List<Dependency> dependencyConstructors = new ArrayList<>();
        switch (os){
            case WINDOWS -> {
                dependencyConstructors.add(new Dependency( //JavaFX for windows
                                        "https://download2.gluonhq.com/openjfx/26.0.2/openjfx-26.0.2_windows-x64_bin-sdk.zip"
                                        ,"JavaFX",
                                        new String[][]{ //Files/folders to move
                                                {JavaFXPathName, "deps/libs/JavaFX/windows_x64/"+JavaFXPathName}
                                        },
                                        new String[]{ //Files to verify
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.base.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.fxml.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.media.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.swing.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.web.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/jfx.incubator.input.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.controls.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.graphics.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/javafx.properties",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/jdk.jsobject.jar",
                                                "deps/libs/JavaFX/windows_x64/"+JavaFXPathName+"/lib/jfx.incubator.richtext.jar"
                                        },
                                        Dependency.FileCompressModeEnum.ZIP_FILE //Remote format
                                )
                        );
                dependencyConstructors.add(new Dependency( //Gson
                                        "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar",
                                        "Gson",new String[][]{
                                                {".", "deps/libs/Gson/gson-2.14.0.jar"}
                                        },
                                        new String[]{
                                                "deps/libs/Gson/gson-2.14.0.jar"
                                        },
                                        Dependency.FileCompressModeEnum.JAR_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //FFMpeg
                                        "https://github.com/BtbN/FFmpeg-Builds/releases/download/autobuild-2026-08-13-17-03/ffmpeg-N-126122-gca821e458a-win64-gpl.zip",
                                        "FFMpeg",new String[][]{
                                                {"ffmpeg-N-126122-gca821e458a-win64-gpl/bin/ffmpeg.exe", "deps/bin/ffmpeg.exe"},
                                                {"ffmpeg-N-126122-gca821e458a-win64-gpl/bin/ffplay.exe", "deps/bin/ffplay.exe"},
                                                {"ffmpeg-N-126122-gca821e458a-win64-gpl/bin/ffprobe.exe", "deps/bin/ffprobe.exe"}
                                        },
                                        new String[]{
                                                "deps/bin/ffmpeg.exe",
                                                "deps/bin/ffplay.exe",
                                                "deps/bin/ffprobe.exe"
                                        },
                                        Dependency.FileCompressModeEnum.ZIP_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //yt-dlp
                                "https://github.com/yt-dlp/yt-dlp/releases/download/2026.07.04/yt-dlp.exe",
                                "yt-dlp",new String[][]{
                                        {".", "deps/bin/yt-dlp.exe"}
                                },
                                new String[]{
                                        "deps/bin/yt-dlp.exe"
                                },
                                Dependency.FileCompressModeEnum.WINDOWS_BINARY_FILE
                        )
                );
                dependencyConstructors.add(new Dependency( //QuickJS
                                "https://github.com/quickjs-ng/quickjs/releases/download/v0.16.1/qjs-windows-x86_64.exe",
                                "QuickJS",new String[][]{
                                        {".", "deps/bin/qjs-windows-x86_64.exe"}
                                },
                                new String[]{
                                        "deps/bin/qjs-windows-x86_64.exe"
                                },
                                Dependency.FileCompressModeEnum.WINDOWS_BINARY_FILE
                        )
                );
                dependencyConstructors.add(new Dependency( //Webp
                                "https://repo1.maven.org/maven2/com/twelvemonkeys/imageio/imageio-webp/3.14.0/imageio-webp-3.14.0.jar",
                                "TwelveMonkeys imageio webp",
                                new String[][]{
                                        {".", "deps/libs/Webp/imageio-webp-3.14.0.jar"}
                                },
                                new String[]{
                                        "deps/libs/Webp/imageio-webp-3.14.0.jar"
                                },
                                Dependency.FileCompressModeEnum.JAR_FILE

                        )
                );
                dependencyConstructors.add(new Dependency( //webp-core
                                "https://repo1.maven.org/maven2/com/twelvemonkeys/imageio/imageio-core/3.14.0/imageio-core-3.14.0.jar",
                                "TwelveMonkeys webp core",
                                new String[][]{
                                        {".", "deps/libs/Webp/imageio-core-3.14.0.jar"}
                                },
                                new String[]{
                                        "deps/libs/Webp/imageio-core-3.14.0.jar"
                                },
                                Dependency.FileCompressModeEnum.JAR_FILE

                        )
                );
                dependencyConstructors.add(new Dependency( //common-lang for webp
                                "https://repo1.maven.org/maven2/com/twelvemonkeys/common/common-lang/3.14.0/common-lang-3.14.0.jar",
                                "TwelveMonkeys common-lang",
                                new String[][]{
                                        {".", "deps/libs/Webp/common-lang-3.14.0.jar"}
                                },
                                new String[]{
                                        "deps/libs/Webp/common-lang-3.14.0.jar"
                                },
                                Dependency.FileCompressModeEnum.JAR_FILE

                        )
                );
            }
            case LINUX -> {
                dependencyConstructors.add(new Dependency( //JavaFX for windows
                                        "https://download2.gluonhq.com/openjfx/26.0.2/openjfx-26.0.2_linux-x64_bin-sdk.zip",
                                        "JavaFX",new String[][]{
                                                {JavaFXPathName, "deps/libs/JavaFX/linux_x64/"+JavaFXPathName}
                                        },
                                        new String[]{
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.base.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.swing.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-54.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-59.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libglassgtk3.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjavafx_iio.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.controls.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-56.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-60.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libglass.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjfxmedia.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.fxml.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.web.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-57.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-61.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libgstreamer-lite.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjfxwebkit.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.graphics.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/jdk.jsobject.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-56.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-62.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjavafx_font_freetype.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libprism_common.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.media.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/jfx.incubator.input.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-57.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libdecora_sse.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjavafx_font_pango.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libprism_es2.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/javafx.properties",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/jfx.incubator.richtext.jar",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libavplugin-ffmpeg-58.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libfxplugins.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libjavafx_font.so",
                                                "deps/libs/JavaFX/linux_x64/"+JavaFXPathName+"/lib/libprism_sw.so"
                                        },
                                        Dependency.FileCompressModeEnum.ZIP_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //Gson
                                        "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar",
                                        "Gson",new String[][]{
                                                {".", "deps/libs/Gson/gson-2.14.0.jar"}
                                        },
                                        new String[]{
                                                "deps/libs/Gson/gson-2.14.0.jar"
                                        },
                                        Dependency.FileCompressModeEnum.JAR_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //FFMpeg
                                        "https://github.com/BtbN/FFmpeg-Builds/releases/download/latest/ffmpeg-master-latest-linux64-gpl.tar.xz",
                                        "FFMpeg",new String[][]{
                                                {"ffmpeg-master-latest-linux64-gpl/bin/ffmpeg", "deps/bin/ffmpeg"},
                                                {"ffmpeg-master-latest-linux64-gpl/bin/ffplay", "deps/bin/ffplay"},
                                                {"ffmpeg-master-latest-linux64-gpl/bin/ffprobe", "deps/bin/ffprobe"}
                                        },
                                        new String[]{
                                                "deps/bin/ffmpeg",
                                                "deps/bin/ffplay",
                                                "deps/bin/ffprobe"
                                        },
                                        Dependency.FileCompressModeEnum.TARBALL_XZ
                                )
                        );
                dependencyConstructors.add(new Dependency( //yt-dlp
                                        "https://github.com/yt-dlp/yt-dlp/releases/download/2026.07.04/yt-dlp_linux",
                                        "yt-dlp",new String[][]{
                                                {".", "deps/bin/yt-dlp_linux"}
                                        },
                                        new String[]{
                                                "deps/bin/yt-dlp_linux"
                                        },
                                        Dependency.FileCompressModeEnum.LINUX_BINARY_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //QuickJS
                                        "https://github.com/quickjs-ng/quickjs/releases/download/v0.16.1/qjs-linux-x86_64",
                                        "QuickJS",
                                        new String[][]{
                                                {".", "deps/bin/qjs-linux-x86_64"}
                                        },
                                        new String[]{
                                                "deps/bin/qjs-linux-x86_64"
                                        },
                                        Dependency.FileCompressModeEnum.LINUX_BINARY_FILE
                                )
                        );
                dependencyConstructors.add(new Dependency( //Webp
                                        "https://repo1.maven.org/maven2/com/twelvemonkeys/imageio/imageio-webp/3.14.0/imageio-webp-3.14.0.jar",
                                        "TwelveMonkeys imageio webp",
                                        new String[][]{
                                                {".", "deps/libs/Webp/imageio-webp-3.14.0.jar"}
                                        },
                                        new String[]{
                                                "deps/libs/Webp/imageio-webp-3.14.0.jar"
                                        },
                                        Dependency.FileCompressModeEnum.JAR_FILE

                                )
                        );
                dependencyConstructors.add(new Dependency( //webp-core
                                        "https://repo1.maven.org/maven2/com/twelvemonkeys/imageio/imageio-core/3.14.0/imageio-core-3.14.0.jar",
                                        "TwelveMonkeys webp core",
                                        new String[][]{
                                                {".", "deps/libs/Webp/imageio-core-3.14.0.jar"}
                                        },
                                        new String[]{
                                                "deps/libs/Webp/imageio-core-3.14.0.jar"
                                        },
                                        Dependency.FileCompressModeEnum.JAR_FILE

                                )
                        );
                dependencyConstructors.add(new Dependency( //common-lang for webp
                                        "https://repo1.maven.org/maven2/com/twelvemonkeys/common/common-lang/3.14.0/common-lang-3.14.0.jar",
                                        "TwelveMonkeys common-lang",
                                        new String[][]{
                                                {".", "deps/libs/Webp/common-lang-3.14.0.jar"}
                                        },
                                        new String[]{
                                                "deps/libs/Webp/common-lang-3.14.0.jar"
                                        },
                                        Dependency.FileCompressModeEnum.JAR_FILE

                                )
                        );
            }
            default -> {
                return;
            }
        }

        for(Dependency depManager : dependencyConstructors) {
            depManager.CheckAndDownload();
            RecursiveDelete(tmpFolder);
        }
    }

    public enum PlatformOSEnum {
        WINDOWS(0, new String[]{"win"}),
        LINUX(1, new String[]{"linux"});

        public final String[] ospatterns;
        public final int id;
        PlatformOSEnum(int id, String[] ospatterns){
            this.ospatterns=ospatterns;
            this.id=id;
        }

        public String[] getOSPattern(){
            return ospatterns;
        }

        public int getId() {
            return id;
        }

        public static PlatformOSEnum getOS(){
            String osenv = System.getProperty("os.name").toLowerCase();
            for(PlatformOSEnum os : values()){
                for(String pat : os.getOSPattern()) if(osenv.contains(pat)) return os;
            }
            return null;
        }
    }

    static void main(String[] args) {
        try {
            PlatformOSEnum thisOS = PlatformOSEnum.getOS();
            if(thisOS==null) {
                System.out.println("Your OS is not supported for this application.");
                return;
            }
            VerifyAndDownload(thisOS);
            TryExtractResources();
            String osDir = "";
            switch (thisOS){
                case WINDOWS -> {
                    osDir = "windows_x64/";
                }
                case LINUX -> {
                    osDir = "linux_x64/";
                }
            }

            File jarPath = getRunningJarFile();

            List<Path> modulePaths = List.of(
                    Paths.get(jarPath.getAbsolutePath()),
                    Paths.get("deps/libs/JavaFX", osDir+JavaFXPathName+"/lib/"),
                    Paths.get("deps/libs/Gson/gson-2.14.0.jar"),
                    Paths.get("deps/libs/Webp/imageio-webp-3.14.0.jar"),
                    Paths.get("deps/libs/Webp/imageio-core-3.14.0.jar"),
                    Paths.get("deps/libs/Webp/common-lang-3.14.0.jar")
            );

            ModuleFinder finder = ModuleFinder.of(modulePaths.toArray(Path[]::new));
            ModuleLayer bootLayer = ModuleLayer.boot();
            Configuration configuration = bootLayer.configuration().resolve(
                    finder,
                    ModuleFinder.of(),
                    Set.of("SeaJava.YT.Downloader")
            );

            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            ModuleLayer customLayer = bootLayer.defineModulesWithOneLoader(configuration, systemClassLoader);
            ClassLoader moduleClassLoader = customLayer.findLoader("SeaJava.YT.Downloader");
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            Class<?> mainClass = moduleClassLoader.loadClass("com.eryck.SeaJavaVideoDownloader.SeaJavaMain");
            mainClass.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Start process failed.");
        }
    }

    public static File getRunningJarFile() {
        try {
            // Obtém a URL do local de onde esta classe foi carregada
            var location = StartMain.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();
            // Converte a URL em URI e depois em File (trata espaços e caracteres especiais no caminho)
            return new File(location.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Erro ao obter o arquivo JAR em execução", e);
        }
    }

    public static void TryExtractResources() {
        //Extracting themes
        File themesFolder = new File("themes/");
        if (!themesFolder.exists()) {
            themesFolder.mkdirs();
            String[] themes = new String[]{
                    "standard-dark.css"
            };
            for (String themefile : themes) {
                ResourceExtractor("/com/eryck/bootstrap/themes/" + themefile, new File(themesFolder, themefile));
            }
        }
    }

    public static boolean ResourceExtractor(String resPath, File fileOutput){
        if(fileOutput!=null && !fileOutput.exists()){
            File folder = fileOutput.getParentFile();
            if(!folder.exists()) folder.mkdirs();
            try {
                fileOutput.createNewFile();
                try (InputStream in = StartMain.class.getResourceAsStream(resPath)) {
                    if (in == null) {
                        return false;
                    }
                    Files.copy(in, fileOutput.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}