package com.eryck.SeaJavaVideoDownloader;

import java.util.List;

public class PlayListInfo {
    public static class Entry
    {
        public String _type;
        public String id;
        public String title;
        public String url;
        public long duration;
        public long view_count;
    }
    public String _type;
    public String id;
    public String title;
    public String uploader;
    public int playlist_count;
    public List<Entry> entries;
}
