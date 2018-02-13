package com.example.dex.redditclient.listings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dex on 4/2/18.
 */

public class LImages {

    @SerializedName("source")
    @Expose
    public LSource source;

    public LSource getSource() {
        return source;
    }

    public void setSource(LSource source) {
        this.source = source;
    }
}
