package com.example.dex.redditclient.listings;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dex.redditclient.R;
import com.example.dex.redditclient.RetrofitHelper;

/**
 * Created by dex on 2/2/18.
 * Adapter for the fragments. Currently has only one fragment for HOT.
 * Can be extended to multiple categories.
 */

public class SectionAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SectionAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ListFragment.newInstance(RetrofitHelper.Options.HOT);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(R.string.hot);
    }
}