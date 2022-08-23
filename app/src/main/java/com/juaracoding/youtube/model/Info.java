package com.juaracoding.youtube.model;

import java.io.Serializable;

public class Info implements Serializable {

    public String kind;
    public Snippet snippet;
    public Statistics statistics;
    public BrandingSettings brandingSettings;

    public class Snippet {
        public String title;
        public String description;
        public Thumbnail thumbnails;
    }

    public class Statistics {
        public long viewCount;
        public long subscriberCount;
        public long videoCount;
    }

    public class BrandingSettings {
        public Channel channel;
        public Image image;
    }

    public class Channel {
        public String profileColor;
    }

    public class Image {
        public String bannerExternalUrl;
    }

}
