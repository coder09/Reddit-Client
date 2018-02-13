package com.example.dex.redditclient.listings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dex on 13/2/18.
 */

public class LVideoPreview {

    @SerializedName("fallback_url")
    @Expose
    public String fallback_url;


    @SerializedName("is_gif")
    @Expose
    public Boolean is_gif;


}
