package edu.android.udacity.project1.movies.myapplication.tasks;

import android.os.AsyncTask;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.global.Global;
import edu.android.udacity.project1.movies.myapplication.model.Video;

public class GetVideosTask extends AsyncTask<Integer, Void, List<Video>> {

    public interface Listener {
        void showProgressBar();
        void showData(List<Video> videos);
        void showError();
        void showEmpty();
    }

    private Listener listener;

    public GetVideosTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Video> doInBackground(Integer... params) {
        if (params != null && params.length > 0) {
            Integer id = params[0];
            return Global.movieClient.getVideos(id);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showProgressBar();
    }

    @Override
    protected void onPostExecute(List<Video> videos) {
        if (videos != null) {
            if (videos.size() > 0) {
                listener.showData(videos);
            } else {
                listener.showEmpty();
            }
        } else {
            listener.showError();
        }
    }
}
