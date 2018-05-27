package edu.android.udacity.project1.movies.myapplication.tasks;

import android.os.AsyncTask;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.android.udacity.project1.movies.myapplication.global.Global;
import edu.android.udacity.project1.movies.myapplication.model.Movie;
import edu.android.udacity.project1.movies.myapplication.service.MovieClient;

// REVIEW: Consider moving GetMovieTask to separate class to structure code and ease refactorings
// http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
// CONCERN: I need access to Activity's instance private properties
// SOLVED: Added respective public methods to MainActivity (show{ProgressBar/Data/Error})
public class GetMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    public interface Listener {
        void showProgressBar();
        void showData(List<Movie> movies);
        void showError();
        void showEmpty();
    }

    private Listener listener;

    public GetMoviesTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length > 0) {
            String url = params[0];
            List<Movie> movies = Global.movieClient.getMovies(url);
            return movies;
        } else {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            if (movies.size() > 0) {
                Collections.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie lhs, Movie rhs) {
                        return lhs.id - rhs.id;
                    }
                });
                listener.showData(movies);
            } else {
                listener.showEmpty();
            }
        } else {
            listener.showError();
        }
    }

    @Override
    protected void onCancelled() {
        listener.showData(null);
        super.onCancelled();
    }
}
