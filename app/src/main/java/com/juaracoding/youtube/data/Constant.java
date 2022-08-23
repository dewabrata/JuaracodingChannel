package com.juaracoding.youtube.data;

public class Constant {

    /* replace with your own API_KEY */
    public static String YOUTUBE_API_KEY = "API_KEY-DISINI";

    public static String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static long DELAY_TIME_SPLASH = 500;

    // maximum data per request
    public static int MAX_LOAD_VIDEO = 40;

    // maximum retry channel info request
    public static int MAX_RETRY_INFO = 3;

    /* FCM notification Topic, please don't change if you don't know about this */
    public static String NOTIF_TOPIC = "ALL-DEVICE";

}
