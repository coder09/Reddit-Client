package com.example.dex.redditclient.listings.model;

/**
 * Created by dex on 12/2/18.
 */

public class SubredditList {
    public String name;

    public SubredditList(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

