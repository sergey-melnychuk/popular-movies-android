package edu.android.udacity.project1.movies.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.DetailsActivity;
import edu.android.udacity.project1.movies.myapplication.R;
import edu.android.udacity.project1.movies.myapplication.model.Movie;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {
    private List<Movie> mMovieList;
    private Activity mActivity;

    public MovieGridAdapter(Activity activity, List<Movie> movieList) {
        mActivity = activity;
        mMovieList = movieList;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView posterImageView;
        private TextView titleTextView;
        private Movie mMovie;
        private Context context = this.itemView.getContext();

        public MovieViewHolder(View view) {
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.iv_movie_image);
            titleTextView = (TextView) view.findViewById(R.id.tv_movie_title);
            view.setOnClickListener(this);
        }

        public void bind(int position) {
            mMovie = mMovieList.get(position);
            titleTextView.setText(mMovie.title);
            Picasso.with(context)
                .load(mMovie.posterImageUri)
                .error(R.drawable.icon_error)
                .placeholder(R.drawable.icon_placeholder)
                .fit()
                .into(posterImageView);

            // REVIEW: Consider using .error() and .placeholder() calls of Picasso
            // https://futurestud.io/tutorials/picasso-placeholders-errors-and-fading
            // SOLUTION: Added .placeholder() and .error()
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), DetailsActivity.class);
            intent.putExtra("movie", mMovie);
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                mActivity.startActivity(intent);
            }
        }
    }

    public void setData(List<Movie> movies) {
        mMovieList = movies;
    }

    @Override
    public MovieGridAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int itemLayoutId = R.layout.image_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(itemLayoutId, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieGridAdapter.MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMovieList != null) {
            return mMovieList.size();
        } else {
            return 0;
        }
    }
}
