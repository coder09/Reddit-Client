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
        setContentView(R.layout.activity_web_view);

        Intent webViewIntent = getIntent();

        webUrl = webViewIntent.getStringExtra("Url");
        Log.d(TAG, "onCreate: url:" + webUrl);
        String subredditTitle = webViewIntent.getStringExtra("Subreddit");
        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.pb_webview);
        mProgressBar.setMax(100);

        loadWebView();


    }

    private void loadWebView() {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(webUrl));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));

    }
}
