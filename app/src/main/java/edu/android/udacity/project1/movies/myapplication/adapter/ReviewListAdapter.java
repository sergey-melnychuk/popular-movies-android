package edu.android.udacity.project1.movies.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.R;
import edu.android.udacity.project1.movies.myapplication.model.Review;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private List<Review> mReviews;
    private Activity mActivity;

    public ReviewListAdapter(Activity activity, List<Review> videos) {
        mActivity = activity;
        mReviews = videos;
    }

    public void setData(List<Review> videos) {
        mReviews = videos;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private Review mReview;
        private TextView mReviewAuthorTextView;
        private TextView mReviewContentTextView;

        public ReviewViewHolder(View view) {
            super(view);
            mReviewAuthorTextView = (TextView) view.findViewById(R.id.tv_review_author);
            mReviewContentTextView = (TextView) view.findViewById(R.id.tv_review_content);
        }

        public void bind(int position) {
            mReview = mReviews.get(position);
            mReviewAuthorTextView.setText(mReview.author);
            mReviewContentTextView.setText(mReview.content);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mReviews != null) {
            return mReviews.size();
        } else {
            return 0;
        }
    }
}
