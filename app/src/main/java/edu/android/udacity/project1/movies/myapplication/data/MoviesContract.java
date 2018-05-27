package edu.android.udacity.project1.movies.myapplication.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String AUTHORITY = "edu.android.udacity.project1.movies.myapplication";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final String POSTER_IMAGE_BASE_DIR = "popular-movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_EXTERNAL_ID = "external_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PLOT = "plot";
    }

}
