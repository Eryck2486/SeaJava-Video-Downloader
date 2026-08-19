package com.eryck.SeaJavaVideoDownloader;

import java.util.List;

// Media info properties to parse yt-dlp JSON
public class MediaInfo {
    public enum MediaConvertEnum {
        NO_CONVERSION(0, "--audio-format", "next", Strings.gIns().NO_CONVERSION),
        // Audio formats
        AUDIO_M4A(1, "--audio-format", "m4a", "Audio M4a"),
        AUDIO_WAV(2, "--audio-format", "wav", "Audio Wav"),
        AUDIO_FLAC(3, "--audio-format", "flac", "Audio Flac"),
        AUDIO_OPUS(4, "--audio-format", "opus", "Audio Opus"),
        AUDIO_AAC(5, "--audio-format", "aac", "Audio AAC"),
        AUDIO_MP3(6, "--audio-format", "mp3", "Audio MP3"),

        // Video formats
        VIDEO_MP4(7, "--recode-video", "mp4", "Vidoe MP4"),
        VIDEO_MKV(8, "--recode-video", "mkv", "Video MKV"),
        VIDEO_WEBM(9, "--recode-video", "webm", "Video Webm"),
        VIDEO_AVI(10, "--recode-video", "avi", "Video AVI"),
        VIDEO_FLV(11, "--recode-video", "flv", "Video FLV");

        private final int id;
        private final String ext;
        private final String name;
        private final String tag;

        MediaConvertEnum(int id, String tag, String ext, String name) {
            this.id = id;
            this.tag = tag;
            this.ext = ext;
            this.name = name;
        }

        public String getExt() {
            return ext;
        }

        public String getTag() {
            return tag;
        }

        public String toString() {
            return name;
        }

        public boolean isAudio() {
            return this == MediaInfo.MediaConvertEnum.AUDIO_M4A
                    || this == MediaInfo.MediaConvertEnum.AUDIO_WAV
                    || this == MediaInfo.MediaConvertEnum.AUDIO_FLAC
                    || this == MediaInfo.MediaConvertEnum.AUDIO_OPUS
                    || this == MediaInfo.MediaConvertEnum.AUDIO_AAC
                    || this == MediaInfo.MediaConvertEnum.AUDIO_MP3;
        }
    }

    public String title = "N/A";
    public String uploader = "N/A";
    public double duration = 0;
    public String thumbnail = "N/A";
    public List<FormatInfo> formats;

    public FormatInfo getBetterFormat() {
        FormatInfo bestFormat = null;
        for (FormatInfo tmpFormat : formats) {
            try {
                if (tmpFormat.resolution != null
                        && !tmpFormat.resolution.contains("audio only")
                        && !tmpFormat.resolution.contains("mhtm")
                        && !tmpFormat.resolution.contains("mhtml")
                        && (tmpFormat.fps == null || tmpFormat.fps > 0)) {
                    if (bestFormat == null) {
                        bestFormat = tmpFormat;
                        continue;
                    }
                    String[] ouldFormatSplit = bestFormat.resolution.split("x");
                    int oldScale = Integer.parseInt(ouldFormatSplit[0]) * Integer.parseInt(ouldFormatSplit[1]);

                    String[] tmpFormatSplit = tmpFormat.resolution.split("x");
                    int tmpScale = Integer.parseInt(tmpFormatSplit[0]) * Integer.parseInt(tmpFormatSplit[1]);

                    if (oldScale < tmpScale ||
                            (oldScale == tmpScale && bestFormat.fps < tmpFormat.fps) ||
                            (bestFormat.ext.equals("webm") && tmpFormat.ext.equals("mp4"))
                    ) {
                        bestFormat = tmpFormat;
                    }
                }
            } catch (NumberFormatException | NullPointerException ignored) {
                System.out.println(Strings.gIns().IGNORING_FORMAT);
            }
        }
        if (bestFormat == null) {
            for (FormatInfo tmpFormat : formats) {
                if (tmpFormat.resolution.contains("audio only")) {
                    if (bestFormat == null) {
                        bestFormat = tmpFormat;
                        continue;
                    }
                    if (bestFormat.ext.equals("webm") && tmpFormat.ext.equals("mp4")) {
                        bestFormat = tmpFormat;
                    }
                }
            }
        }
        if (bestFormat == null) {
            bestFormat = new FormatInfo();
            bestFormat.format_id = "best";
        }
        return bestFormat;
    }

