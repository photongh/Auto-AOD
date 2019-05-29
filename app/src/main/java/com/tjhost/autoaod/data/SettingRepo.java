package com.tjhost.autoaod.data;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.services.NotificationMonitorService;

public abstract class SettingRepo extends Repo{
    public static final boolean DEFAULT_SETTING_SERVICE_ENABLE = Constants.DEFAULT_SETTING_SERVICE_ENABLE;
    public static final boolean DEFAULT_SETTING_AIRMODE_ENABLE = Constants.DEFAULT_SETTING_AIRMODE_ENABLE;

    private MediatorLiveData<Boolean> enableServiceState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> enableAirModeState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> serviceRunningState = new MediatorLiveData<>();

    public SettingRepo(Context applicationContext) {
        super(applicationContext);
    }

    public abstract void saveEnableServiceState(boolean enable);
    public abstract void saveEnableAirmodeState(boolean enable);
    public abstract void setServiceRunningState(boolean enable);

    public abstract MediatorLiveData<Boolean> loadEnableServiceState();
    public abstract MediatorLiveData<Boolean> loadEnableAirmodeState();
    public MediatorLiveData<Boolean> loadServiceRunningState() {
        setServiceRunningState(NotificationMonitorService.INSTANCE != null);
        return getServiceRunningState();
    }

    public MediatorLiveData<Boolean> getEnableServiceState() {
        return enableServiceState;
    }
    public MediatorLiveData<Boolean> getEnableAirmodeState() {
        return enableAirModeState;
    }
    public MediatorLiveData<Boolean> getServiceRunningState() {
        return serviceRunningState;
    }

}
