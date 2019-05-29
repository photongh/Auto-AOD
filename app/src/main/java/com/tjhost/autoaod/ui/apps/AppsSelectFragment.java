package com.tjhost.autoaod.ui.apps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tjhost.autoaod.R;
import com.tjhost.autoaod.ui.base.BaseSupportFragment;

public class AppsSelectFragment extends BaseSupportFragment {
    private RecyclerView mRecyclerView;
    private AppsAdapter mAdapter;
    private ProgressBar progressBar;
    private AppsViewModel appsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsViewModel = ViewModelProviders.of(this).get(AppsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_select, container, false);
        progressBar = view.findViewById(R.id.apps_loading_progress);
        mRecyclerView = view.findViewById(R.id.apps_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new AppsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setAppsCheckCallback((apps, checked) -> {
                if (checked) appsViewModel.addApps(apps);
                else appsViewModel.removeApps(apps);
                appsViewModel.refreshServiceAppsConfig();
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appsViewModel.getAppsLd().observe(this, userApps -> {
            if (userApps != null) {
                Log.d("AppsSelectFragment", "onChange, userApps.size() = " + userApps.size());
                mAdapter.setAppsList(userApps);
                progressBar.setVisibility(View.GONE);
            } else {
                Log.d("AppsSelectFragment", "onChange, userApps = null");
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
}
