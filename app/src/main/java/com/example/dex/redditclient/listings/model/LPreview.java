package com.example.dex.redditclient.listings.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dex on 4/2/18.
 */

public class LPreview {

    @SerializedName("images")
    @Expose
    public List<LImages> images = new ArrayList<>();

    @SerializedName("reddit_video_preview")
    @Expose
    public LVideoPreview reddit_video_preview;

    public List<LImages> getImages() {
        return images;
    }

    public void setImages(List<LImages> images) {
        this.images = images;
    }

}
