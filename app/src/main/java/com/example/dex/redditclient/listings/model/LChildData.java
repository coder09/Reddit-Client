package com.example.dex.redditclient.listings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dex on 2/2/18.
 */

public class LChildData {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("author")
    @Expose
    public String author;


    @SerializedName("ups")
    @Expose
    public String ups;

    @SerializedName("num_comments")
    @Expose
    public String num_comments;


    @SerializedName("subreddit")
    @Expose
    public String subreddit;


    @SerializedName("domain")
    @Expose
    public String domain;


    @SerializedName("created")
    @Expose
    public String created;


    @SerializedName("preview")
    @Expose
    public LPreview preview;

    public LPreview getPreview() {
        return preview;
    }


}
