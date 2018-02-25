package com.example.dex.redditclient.listings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dex.redditclient.R;
import com.example.dex.redditclient.listings.model.SubredditList;

import java.util.ArrayList;

/**
 * Created by dex on 12/2/18.
 */
public class NavListAdapter extends ArrayAdapter<SubredditList> {

    private Context mContext;


    public NavListAdapter(@NonNull Context context, ArrayList<SubredditList> mList) {
        super(context, R.layout.nav_listitems, mList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SubredditList subredditList = getItem(position);

        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.nav_listitems, parent, false);

            viewHolder.title = convertView.findViewById(R.id.nav_tvlist);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(subredditList.name);


        return convertView;
    }

    private static class ViewHolder {

        TextView title;


    }
}
