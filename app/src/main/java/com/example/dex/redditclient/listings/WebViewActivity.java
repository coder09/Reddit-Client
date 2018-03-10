package com.example.dex.redditclient.listings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.dex.redditclient.R;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";

    private WebView mWebView;
    private String webUrl;
    private ProgressBar mProgressBar;


    private CustomTabsClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_view);


        Intent webViewIntent = getIntent();

        webUrl = webViewIntent.getStringExtra("Url");
        Log.d(TAG, "onCreate: url:" + webUrl);
        String subredditTitle = webViewIntent.getStringExtra("Subreddit");
//
//        getSupportActionBar().setTitle("/r/" + subredditTitle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.pb_webview);
        mProgressBar.setMax(100);

        loadWebView();


    }

    private void loadWebView() {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

//        customTabsIntent.intent.setPackage("com.android.chrome");
//        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(webUrl));

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.webview_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.action_fullscreen:
//                makeFullscreen();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void makeFullscreen() {
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));

    }

//    private void exitFullscreen() {
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//    }
}
