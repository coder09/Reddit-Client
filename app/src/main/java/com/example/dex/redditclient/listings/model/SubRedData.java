package com.example.dex.redditclient.listings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dex on 2/2/18.
 */

public class SubRedData {

    @SerializedName("children")
    @Expose
    public List<LChild> children = null;

    @SerializedName("after")
    @Expose
    public String after;

    @SerializedName("before")
    @Expose
    public String before;
}
