package edu.android.udacity.project1.movies.myapplication.global;

import edu.android.udacity.project1.movies.myapplication.service.MovieClient;

public class Global {
    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String API_KEY = "REMOVED";
    private static final String IMG_PREFIX = "http://image.tmdb.org/t/p/w185";

    private static final MovieClient.MovieClientConfig movieClientConfig =
        new MovieClient.MovieClientConfig(BASE_URL, API_KEY, IMG_PREFIX);

    public static final MovieClient movieClient =
        new MovieClient(movieClientConfig);
}
