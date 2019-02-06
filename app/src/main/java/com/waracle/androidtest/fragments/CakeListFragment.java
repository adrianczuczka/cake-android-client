package com.waracle.androidtest.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waracle.androidtest.pojos.Cake;
import com.waracle.androidtest.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import com.waracle.androidtest.R;

/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public class CakeListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Cake> cakes = new ArrayList<>();

    public static CakeListFragment getInstance(ArrayList<Cake> cakes){
        CakeListFragment fragment = new CakeListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("cakes", cakes);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            cakes = getArguments().getParcelableArrayList("cakes");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //Starting from API 26, there is no need to cast findViewById.
        mRecyclerView = rootView.findViewById(R.id.cake_list_recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CakeListAdapter mAdapter = new CakeListAdapter(cakes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private static class CakeListAdapter extends RecyclerView.Adapter<CakeListAdapter.CakeViewHolder> {

        private final List<Cake> cakes;
        private final ImageLoader mImageLoader;

        private static class CakeViewHolder extends RecyclerView.ViewHolder{
            private final TextView title;
            private final TextView description;
            private final ImageView image;

            private CakeViewHolder(View view){
                super(view);
                this.title = view.findViewById(R.id.cake_list_item_title);
                this.description = view.findViewById(R.id.cake_list_item_description);
                this.image = view.findViewById(R.id.cake_list_item_image);
            }
        }

        private CakeListAdapter() {
            this(new ArrayList<Cake>());
        }

        private CakeListAdapter(List<Cake> items) {
            cakes = items;
            mImageLoader = new ImageLoader();
        }

        @NonNull
        @Override
        public CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_layout, parent, false);
            return new CakeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CakeViewHolder cakeViewHolder, int i) {
            Cake cake = cakes.get(i);
            cakeViewHolder.title.setText(cake.getTitle());
            cakeViewHolder.description.setText(cake.getDescription());
            if (cake.getImageData() != null) {
                mImageLoader.load(cake.getImageData(), cakeViewHolder.image);
            }
        }

        @Override
        public int getItemCount() {
            return cakes.size();
        }
    }
}