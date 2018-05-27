package edu.android.udacity.project1.movies.myapplication.tasks;

import android.os.AsyncTask;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.global.Global;
import edu.android.udacity.project1.movies.myapplication.model.Review;

public class GetReviewsTask extends AsyncTask<Integer, Void, List<Review>> {

    public interface Listener {
        void showProgressBar();
        void showData(List<Review> videos);
        void showError();
        void showEmpty();
    }

    private Listener listener;

    public GetReviewsTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Review> doInBackground(Integer... params) {
        if (params != null && params.length > 0) {
            Integer id = params[0];
            return Global.movieClient.getReviews(id);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null) {
            if (reviews.size() > 0) {
                listener.showData(reviews);
            } else {
                listener.showEmpty();
            }
        } else {
            listener.showError();
        }
    }
}
