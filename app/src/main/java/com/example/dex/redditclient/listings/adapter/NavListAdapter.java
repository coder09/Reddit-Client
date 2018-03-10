package com.example.dex.redditclient.listings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dex.redditclient.R;
import com.example.dex.redditclient.listings.model.SubredditList;

import java.util.ArrayList;

/**
 * Created by dex on 12/2/18.
 */
public class NavListAdapter extends ArrayAdapter<SubredditList> {


    public NavListAdapter(@NonNull Context context, ArrayList<SubredditList> list_item) {
        super(context, 0, list_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SubredditList sub_list = getItem(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_listitems, parent, false);

        }

        TextView tvName = convertView.findViewById(R.id.nav_tvlist);
        ImageView iv_rating = convertView.findViewById(R.id.iv_rating);

        tvName.setText(sub_list.name);


        return convertView;
    }

}
