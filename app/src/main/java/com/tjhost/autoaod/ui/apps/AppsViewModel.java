package com.tjhost.autoaod.ui.apps;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.data.AppsRepo;
import com.tjhost.autoaod.data.DataFactory;
import com.tjhost.autoaod.data.model.UserApps;
import com.tjhost.autoaod.services.NotificationMonitorService;

import java.util.List;

public class AppsViewModel extends AndroidViewModel {
    private AppsRepo mRepo;
    private MediatorLiveData<List<UserApps>> appsLd = new MediatorLiveData<>();

    public interface AppsFilter {
        /**
         * data filter
         * @param apps
         * @return true if this apps is what you need
         */
        boolean filter(UserApps apps);
    }

    public AppsViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        mRepo = DataFactory.getAppsRepo(getApplication());
        appsLd.setValue(null);
        appsLd.addSource(mRepo.loadAllApps(), appsLd::setValue);
    }

    public MediatorLiveData<List<UserApps>> getAppsLd() {
        return appsLd;
    }

    public void loadApps() {
        mRepo.loadAllApps();
    }

    public void addApps(UserApps apps) {
        mRepo.addApps(apps);
    }

    public void removeApps(UserApps apps) {
        mRepo.removeApps(apps);
    }

    public void refreshServiceAppsConfig() {
        if (NotificationMonitorService.INSTANCE == null)
            return;
        NotificationMonitorService.INSTANCE.refreshAppsConfig();
    }
}
