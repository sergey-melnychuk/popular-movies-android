package edu.android.udacity.project1.movies.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.android.udacity.project1.movies.myapplication.adapter.ReviewListAdapter;
import edu.android.udacity.project1.movies.myapplication.adapter.VideoListAdapter;
import edu.android.udacity.project1.movies.myapplication.data.MoviesContract;
import edu.android.udacity.project1.movies.myapplication.model.Movie;
import edu.android.udacity.project1.movies.myapplication.model.Review;
import edu.android.udacity.project1.movies.myapplication.model.Video;
import edu.android.udacity.project1.movies.myapplication.tasks.GetReviewsTask;
import edu.android.udacity.project1.movies.myapplication.tasks.GetVideosTask;
import edu.android.udacity.project1.movies.myapplication.util.Data;
import edu.android.udacity.project1.movies.myapplication.util.YouTube;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_movie_details_title)          TextView mTitleTextView;
    @BindView(R.id.iv_movie_details_poster)         ImageView mPosterImageView;
    @BindView(R.id.tv_movie_details_description)    TextView mDetailsTextView;
    @BindView(R.id.tv_details_year)                 TextView mYearTextView;
    @BindView(R.id.tv_details_rating)               TextView mRatingTextView;

    private VideoListAdapter mVideoListAdapter;
    @BindView(R.id.rv_videos_list)                  RecyclerView mVideosRecyclerView;
    @BindView(R.id.pb_videos_loading)               ProgressBar mVideosLoadingProgressBar;
    @BindView(R.id.tv_videos_loading_error)         TextView mVideosLoadingErrorTextView;

    private ReviewListAdapter mReviewListAdapter;
    @BindView(R.id.rv_reviews_list)                 RecyclerView mReviewsRecyclerView;
    @BindView(R.id.pb_reviews_loading)              ProgressBar mReviewsLoadingProgressBar;
    @BindView(R.id.tv_reviews_loading_error)        TextView mReviewsLoadingErrorTextView;

    @BindView(R.id.tv_reviews_section_header)       TextView mReviewsHeaderTextView;
    @BindView(R.id.tv_trailers_section_header)      TextView mVideosHeaderTextView;

    @BindView(R.id.btn_star)                        ImageButton mStarImageButton;

    private boolean mStarChecked;

    private Movie mMovie;

    private RequestQueue mRequestQueue;

    // REVIEW: Consider using ButterKnife library for view injection
    // https://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    // http://jakewharton.github.io/butterknife/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra("movie");
        mStarChecked = isStarred(mMovie);
        showStar(mStarChecked);
        mStarImageButton.setOnClickListener(this);

        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mVideosRecyclerView.setHasFixedSize(true);
        mVideoListAdapter = new VideoListAdapter(this, null);
        mVideosRecyclerView.setAdapter(mVideoListAdapter);

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews_list);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        mReviewListAdapter = new ReviewListAdapter(this, null);
        mReviewsRecyclerView.setAdapter(mReviewListAdapter);

        mTitleTextView.setText(mMovie.title);
        mDetailsTextView.setText(mMovie.overview);
        mYearTextView.setText(mMovie.releaseDate.substring(0,4));
        String rating = String.format("%1.2f / 10", mMovie.voteAverage);
        mRatingTextView.setText(rating);

        Context context = this;
        Picasso.with(context)
            .load(mMovie.posterImageUri)
            .config(Bitmap.Config.RGB_565)
            .error(R.drawable.icon_error)
            .placeholder(R.drawable.icon_placeholder)
            .fit()
            .into(mPosterImageView);

        mRequestQueue = Volley.newRequestQueue(this);

        GetVideosTask getVideosTask = new GetVideosTask(new VideoTaskListener());
        getVideosTask.execute(mMovie.id);

        GetReviewsTask getReviewsTask = new GetReviewsTask(new ReviewTaskListener());
        getReviewsTask.execute(mMovie.id);
    }

    private boolean isStarred(Movie movie) {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.id)).build();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    private void showStar(boolean checked) {
        if (checked) {
            mStarImageButton.setImageResource(R.drawable.icon_star_checked);
        } else {
            mStarImageButton.setImageResource(R.drawable.icon_star_empty);
        }
    }

    private void saveMovie(Movie movie) {
        ContentValues cv = Data.movie2cv(movie);
        Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
        if (uri != null) {
            Toast.makeText(this, "Movie \'" + movie.title + "\' starred", Toast.LENGTH_SHORT).show();
        }
        if (movie.posterImageUri != null) {
            savePoster(movie);
        }
    }

    private void savePoster(final Movie movie) {
        Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Data.save(DetailsActivity.this, String.valueOf(movie.id), response);
            }
        };

        ImageRequest imageRequest = new ImageRequest(
                movie.posterImageUri, listener, 240, 320, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, null);

        mRequestQueue.add(imageRequest);
    }

    private void removeMovie(Movie movie) {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.id)).build();
        getContentResolver().delete(uri, null, null);
        Toast.makeText(this, "Movie \'" + movie.title + "\' unstarred", Toast.LENGTH_SHORT).show();
        Data.delete(this, String.valueOf(movie.id));
        finish();
    }

    @Override
    public void onClick(View view) {
        if (mStarChecked) {
            removeMovie(mMovie);
        } else {
            saveMovie(mMovie);
        }
        mStarChecked = !mStarChecked;
        showStar(mStarChecked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mMovie != null && mVideoListAdapter.getData() != null && mVideoListAdapter.getData().size() > 0) {
                    String key = mVideoListAdapter.getData().get(0).key;
                    String url = YouTube.webUri(key).toString();
                    shareTrailerUrl(url);
                    return true;
                } else {
                    Toast.makeText(this, "No trailers to share", Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareTrailerUrl(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private class VideoTaskListener implements GetVideosTask.Listener {
        @Override
        public void showProgressBar() {
            mVideosRecyclerView.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setVisibility(View.INVISIBLE);
            mVideosHeaderTextView.setVisibility(View.INVISIBLE);
            mVideosLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void showData(List<Video> videos) {
            mVideoListAdapter.setData(videos);
            mVideoListAdapter.notifyDataSetChanged();
            mVideosRecyclerView.setVisibility(View.VISIBLE);
            mVideosLoadingProgressBar.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setVisibility(View.INVISIBLE);
            mVideosHeaderTextView.setVisibility(View.VISIBLE);
        }

        @Override
        public void showError() {
            mVideosLoadingProgressBar.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setVisibility(View.VISIBLE);
            mVideosHeaderTextView.setVisibility(View.VISIBLE);
            mVideosRecyclerView.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setText(getString(R.string.error_message));
        }

        @Override
        public void showEmpty() {
            mVideosLoadingProgressBar.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setVisibility(View.VISIBLE);
            mVideosHeaderTextView.setVisibility(View.VISIBLE);
            mVideosRecyclerView.setVisibility(View.INVISIBLE);
            mVideosLoadingErrorTextView.setText(getString(R.string.no_items_available));
        }
    }

    private class ReviewTaskListener implements GetReviewsTask.Listener {
        @Override
        public void showProgressBar() {
            mReviewsRecyclerView.setVisibility(View.INVISIBLE);
            mReviewsLoadingErrorTextView.setVisibility(View.INVISIBLE);
            mReviewsHeaderTextView.setVisibility(View.VISIBLE);
            mReviewsLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void showData(List<Review> videos) {
            mReviewListAdapter.setData(videos);
            mReviewListAdapter.notifyDataSetChanged();
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
            mReviewsLoadingProgressBar.setVisibility(View.INVISIBLE);
            mReviewsLoadingErrorTextView.setVisibility(View.INVISIBLE);
            mReviewsHeaderTextView.setVisibility(View.VISIBLE);
        }

        @Override
        public void showError() {
            mReviewsLoadingProgressBar.setVisibility(View.INVISIBLE);
            mReviewsHeaderTextView.setVisibility(View.VISIBLE);
            mReviewsLoadingErrorTextView.setVisibility(View.VISIBLE);
            mReviewsLoadingErrorTextView.setText(getString(R.string.error_message));
        }

        @Override
        public void showEmpty() {
            mReviewsLoadingProgressBar.setVisibility(View.INVISIBLE);
            mReviewsHeaderTextView.setVisibility(View.VISIBLE);
            mReviewsLoadingErrorTextView.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.INVISIBLE);
            mReviewsLoadingErrorTextView.setText(getString(R.string.no_items_available));
        }
    }

}
