package com.waracle.androidtest;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public class PlaceholderFragment extends ListFragment {

    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    private ListView mListView;
    private MyAdapter mAdapter;
    private MainActivityModel model;

    public PlaceholderFragment() { /**/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(android.R.id.list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create and set the list adapter.
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        // Load data from net.
        if (getActivity() != null) {
            model = ViewModelProviders.of(getActivity()).get(MainActivityModel.class);
            model.getCakes().observe(this, new Observer<List<Cake>>() {
                @Override
                public void onChanged(@Nullable List<Cake> cakes) {
                    mAdapter.setItems(cakes);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class MyAdapter extends BaseAdapter {

        // Can you think of a better way to represent these items???
        private List<Cake> cakes;
        private ImageLoader mImageLoader;

        MyAdapter() {
            this(new ArrayList<Cake>());
        }

        MyAdapter(List<Cake> items) {
            cakes = items;
            mImageLoader = new ImageLoader();
        }

        @Override
        public int getCount() {
            return cakes.size();
        }

        @Override
        public Cake getItem(int position) {
            return cakes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View root = inflater.inflate(R.layout.list_item_layout, parent, false);
            if (root != null) {
                TextView title = (TextView) root.findViewById(R.id.title);
                TextView desc = (TextView) root.findViewById(R.id.description);
                ImageView image = (ImageView) root.findViewById(R.id.image);
                Cake cake = cakes.get(position);
                title.setText(cake.getTitle());
                desc.setText(cake.getDescription());
                if (cake.getImageData() != null) {
                    mImageLoader.load(cake.getImageData(), image);
                }
            }

            return root;
        }

        void setItems(List<Cake> items) {
            cakes = items;
        }
    }
}