package com.example.dex.redditclient.listings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dex on 2/2/18.
 */

public class LChild {

    @SerializedName("data")
    @Expose
    public LChildData data;

}
