package edu.android.udacity.project1.movies.myapplication.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.android.udacity.project1.movies.myapplication.model.Movie;
import edu.android.udacity.project1.movies.myapplication.model.Review;
import edu.android.udacity.project1.movies.myapplication.model.Video;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// REVIEW: Consider using Retrofit or Valley REST clients
// http://vickychijwani.me/retrofit-vs-volley/
// http://www.vogella.com/tutorials/Retrofit/article.html
// http://www.androidhive.info/2014/05/android-working-with-volley-library-1/

public class MovieClient {
    private static final String TAG = MovieClient.class.getName();

    public static class MovieClientConfig {
        public final String baseUrl;
        public final String apiKey;
        public final String posterPrefix;
        public MovieClientConfig(String baseUrl, String apiKey, String posterPrefix) {
            this.baseUrl = baseUrl;
            this.apiKey = apiKey;
            this.posterPrefix = posterPrefix;
        }
    }

    private MovieClientConfig config;
    private OkHttpClient client;

    public MovieClient(MovieClientConfig config) {
        this.config = config;
        this.client = new OkHttpClient();
    }

    synchronized public List<Movie> getMovies(String path) {
        try {
            String json = httpGet(path);
            List<Movie> movies = parseResponse(json);
            return movies;
        } catch (IOException e) {
            Log.w(TAG, "Failed to get movies", e);
            return null;
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse response", e);
            return null;
        }
    }

    synchronized public List<Video> getVideos(int movieId) {
        String path = "/movie/" + movieId + "/videos";
        try {
            String json = httpGet(path);
            return parseVideos(json);
        } catch (IOException e) {
            Log.w(TAG, "Failed to get videos from " + path, e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse videos from " + path, e);
            return null;
        }
    }

    synchronized public List<Review> getReviews(int movieId) {
        String path = "/movie/" + movieId + "/reviews";
        try {
            String json = httpGet(path);
            return parseReviews(json);
        } catch (IOException e) {
            Log.w(TAG, "Failed to get reviews from " + path, e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse reviews from " + path, e);
            return null;
        }
    }


    private String httpGet(String path) throws IOException {
        String url = config.baseUrl + path + "?api_key=" + config.apiKey;
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        Log.d(TAG, "Received from " + url + ": " + body);
        return body;
    }

    private Movie parseMovie(JSONObject object) throws JSONException {
        return new Movie(
            object.getInt("id"),
            object.getString("title"),
            config.posterPrefix + object.getString("poster_path"),
            object.getString("release_date"),
            object.getString("overview"),
            object.getDouble("vote_average")
        );
    }

    private List<Movie> parseResponse(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("results");
        List<Movie> movies = new ArrayList<>();
        int numberOfMovies = array.length();
        for (int i=0; i<numberOfMovies; i++) {
            Movie movie = parseMovie(array.getJSONObject(i));
            movies.add(movie);
        }
        return movies;
    }

    private List<Video> parseVideos(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("results");
        List<Video> videos = new ArrayList<>();
        int numberOfVideos = array.length();
        for (int i=0; i<numberOfVideos; i++) {
            String key = array.getJSONObject(i).getString("key");
            String name = array.getJSONObject(i).getString("name");
            Video video = new Video(name, key);
            videos.add(video);
        }
        return videos;
    }

    private List<Review> parseReviews(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("results");
        List<Review> videos = new ArrayList<>();
        int numberOfVideos = array.length();
        for (int i=0; i<numberOfVideos; i++) {
            JSONObject reviewObject = array.getJSONObject(i);
            String author = reviewObject.getString("author");
            String content = reviewObject.getString("content");
            Review review = new Review(author, content);
            videos.add(review);
        }
        return videos;
    }
}
