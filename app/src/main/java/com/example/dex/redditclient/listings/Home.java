package com.example.dex.redditclient.listings;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dex.redditclient.R;
import com.example.dex.redditclient.RetrofitHelper;
import com.example.dex.redditclient.listings.model.SubredditList;

import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Home";


    private RetrofitHelper mRetroHelper;
    //    private String mQuery = "popular";
    private SearchView searchView;

    private EditText et_subreddit;
    private ImageView et_clear, btnSearch;
    private ListView mDrawerList;
    private String currentFeed = "popular";
    private String feedName;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();

        ViewPager mViewPager = findViewById(R.id.viewpager);

        mRetroHelper = new RetrofitHelper(context);

        SectionAdapter mSectionAdapter = new SectionAdapter(getSupportFragmentManager(), context);
        mViewPager.setAdapter(mSectionAdapter);
//        handleIntent(getIntent());

        //Static call for subreddit
        mRetroHelper.download(currentFeed);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Nav drawer listItems
        View headerview = navigationView.getHeaderView(0);
        et_subreddit = findViewById(R.id.etSubreddit);
        et_clear = findViewById(R.id.btnclose);
        btnSearch = findViewById(R.id.btnSearch);


        // Construct the data source
        final ArrayList<SubredditList> subredditLists = new ArrayList<SubredditList>();

        // Create the adapter to convert the array to views
        final NavListAdapter adapter = new NavListAdapter(this, subredditLists);

        // Attach the adapter to a ListView
        mDrawerList = findViewById(R.id.list_view);
        mDrawerList.setHeaderDividersEnabled(true);
        mDrawerList.setFooterDividersEnabled(true);
        mDrawerList.setTextFilterEnabled(true);

        mDrawerList.setAdapter(adapter);

        subredditLists.add(new SubredditList("ask"));
        subredditLists.add(new SubredditList("pic"));
        subredditLists.add(new SubredditList("funny"));
        subredditLists.add(new SubredditList("cats"));
        subredditLists.add(new SubredditList("cars"));
        subredditLists.add(new SubredditList("dogs"));
        subredditLists.add(new SubredditList("gifs"));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView mSubList = view.findViewById(R.id.nav_tvlist);
                String feedName = mSubList.getText().toString();

//                Log.d(TAG, "onItemClick: " + feedName);
                if (!feedName.equals("")) {
                    currentFeed = feedName;
                    downloadPost(currentFeed);
                } else {
                    downloadPost(currentFeed);
                }
                hideSoftKeyboard();

//                Log.d(TAG, "onClick: button " + feedName);

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setColorScheme(
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark

        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                downloadPost(currentFeed);

            }
        });

        et_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_subreddit.setText("");
            }
        });

        // search icon listener in keyboard
        et_subreddit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        | keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    feedName = et_subreddit.getText().toString();

                    if (!feedName.equals("")) {
                        currentFeed = feedName;
                        downloadPost(currentFeed);
                    } else {
                        downloadPost(currentFeed);
                    }

                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                }

                return false;
            }
        });


    }

    private void downloadPost(String currentFeed) {

        mSwipeRefreshLayout.setRefreshing(false);

        mRetroHelper.download(currentFeed);

    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        handleIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            searchView.setQuery("", false);
//            searchView.setIconified(true);
//            mQuery = intent.getStringExtra(SearchManager.QUERY);
//            Log.e("anindya", mQuery);
//            mRetroHelper.download(mQuery);
//        }
//    }


    public RetrofitHelper getHelper() {
        return mRetroHelper;
    }

    public String getQuery() {
        return currentFeed;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(true);
            downloadPost(currentFeed);


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
