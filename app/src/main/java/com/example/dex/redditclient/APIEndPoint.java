package com.example.dex.redditclient;

import com.example.dex.redditclient.listings.model.LResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dex on 2/2/18.
 */

public interface APIEndPoint {

    @GET("r/{subreddit}/hot/.json")
    Call<LResult> getHot(@Path("subreddit") String subreddit, @Query("limit") int limit);

    @GET("r/{subreddit}/hot/.json")
    Call<LResult> getHotAfter(@Path("subreddit") String subreddit, @Query("after") String next, @Query("limit") int limit);

    @GET("r/{subreddit}/hot/.json")
    Call<LResult> getHotBefore(@Path("subreddit") String subreddit, @Query("before") String next, @Query("limit") int limit);

//    @GET("r/{subreddit}/comments/{article}/.json")
//    Call<List<CResult>> getComments(@Path("subreddit") String subreddit, @Path("article") String article);
}
