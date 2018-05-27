package edu.android.udacity.project1.movies.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.adapter.MovieGridAdapter;
import edu.android.udacity.project1.movies.myapplication.data.MoviesContract;
import edu.android.udacity.project1.movies.myapplication.model.Movie;
import edu.android.udacity.project1.movies.myapplication.tasks.GetMoviesTask;
import edu.android.udacity.project1.movies.myapplication.util.Check;
import edu.android.udacity.project1.movies.myapplication.util.Data;

public class MainActivity extends AppCompatActivity implements GetMoviesTask.Listener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MainActivity.class.getName();

    private ProgressBar mRequestProgressBar;
    private TextView mErrorMessageTextView;

    private RecyclerView mMovieGrid;
    private MovieGridAdapter mMovieGridAdapter;

    private static final String URL_TOP_RATED = "/movie/top_rated";
    private static final String URL_POPULAR = "/movie/popular";

    private static final int IMAGE_WIDTH = 180;

    private static final int FAVOURITE_MOVIES_LOADER_ID = 1001;

    private static final String CURRENT_SCROLL_POSITION = "current-scroll";
    private static final String SELECTED_COLLECTION_KEY = "selected-collection";
    private static final String COLLECTION_POPULAR = "popular";
    private static final String COLLECTION_TOPRATED = "toprated";
    private static final String COLLECTION_STARRED = "starred";
    private String mSelectedCollection;

    private int mCurrentScrollPosition;
    private GetMoviesTask mCurrentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestProgressBar = (ProgressBar) findViewById(R.id.pb_pendig_indicator);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mMovieGrid = (RecyclerView) findViewById(R.id.rv_movie_grid);

        int numberOfGridColumns = calculateNoOfColumns(getBaseContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfGridColumns);
        mMovieGrid.setLayoutManager(gridLayoutManager);
        mMovieGrid.setHasFixedSize(true);

        mMovieGridAdapter = new MovieGridAdapter(this, null);
        mMovieGrid.setAdapter(mMovieGridAdapter);

        mSelectedCollection = getCollectionFromPreferences();
        openCollection(mSelectedCollection);
    }

    private void saveCollection(String name) {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SELECTED_COLLECTION_KEY, mSelectedCollection);
        editor.commit();
    }

    private String getCollectionFromPreferences() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        String collection = sp.getString(SELECTED_COLLECTION_KEY, COLLECTION_STARRED);
        return collection;
    }

    private void openCollection(String name) {
        if (COLLECTION_POPULAR.equals(name)) {
            getMovieData(URL_POPULAR);
            getSupportActionBar().setTitle(getString(R.string.title_popular));
        } else if (COLLECTION_TOPRATED.equals(name)) {
            getMovieData(URL_TOP_RATED);
            getSupportActionBar().setTitle(getString(R.string.title_toprated));
        } else if (COLLECTION_STARRED.equals(name)) {
            getSupportActionBar().setTitle(getString(R.string.title_starred));
            getSupportLoaderManager().restartLoader(FAVOURITE_MOVIES_LOADER_ID, null, this);
        }
     }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = mMovieGrid.getLayoutManager();
        int scrollPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        mCurrentScrollPosition = scrollPosition;
        outState.putInt(CURRENT_SCROLL_POSITION, scrollPosition);
        Log.d(TAG, "Stored scroll position: " + scrollPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            int scrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);
            mCurrentScrollPosition = scrollPosition;
            Log.d(TAG, "Restored scroll position: " + scrollPosition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (COLLECTION_STARRED.equals(mSelectedCollection)) {
            getSupportLoaderManager().restartLoader(FAVOURITE_MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportLoaderManager().destroyLoader(FAVOURITE_MOVIES_LOADER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_popular:
                mSelectedCollection = COLLECTION_POPULAR;
                saveCollection(COLLECTION_POPULAR);
                openCollection(COLLECTION_POPULAR);
                return true;
            case R.id.menu_top_rated:
                mSelectedCollection = COLLECTION_TOPRATED;
                saveCollection(COLLECTION_TOPRATED);
                openCollection(COLLECTION_TOPRATED);
                return true;
            case R.id.menu_starred:
                mSelectedCollection = COLLECTION_STARRED;
                saveCollection(COLLECTION_STARRED);
                openCollection(COLLECTION_STARRED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / IMAGE_WIDTH);
        return noOfColumns;
    }

    private void getMovieData(String url) {
        if (Check.isOnline(this)) {
            if (mCurrentTask != null) {
                mCurrentTask.cancel(true);
            }
            GetMoviesTask getMoviesTask = new GetMoviesTask(this);
            mCurrentTask = getMoviesTask;
            getMoviesTask.execute(url);
        } else {
            showError();
        }
    }

    @Override
    public void showProgressBar() {
        mMovieGridAdapter.setData(null);
        mMovieGrid.setVisibility(View.INVISIBLE);
        mRequestProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError() {
        mMovieGrid.setVisibility(View.INVISIBLE);
        mMovieGridAdapter.setData(null);
        mRequestProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setText(getString(R.string.error_message));
    }

    @Override
    public void showEmpty() {
        mMovieGrid.setVisibility(View.INVISIBLE);
        mMovieGridAdapter.setData(null);
        mRequestProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setText(getString(R.string.no_items_available));
    }

    @Override
    public void showData(List<Movie> movies) {
        mMovieGridAdapter.setData(movies);
        mMovieGridAdapter.notifyDataSetChanged();
        mMovieGrid.setVisibility(View.VISIBLE);
        mRequestProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        if (mCurrentScrollPosition >= 0 && mCurrentScrollPosition <= mMovieGridAdapter.getItemCount()) {
            mMovieGrid.smoothScrollToPosition(mCurrentScrollPosition);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            private List<Movie> mVideoList = null;

            @Override
            protected void onStartLoading() {
                if (mVideoList != null) {
                    deliverResult(mVideoList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                try {
                    Cursor cursor = getContentResolver().query(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            null, null, null,
                            MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID);
                    List<Movie> movies = Data.fetchMovies(cursor, MainActivity.this);
                    cursor.close();
                    return movies;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load favourite movies", e);
                    return null;
                }
            }

            public void deliverResult(List<Movie> data) {
                mVideoList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data == null || data.isEmpty()) {
            showEmpty();
        } else {
            showData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieGridAdapter.setData(null);
    }
}
