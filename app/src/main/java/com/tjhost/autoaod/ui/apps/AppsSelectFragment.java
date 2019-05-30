package com.tjhost.autoaod.ui.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.R;
import com.tjhost.autoaod.data.model.UserApps;
import com.tjhost.autoaod.ui.base.BaseSupportFragment;

import java.util.List;

public class AppsSelectFragment extends BaseSupportFragment {
    private RecyclerView mRecyclerView;
    private AppsAdapter mAdapter;
    private ProgressBar progressBar;
    private AppsViewModel appsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        subscribeUi(appsViewModel.getAppsLd());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_select, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    subscribeUi(appsViewModel.filterCurrentApps(apps ->
                            apps.name.toLowerCase().contains(newText.toLowerCase())));
                } else {
                    subscribeUi(appsViewModel.filterCurrentApps(null));
                }
                return false;
            }
        });
    }

    private int selectAllButtonClickCount;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.select_all) {
            selectAllButtonClickCount++;
            if (selectAllButtonClickCount % 2 == 1)
                appsViewModel.selectAll(true);
            else
                appsViewModel.selectAll(false);
        }
        return super.onOptionsItemSelected(item);
    }

    private void subscribeUi(LiveData<List<UserApps>> ld) {
        ld.observe(this, userApps -> {
            if (userApps != null) {
                if (Constants.DEBUG) Log.d("AppsSelectFragment", "onChange, userApps.size() = " + userApps.size());
                mAdapter.setAppsList(userApps);
                progressBar.setVisibility(View.GONE);
            } else {
                if (Constants.DEBUG) Log.d("AppsSelectFragment", "onChange, userApps = null");
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
}
