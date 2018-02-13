package com.example.dex.redditclient.listings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dex.redditclient.R;
import com.example.dex.redditclient.listings.model.LChild;

import java.util.List;

/**
 * Created by dex on 2/2/18.
 * View adapter for each item in home screen.
 */

class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private static final String TAG = "ViewAdapter";
    private final List<LChild> mValues;
    private Context mContext;

    ViewAdapter(List<LChild> items, Context context) {

        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        try {

            holder.mPostTitle.setText(holder.mItem.data.title);
            holder.mPostUps.setText(holder.mItem.data.ups);
            holder.mPostTime.setText(holder.mItem.data.created);
            holder.mPostSource.setText(holder.mItem.data.domain);
            holder.mPostSubreddit.setText(holder.mItem.data.subreddit);

            final String mThumbnail = holder.mItem.data.thumbnail;

            final String mImageUrl = holder.mItem.data.preview.getImages().get(0).getSource().getUrl();

            if (isValidImageUrl(mImageUrl)) {

                final String id = holder.mItem.data.id;
                holder.mProgressBar.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(mImageUrl)
                        .asBitmap()
                        .error(R.drawable.reddit)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                holder.mProgressBar.setVisibility(View.GONE);
                                Glide.with(mContext)
                                        .load(mThumbnail)
                                        .into(holder.mPostThumbnail);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.mProgressBar.setVisibility(View.GONE);

                                return false;
                            }
                        })
                        .into(holder.mPostThumbnail);


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mPostTitle.setTextColor(Color.parseColor("#8BC34A"));
                        Context context = v.getContext();
                        Intent previewIntent = new Intent(context, PreviewActivity.class);

                        previewIntent.putExtra("mImageUrl", mImageUrl);
                        previewIntent.putExtra("mId", id);

                        context.startActivity(previewIntent);
                    }
                });
            }

            try {
                final String id = holder.mItem.data.id;

                final String mVideoUrl = holder.mItem.data.preview.reddit_video_preview.fallback_url;

                final boolean isGif = holder.mItem.data.preview.reddit_video_preview.is_gif;

                if (isGif) {
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.mPostTitle.setTextColor(Color.parseColor("#8BC34A"));
                            Context context = v.getContext();
                            Intent previewIntent = new Intent(context, PreviewActivity.class);
                            previewIntent.putExtra("mVideoUrl", mVideoUrl);
                            previewIntent.putExtra("mId", id);
                            context.startActivity(previewIntent);
                        }
                    });

                }


            } catch (NullPointerException e) {
                Log.d(TAG, "onBindViewHolder: " + e.getMessage());

            }


        } catch (NullPointerException e) {
            Log.d(TAG, "onBindViewHolder: ImageUrl ERROR :" + e.getMessage());
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mPostThumbnail.setVisibility(View.GONE);
        }
    }

    private boolean isValidImageUrl(String thumbnail) {
        return thumbnail != null && !thumbnail.isEmpty() && thumbnail.startsWith("http");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;

        private ImageView mPostThumbnail;
        private TextView mPostTitle;
        private TextView mPostUps;
        private TextView mPostTime;
        private TextView mPostSource;
        private TextView mPostSubreddit;
        private ProgressBar mProgressBar;

        private LChild mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;

            mPostThumbnail = view.findViewById(R.id.postThumbnail);
            mPostTitle = view.findViewById(R.id.post_title);
            mPostUps = view.findViewById(R.id.post_ups);
            mPostTime = view.findViewById(R.id.post_time);
            mPostSource = view.findViewById(R.id.post_imageSource);
            mPostSubreddit = view.findViewById(R.id.post_subreddit);
            mProgressBar = view.findViewById(R.id.post_ImageProgress);
        }
    }
}