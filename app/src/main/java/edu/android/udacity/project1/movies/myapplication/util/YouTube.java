package edu.android.udacity.project1.movies.myapplication.util;

import android.net.Uri;

public class YouTube {
    private static final String YOUTUBE_WEB_BASE_URI = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_BASE_URI = "vnd.youtube:";

    private static final String THUMBNAIL_PREFIX = "http://img.youtube.com/vi/";
    private static final String THUMBNAIL_SUFFIX = "/0.jpg";

    public static Uri webUri(String key) {
        return Uri.parse(YOUTUBE_WEB_BASE_URI + key);
    }

    public static Uri appUri(String key) {
        return Uri.parse(YOUTUBE_APP_BASE_URI + key);
    }

    public static String thumbnail(String key) {
        return THUMBNAIL_PREFIX + key + THUMBNAIL_SUFFIX;
    }

}