    public FormatInfo getEconomicalFormat() {
        if (formats == null || formats.isEmpty()) {
            FormatInfo fallback = new FormatInfo();
            fallback.format_id = "worst";
            return fallback;
        }

        FormatInfo economicalFormat = null;

        for (FormatInfo tmpFormat : formats) {
            if (tmpFormat.isAudioOnly() || tmpFormat.vcodec.equals("none")
                    || (tmpFormat.resolution != null && (tmpFormat.resolution.contains("mhtm") || tmpFormat.resolution.contains("mhtml")))) {
                continue;
            }

            if (economicalFormat == null) {
                economicalFormat = tmpFormat;
                continue;
            }

            long currentSize = tmpFormat.filesize != null && tmpFormat.filesize > 0 ? tmpFormat.filesize :
                    (tmpFormat.filesize_approx != null ? tmpFormat.filesize_approx : Long.MAX_VALUE);
            long bestSize = economicalFormat.filesize != null && economicalFormat.filesize > 0 ? economicalFormat.filesize :
                    (economicalFormat.filesize_approx != null ? economicalFormat.filesize_approx : Long.MAX_VALUE);

            if (currentSize < bestSize) {
                economicalFormat = tmpFormat;
            }
            else if (currentSize == Long.MAX_VALUE && bestSize == Long.MAX_VALUE) {
                int currentHeight = tmpFormat.height != null && tmpFormat.height > 0 ? tmpFormat.height : Integer.MAX_VALUE;
                int bestHeight = economicalFormat.height != null && economicalFormat.height > 0 ? economicalFormat.height : Integer.MAX_VALUE;

                if (currentHeight < bestHeight) {
                    economicalFormat = tmpFormat;
                }
            }
        }

        if (economicalFormat == null) {
            return getAudioOnlyFormat();
        }

        return economicalFormat;
    }

    public FormatInfo getAudioOnlyFormat() {
        if (formats == null || formats.isEmpty()) {
            FormatInfo fallback = new FormatInfo();
            fallback.format_id = "bestaudio/ba";
            return fallback;
        }

        FormatInfo bestAudio = null;
        double maxAbr = -1.0;

        for (FormatInfo tmpFormat : formats) {
            if (tmpFormat.isAudioOnly() || (tmpFormat.resolution != null && tmpFormat.resolution.contains("audio only"))) {

                double currentAbr = 0.0;
                try {
                    if (tmpFormat.abr != null && !tmpFormat.abr.equals("N/A")) {
                        currentAbr = Double.parseDouble(tmpFormat.abr.split("\\.")[0]);
                    }
                } catch (NumberFormatException ignored) {}

                if (bestAudio == null || currentAbr > maxAbr || (currentAbr == maxAbr && tmpFormat.ext.equals("m4a"))) {
                    bestAudio = tmpFormat;
                    maxAbr = currentAbr;
                }
            }
        }

        if (bestAudio == null) {
            bestAudio = new FormatInfo();
            bestAudio.format_id = "bestaudio/ba";
        }

        return bestAudio;
    }

    public static class FormatInfo {
        public String abr = "N/A";
        public String format_id = "N/A";
        public String ext = "N/A";
        public String resolution = "N/A";
        public Integer height = 0;
        public Double fps = 0.0;
        public String vcodec = "N/A";
        public String acodec = "N/A";
        public Long filesize = 0L;
        public Long filesize_approx = 0L;

        public boolean isAudioOnly() {
            return "none".equals(vcodec);
        }

        public boolean isVideoOnly() {
            return "none".equals(acodec);
        }

        public boolean isCompleteMedia() {
            return !isAudioOnly() && !isVideoOnly();
        }

        @Override
        public String toString() {
            if (isAudioOnly()) return String.format("Apenas Áudio (%s) - %s | ~%s kbps",
                    ext,
                    acodec,
                    (abr.split("\\."))[0]
            );
            return String.format("%s | %s (%s fps)",
                    height != null && height > 0 ? height + "p" : resolution,
                    ext,
                    fps != null ? fps.intValue() : 30
            );
        }
    }
}