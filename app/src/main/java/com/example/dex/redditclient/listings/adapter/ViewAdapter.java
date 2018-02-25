package com.example.dex.redditclient.listings.adapter;

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
import com.example.dex.redditclient.listings.GetTimeAgo;
import com.example.dex.redditclient.listings.PreviewActivity;
import com.example.dex.redditclient.listings.model.LChild;

import java.util.List;

/**
 * Created by dex on 2/2/18.
 * View adapter for each item in home screen.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private static final String TAG = "ViewAdapter";
    private final List<LChild> mValues;
    private Context mContext;

    public ViewAdapter(List<LChild> items, Context context) {
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
            Log.d(TAG, "Title: " + holder.mItem.data.title + ",\n position: " + position);

            holder.mPostTitle.setText(holder.mItem.data.title);
            holder.mPostUps.setText(holder.mItem.data.ups);
            holder.mPostDomain.setText(holder.mItem.data.domain);
            holder.mPostSubreddit.setText(holder.mItem.data.subreddit);

            Long postTime = holder.mItem.data.created;

            String postCreatedTime = GetTimeAgo.getTimeAgo(postTime, mContext);

            holder.mPostTime.setText(postCreatedTime);


            final String mThumbnail = holder.mItem.data.thumbnail;

            final String mUrl = holder.mItem.data.url;

            final String mImageUrl = holder.mItem.data.preview.getImages().get(0).getSource().getUrl();


            if (mThumbnail.equals("") || mThumbnail.equals("self")) {

                holder.mProgressBar.setVisibility(View.GONE);
                holder.mErrorThumbnail.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(R.drawable.reddit)
                        .into(holder.mErrorThumbnail);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mPostTitle.setTextColor(Color.parseColor("#8BC34A"));
                        Context context = v.getContext();
                        Intent previewIntent = new Intent(context, PreviewActivity.class);
                        previewIntent.putExtra("mImageUrl", mUrl);
                        previewIntent.putExtra("mSubreddit", holder.mItem.data.subreddit);
                        context.startActivity(previewIntent);

                    }
                });


            } else {

                final String resultImageUrl = mImageUrl.replace("&amp;", "&");

                final String id = holder.mItem.data.id;

                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.mPostThumbnail.setVisibility(View.VISIBLE);
//                holder.mErrorThumbnail.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(resultImageUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                holder.mProgressBar.setVisibility(View.GONE);
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
//                        previewIntent.putExtra("mVideoUrl", mVideoUrl);
                        previewIntent.putExtra("mId", id);
                        previewIntent.putExtra("mImageUrl", resultImageUrl);
                        previewIntent.putExtra("mSubreddit", holder.mItem.data.subreddit);
                        context.startActivity(previewIntent);
                    }
                });


            }

            try {
//                Log.d(TAG, "onBindViewHolder: video block called");
                final String id = holder.mItem.data.id;

                final String mVideoUrl = holder.mItem.data.preview.reddit_video_preview.fallback_url;

//                final boolean isGif = holder.mItem.data.preview.reddit_video_preview.is_gif;

//                if (isGif) {
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mPostTitle.setTextColor(Color.parseColor("#8BC34A"));
                        Context context = v.getContext();
                        Intent previewIntent = new Intent(context, PreviewActivity.class);
                        previewIntent.putExtra("mVideoUrl", mVideoUrl);
                        previewIntent.putExtra("mId", id);
                        previewIntent.putExtra("mSubreddit", holder.mItem.data.subreddit);
                        context.startActivity(previewIntent);
                    }
                });

//                }


            } catch (NullPointerException e) {
//                Log.d(TAG, "onBindViewHolder: " + e.getMessage());

            }


        } catch (NullPointerException e) {
//            Log.d(TAG, "onBindViewHolder: ImageUrl ERROR :" + e.getMessage());
        }
    }

//    private void ValidateImageUrl(String thumbnail) {
//        String resultUrl = thumbnail.replace("&amp;", "&");
//        Log.d(TAG, "isValidImageUrl: originalUrl: " + thumbnail);
//        Log.d(TAG, "isValidImageUrl: resultUrl: " + resultUrl);
//    }

//    private boolean isValidImageUrl(String thumbnail) {
//        return thumbnail != null && !thumbnail.isEmpty() && thumbnail.startsWith("http");
//    }

//    private boolean isGif(String url) {
//        return url.endsWith(".gifv") || url.endsWith(".gif");
//    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        GetTimeAgo getTimeAgo;
        private ImageView mPostThumbnail;
        private ImageView mErrorThumbnail;
        private TextView mPostTitle;
        private TextView mPostUps;
        private TextView mPostTime;
        private TextView mPostDomain;
        private TextView mPostSubreddit;
        private ProgressBar mProgressBar;
        private LChild mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;

            getTimeAgo = new GetTimeAgo();
            mPostThumbnail = view.findViewById(R.id.postThumbnail);
            mPostTitle = view.findViewById(R.id.post_title);
            mPostUps = view.findViewById(R.id.post_ups);
            mPostTime = view.findViewById(R.id.post_time);
            mPostDomain = view.findViewById(R.id.post_imageSource);
            mPostSubreddit = view.findViewById(R.id.post_subreddit);
            mProgressBar = view.findViewById(R.id.post_ImageProgress);
            mErrorThumbnail = view.findViewById(R.id.iv_error_image);
        }
    }
}