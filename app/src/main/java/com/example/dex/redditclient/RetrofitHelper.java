package com.example.dex.redditclient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dex.redditclient.listings.model.LResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dex on 2/2/18.
 */

public class RetrofitHelper {

    public static final String DOWNLOAD_COMPLETE = "download_complete";
    public static final String DOWNLOAD_COMMENTS_COMPLETE = "download_comments_complete";
    private static final String BASE_URL = "http://www.reddit.com/";
    private static final int LIMIT = 25;
    private static String mSubreddit;
    private APIEndPoint mEndPoint;
    //    private CResult mComments;
    private LResult mResult;
    private Context mContext;
    private String prevSubReddit;

    public RetrofitHelper(Context context) {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mEndPoint = mRetrofit.create(APIEndPoint.class);
        mContext = context;
    }

    public void download(String subreddit) {
        prevSubReddit = mSubreddit;
        mSubreddit = subreddit;

        Call<LResult> call = mEndPoint.getHot(subreddit, LIMIT);
        enqueueCall(call);
    }

    public void download(String subreddit, String query, boolean after) {
        Call<LResult> call = after ? mEndPoint.getHotAfter(subreddit, query, LIMIT)
                : mEndPoint.getHotBefore(subreddit, query, LIMIT);
        enqueueCall(call);
    }

    private void enqueueCall(Call<LResult> call) {


        call.enqueue(new Callback<LResult>() {
            @Override
            public void onResponse(Call<LResult> call, Response<LResult> response) {
                if (response.body() != null) {
                    mResult = response.body();
                    prevSubReddit = mSubreddit;
                } else {
                    mSubreddit = prevSubReddit;
                    Toast.makeText(mContext, "No results for this search term!", Toast.LENGTH_SHORT).show();
                }
                mContext.sendBroadcast(new Intent(DOWNLOAD_COMPLETE));
            }

            @Override
            public void onFailure(Call<LResult> call, Throwable t) {
                Log.e("Listing error: ", t.getMessage());
            }
        });
    }


//    public void downloadComments(String listingId) {
//        Call<List<CResult>> call = mEndPoint.getComments(mSubreddit, listingId);
//        Log.e("anindya", mSubreddit + " "+ listingId);
//        enqueueComment(call);
//    }

//    private void enqueueComment(Call<List<CResult>> call) {
//        call.enqueue(new Callback<List<CResult>>() {
//            @Override
//            public void onResponse(Call<List<CResult>> call, Response<List<CResult>> response) {
//                mComments = response.body().get(1); //index 0 has the article.
//                mContext.sendBroadcast(new Intent(DOWNLOAD_COMMENTS_COMPLETE));
//            }
//
//            @Override
//            public void onFailure(Call<List<CResult>> call, Throwable t) {
//                Log.e("anindya", t.getMessage());
//            }
//        });
//    }

    public LResult getResult() {
        return mResult;
    }

    public String getSubReddit() {
        return prevSubReddit;
    }

//    public CResult getComments() {
//        return mComments;
//    }

    public enum Options {HOT, NEW, RISING, RANDOM}
}
