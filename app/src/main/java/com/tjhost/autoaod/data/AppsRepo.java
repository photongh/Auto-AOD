package com.tjhost.autoaod.data;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.data.model.UserApps;
import com.tjhost.autoaod.ui.apps.AppsViewModel;

import java.util.List;

public abstract class AppsRepo extends Repo{
    private MediatorLiveData<List<UserApps>> applications = new MediatorLiveData<>();
    public AppsRepo(Context applicationContext) {
        super(applicationContext);
    }

    public abstract void addApps(List<UserApps> apps);
    public abstract void addApps(UserApps apps);
    public abstract void removeApps(List<UserApps> apps);
    public abstract void removeApps(UserApps apps);
    public abstract void saveApps(List<UserApps> apps);
    public abstract void clearApps();

    public abstract MediatorLiveData<List<UserApps>> loadAllApps();
    public abstract MediatorLiveData<List<UserApps>> loadApps(AppsViewModel.AppsFilter filter);

    public MediatorLiveData<List<UserApps>> getApplications() {
        return applications;
    }
}
