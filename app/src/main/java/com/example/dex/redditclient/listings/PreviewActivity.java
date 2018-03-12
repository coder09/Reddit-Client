package com.example.dex.redditclient.listings;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.example.dex.redditclient.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = "PreviewActivity";
    private ProgressBar mProgressBar;
    private PhotoView mPhotoView;
    private String mPostImageUrl;
    private String mPostId;
    private SimpleExoPlayerView mExoplayerView;
    private SimpleExoPlayer mExoplayer;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Getting write permission from user
        ActivityCompat.requestPermissions(PreviewActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        mPhotoView = findViewById(R.id.photo_view);
        mProgressBar = findViewById(R.id.preview_progressbar);
        mExoplayerView = findViewById(R.id.mExoPlayer);
        mProgressDialog = new ProgressDialog(this, R.style.MyDialogTheme);

        // Getting Intent
        Intent previewIntent = getIntent();

        mPostImageUrl = previewIntent.getStringExtra("PostUrl");
        mPostId = previewIntent.getStringExtra("PostId");
        String mPostTitle = previewIntent.getStringExtra("PostTitle");

        getSupportActionBar().setTitle(mPostTitle);

        if (isValidImageUrl(mPostImageUrl)) {
            showPicture(mPostImageUrl);
        } else {
            String mVideoUrl = previewIntent.getStringExtra("PostVideoUrl");

            Log.d(TAG, "onCreate: Video Url: " + mVideoUrl);
            showGif(mVideoUrl);
        }
    }

    private boolean isValidImageUrl(String thumbnail) {
        return thumbnail != null && !thumbnail.isEmpty() && thumbnail.startsWith("http");
    }

    private void showGif(String videoUrl) {
        try {
            mProgressBar.setVisibility(View.GONE);
            mExoplayerView.setVisibility(View.VISIBLE);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            mExoplayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            Uri videoURI = Uri.parse(videoUrl);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exop`layer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            mExoplayerView.setPlayer(mExoplayer);
            mExoplayer.prepare(mediaSource);
            mExoplayer.setPlayWhenReady(true);
            mExoplayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        } catch (Exception e) {
            Log.e(TAG, "Preview Activity: " + e.getMessage());
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        Log.d(TAG, "releasePlayer: called");
        if (mExoplayer != null) {
            mExoplayer.release();
            mExoplayer = null;
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void showPicture(final String imgUrl) {
//        Log.d(TAG, "onCreate: Received url: " + mPostImageUrl);

        mPhotoView.setVisibility(View.VISIBLE);
        Glide.with(PreviewActivity.this)
                .load(imgUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mPhotoView);
    }

    private void download_image() {
        Glide.with(PreviewActivity.this)
                .load(mPostImageUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mProgressDialog.dismiss();
                        saveImage(resource);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        mProgressDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Downloading Image</font>"));
                        mProgressDialog.show();
                    }
                });
    }

    private String saveImage(Bitmap image) {
//        Log.d(TAG, "saveImage: called");
        String savedImagePath = null;
        String imageFileName = "";
        imageFileName = mPostId + ".jpg";

        String folderName = "/RedditClient";

        File storageDir = new File(Environment.getExternalStorageDirectory() + folderName);

        boolean success = true;

        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();

            if (imageFile.exists()) {
                Toast.makeText(this, "File Already Exists", Toast.LENGTH_SHORT).show();

            } else {
                try {
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                galleryAddPic(savedImagePath);

                Toast.makeText(PreviewActivity.this, "IMAGE SAVED", Toast.LENGTH_LONG).show();

            }
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        super.onBackPressed();
        finish();
        releasePlayer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_preview, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_download:
                download_image();
                break;

            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}





