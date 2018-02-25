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
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    private PhotoView mPhotoView;
    private String mImgUrl;
    private String mId;
    private SimpleExoPlayerView mExoplayerView;
    private SimpleExoPlayer mExoplayer;
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Getting write permission from user
        ActivityCompat.requestPermissions(PreviewActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        mWebView = findViewById(R.id.webview);
        mPhotoView = findViewById(R.id.photo_view);
        mProgressBar = findViewById(R.id.preview_progressbar);
        mExoplayerView = findViewById(R.id.mExoPlayer);

        // Getting Intent
        Intent previewIntent = getIntent();
        mImgUrl = previewIntent.getStringExtra("mImageUrl");
        mId = previewIntent.getStringExtra("mId");
        String mSubreddit = previewIntent.getStringExtra("mSubreddit");
        getSupportActionBar().setTitle("/r/" + mSubreddit);

        if (isValidImageUrl(mImgUrl)) {
            showPicture(mImgUrl);
        } else {
            String mVideoUrl = previewIntent.getStringExtra("mVideoUrl");
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

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
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

    private void showPicture(final String imgUrl) {
        Log.d(TAG, "onCreate: Received url: " + mImgUrl);
        mProgressBar.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);

        Glide.with(PreviewActivity.this)
                .load(imgUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        mProgressBar.setVisibility(View.GONE);
//                        Toast.makeText(PreviewActivity.this, "Unable to load!", Toast.LENGTH_SHORT).show();

                        mWebView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);

//                        mWebView.getSettings().setJavaScriptEnabled(true);
                        Log.d(TAG, "onCreate: Received url: " + mImgUrl);

                        mWebView.setWebViewClient(new WebViewClient());
                        mWebView.getSettings().setDomStorageEnabled(true);
                        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

                        mWebView.loadUrl(String.valueOf(imgUrl));

                        mWebView.setHorizontalScrollBarEnabled(false);

                        mWebView.setWebViewClient(new WebViewClient() {

                            @Override
                            public void onPageCommitVisible(WebView view, String url) {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });

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
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void download_image() {
        Glide.with(PreviewActivity.this)
                .load(mImgUrl)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

//        if (mExoplayerView.hasWindowFocus()) {
//            mExoplayerView.
//        } else {
//            finish();
//
//        }
    }
}





