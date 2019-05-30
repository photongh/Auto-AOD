package com.tjhost.autoaod.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.data.model.UserApps;
import com.tjhost.autoaod.ui.apps.AppsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PrefAppsRepo extends AppsRepo {
    private PrefSettings mSettings;

    public PrefAppsRepo(Context applicationContext) {
        super(applicationContext);
        mSettings = new PrefSettings(getContext());
    }

    @Override
    public void addApps(List<UserApps> apps) {
        if (apps == null || apps.size() == 0)
            return;
        List<UserApps> lists = mSettings.getAppsList(PrefSettings.KEY_APPS_LIST);
        if (lists == null) {
            saveApps(apps);
        } else {
            for (int i=0; i<apps.size(); i++) {
                if (!lists.contains(apps.get(i)))
                    lists.add(apps.get(i));
            }
            saveApps(lists);
        }
    }

    @Override
    public void addApps(UserApps apps) {
        if (apps == null) return;
        List<UserApps> lists = new ArrayList<>();
        lists.add(apps);
        addApps(lists);
    }

    @Override
    public void removeApps(List<UserApps> apps) {
        if (apps == null || apps.size() == 0)
            return;
        List<UserApps> lists = mSettings.getAppsList(PrefSettings.KEY_APPS_LIST);
        if (lists == null) {
            return;
        } else {
            for (int i=0; i<apps.size(); i++) {
                lists.remove(apps.get(i));
            }
            saveApps(lists);
        }
    }

    @Override
    public void removeApps(UserApps apps) {
        if (apps == null) return;
        List<UserApps> lists = new ArrayList<>();
        lists.add(apps);
        removeApps(lists);
    }

    @Override
    public void saveApps(List<UserApps> apps) {
        mSettings.putAppsList(PrefSettings.KEY_APPS_LIST, apps);

//        if (apps == null) return;
//        List<UserApps> allApps = getApplications().getValue();
//        if (allApps == null) return;
//        for (int i=0; i<apps.size(); i++) {
//            int index = allApps.indexOf(apps.get(i));
//            if (index != -1) {
//                if (allApps.get(index).checked) continue;
//                allApps.get(index).checked = true;
//                setMutableLiveDataValue(getApplications(), allApps);
//            }
//        }
    }

    @Override
    public void clearApps() {
        mSettings.putAppsList(PrefSettings.KEY_APPS_LIST, null);
        setMutableLiveDataValue(getApplications(), null);
    }

    @Override
    public MediatorLiveData<List<UserApps>> loadAllApps() {
        return loadApps(null);
    }

    @Override
    public MediatorLiveData<List<UserApps>> loadApps(AppsViewModel.AppsFilter filter) {
        AppExecutors.getInstance().normalThread().execute(() -> {
            PackageManager pm = getContext().getPackageManager();
            List<PackageInfo> lists = pm.getInstalledPackages(0);
            List<UserApps> apps = new ArrayList<>();
            for (PackageInfo info : lists) {
                UserApps app = new UserApps();
                app.pkg = info.packageName;
                app.checked = false;
                app.name = (String) info.applicationInfo.loadLabel(pm);
                app.icon = info.applicationInfo.loadIcon(pm);
                app.isSystem = (info.applicationInfo.flags &
                        ApplicationInfo.FLAG_SYSTEM) != 0;
                apps.add(app);
            }

            List<UserApps> checkedList = mSettings.getAppsList(PrefSettings.KEY_APPS_LIST);
            if (checkedList != null) {
                for (UserApps ua : checkedList) {
                    if (apps.contains(ua)) {
                        int index = apps.indexOf(ua);
                        if (index != -1) apps.get(index).checked = true;
                    }
                }
            }

            Collections.sort(apps, (o1, o2) -> {
                if (o1.checked != o2.checked) {
                    return o1.checked ? -1 : 1;
                }
                return o1.compareTo(o2);
            });

            if (filter != null) {
                Iterator<UserApps> iter = apps.iterator();
                while (iter.hasNext()) {
                    if (!filter.filter(iter.next()))
                        iter.remove();
                }
            }

            setMutableLiveDataValue(getApplications(), apps);
        });

        return getApplications();
    }

    @Override
    public void release() {
        super.release();
    }
}
