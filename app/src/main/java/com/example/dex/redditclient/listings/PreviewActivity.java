package com.example.dex.redditclient.listings;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = "PreviewActivity";
    private ProgressBar mProgressBar;
    private PhotoView photoView;
    private String imgUrl, videoUrl, mId;
    private Boolean mGif;
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setHomeButtonEnabled(true);


        ActivityCompat.requestPermissions(PreviewActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        photoView = findViewById(R.id.photo_view);
        mProgressBar = findViewById(R.id.preview_progressbar);
        exoPlayerView = findViewById(R.id.mExoPlayer);

        // Getting Intent
        Intent previewIntent = getIntent();
        imgUrl = previewIntent.getStringExtra("mImageUrl");
        mId = previewIntent.getStringExtra("mId");

        if (isValidImageUrl(imgUrl)) {
            showPicture(imgUrl);
        } else {
            videoUrl = previewIntent.getStringExtra("mVideoUrl");
            mGif = previewIntent.getBooleanExtra("isGif", false);


            showGif(videoUrl);
        }


//
//        if (!mGif) {
//
//            showPicture(imgUrl);
//
//        } else {
//            showGif(videoUrl);
//        }


//        } else {
//
//            mProgressBar.setVisibility(View.GONE);
//            photoView.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Unable to load ", Toast.LENGTH_SHORT).show();
//        }


    }

    private boolean isValidImageUrl(String thumbnail) {
        return thumbnail != null && !thumbnail.isEmpty() && thumbnail.startsWith("http");
    }

    private void showGif(String videoUrl) {
        try {
            exoPlayerView.setVisibility(View.VISIBLE);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            Uri videoURI = Uri.parse(videoUrl);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        } catch (Exception e) {
            Log.e(TAG, "Preview Activity: " + e.getMessage());
        }
    }

    private void showPicture(String imgUrl) {
        mProgressBar.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);

        Glide.with(PreviewActivity.this)
                .load(imgUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
                .into(photoView);

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

//            case R.id.action_login:
//                Intent intent = new Intent(PreviewActivity.this, LoginActivity.class);
//                startActivity(intent);
//                break;


            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void download_image() {
        Glide.with(PreviewActivity.this)
                .load(imgUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        saveImage(resource);
                    }
                });
    }

    private String saveImage(Bitmap image) {
        Log.d(TAG, "saveImage: called");
        String savedImagePath = null;
        String imageFileName = "";
        imageFileName = mId + ".jpg";

//        if (matcher1.matches()) {
//            imageFileName = post_id + ".jpg";
//            Log.d(TAG, "saveImage: jpg called");
//        }
//        else {
//           // imageFileName = post_id + ".gif";
//            Log.d(TAG, "saveImage: gif called");
//
//        }

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
}





