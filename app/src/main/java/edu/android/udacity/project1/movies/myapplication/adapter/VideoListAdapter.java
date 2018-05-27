package edu.android.udacity.project1.movies.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.android.udacity.project1.movies.myapplication.R;
import edu.android.udacity.project1.movies.myapplication.model.Video;
import edu.android.udacity.project1.movies.myapplication.util.YouTube;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {
    private List<Video> mVideos;
    private Activity mActivity;

    public VideoListAdapter(Activity activity, List<Video> videos) {
        mActivity = activity;
        mVideos = videos;
    }

    public List<Video> getData() {
        return mVideos;
    }

    public void setData(List<Video> videos) {
        mVideos = videos;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Video mVideo;
        private ImageView mPosterImageView;

        public VideoViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.iv_trailer_thumbnail);
            view.setOnClickListener(this);
        }

        public void bind(int position) {
            mVideo = mVideos.get(position);
            String imageUrl = YouTube.thumbnail(mVideo.key);

            Picasso.with(mActivity)
                .load(imageUrl)
                .error(R.drawable.icon_error)
                .placeholder(R.drawable.icon_play)
                .fit()
                .into(mPosterImageView);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, YouTube.appUri(mVideo.key));
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                mActivity.startActivity(intent);
            } else {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, YouTube.webUri(mVideo.key));
                if (webIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                    mActivity.startActivity(webIntent);
                } else {
                    Toast.makeText(view.getContext(), "Failed to start video", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mVideos != null) {
            return mVideos.size();
        } else {
            return 0;
        }
    }
}
