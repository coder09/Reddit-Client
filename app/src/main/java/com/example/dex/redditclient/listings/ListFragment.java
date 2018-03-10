package com.example.dex.redditclient.listings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dex.redditclient.R;
import com.example.dex.redditclient.listings.adapter.ViewAdapter;
import com.example.dex.redditclient.listings.model.LChild;
import com.example.dex.redditclient.listings.model.LResult;
import com.example.dex.redditclient.utils.RetrofitHelper;

import java.util.List;

/**
 * Created by dex on 2/2/18.
 * <p>
 * A fragment to hold the ListView (RecyclerView) of the listings.
 */

public class ListFragment extends Fragment {

    List<LChild> mItemList;
    Context mContext;
    private View rootView;
    private LResult mResult;
    private RetrofitHelper mHelper;
    private int count = 0;
    private ProgressBar mProgressBar;
    private int pageCount = 1;
    private ViewAdapter viewAdapter;



    private EndlessRecyclerViewScrollListener scrollListener;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                LResult temp = ((MainActivity) getActivity()).getHelper().getResult();

                if (temp.data.children.size() > 0) {
                    getActivity().setTitle("/r/" + mHelper.getSubReddit());
                    mResult = temp;
                    mItemList = mResult.data.children;
                    setupRecyclerView();
                }
            } catch (Exception e) {
                getActivity().setTitle("");
                Toast.makeText(getContext(), "No results for this search term!", Toast.LENGTH_SHORT).show();
                Log.e("ListFragment: ", e.getMessage());
            }
        }
    };

    public ListFragment() {
    }

    public static ListFragment newInstance(RetrofitHelper.Options option) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable("option", option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mHelper = ((MainActivity) getActivity()).getHelper();
        mProgressBar = rootView.findViewById(R.id.fragment_progressbar);

//        mProgressBar.setVisibility(View.VISIBLE);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = rootView.getContext();
        context.registerReceiver(mReceiver, new IntentFilter(RetrofitHelper.DOWNLOAD_COMPLETE));
    }

    private void setupRecyclerView() {
        mProgressBar.setVisibility(View.VISIBLE);

        RecyclerView itemList = rootView.findViewById(R.id.item_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setHasFixedSize(true);
        itemList.setLayoutManager(linearLayoutManager);
        itemList.setItemAnimator(new DefaultItemAnimator());

        // Adapter settings
        viewAdapter = new ViewAdapter(mItemList, getContext());
        itemList.setAdapter(viewAdapter);

//        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                loadNext(page);
//            }
//        };
        // Adds the scroll listener to RecyclerView
//        itemList.addOnScrollListener(scrollListener);




//        itemList.setAdapter(new ViewAdapter(mItemList, getContext()));

//        itemList.addOnScrollListener(new EndlessRecyclerViewScrollListener() {
//            @Override
//            public void onLoadMore() {
//                loadNext();
//            }
//        });
        mProgressBar.setVisibility(View.INVISIBLE);
//

    }


//        final Button next = rootView.findViewById(R.id.next);
//        final Button prev = rootView.findViewById(R.id.prev);

//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity h = (MainActivity) getActivity();
//                int lastChildIndex = mResult.data.children.size() - 1;
//                mHelper.download(h.getQuery(), mResult.data.children.get(lastChildIndex).data.name, true);
//                count += 1;
//            }
//        });
//
//        prev.setEnabled(count > 0);
//        prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count -= 1;
//                if (count >= 0) {
//                    MainActivity h = (MainActivity) getActivity();
//                    mHelper.download(h.getQuery(), mResult.data.children.get(0).data.name, false);
//                } else {
//                    prev.setEnabled(false);
//                }
//            }
//        });


    //--------Infinite Scrolling----------
//        itemList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                loadNext();
//
//            }
//        });
//
//
//    }

//    public void loadNext(int page) {
//        pageCount++;
//
//        MainActivity h = (MainActivity) getActivity();
//        int lastChildIndex = mResult.data.children.size() - 1;
//        String query = mResult.data.children.get(lastChildIndex).data.name;
//        mHelper.download(h.getQuery(),query, true);
//        count += 1;
////        viewAdapter.notifyDataSetChanged();
//
//
//
//        Toast.makeText(getContext(), "Page " + pageCount, Toast.LENGTH_SHORT).show();
//
//    }


}



