package com.waracle.androidtest.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.waracle.androidtest.viewModels.MainActivityModel;
import com.waracle.androidtest.R;

public class LoadingFragment extends Fragment {
    private ProgressBar indeterminateBar;
    private ProgressBar determinateBar;

    public static LoadingFragment getInstance() {
        return new LoadingFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            MainActivityModel model = ViewModelProviders.of(getActivity()).get(MainActivityModel.class);
            model.getProgress().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer progress) {
                    if (progress != null && progress > 0) {
                        indeterminateBar.setVisibility(View.GONE);
                        determinateBar.setVisibility(View.VISIBLE);
                        determinateBar.setProgress(progress);
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        indeterminateBar = view.findViewById(R.id.indeterminateProgressBar);
        determinateBar = view.findViewById(R.id.determinateProgressBar);
        return view;
    }
}
