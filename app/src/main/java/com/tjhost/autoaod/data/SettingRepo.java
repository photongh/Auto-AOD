package com.tjhost.autoaod.data;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.services.NotificationMonitorService;

public abstract class SettingRepo extends Repo{
    public static final boolean DEFAULT_SETTING_SERVICE_ENABLE = Constants.DEFAULT_SETTING_SERVICE_ENABLE;
    public static final boolean DEFAULT_SETTING_AIRMODE_ENABLE = Constants.DEFAULT_SETTING_AIRMODE_ENABLE;
    public static final boolean DEFAULT_SETTING_SCHEDULE_MODE_ENABLE = Constants.DEFAULT_SETTING_TIME_SCHEDULE_ENABLE;
    public static final int DEFAULT_SETTING_SCHEDULE_START_TIME = Constants.DEFAULT_SETTING_TIME_SCHEDULE_START;
    public static final int DEFAULT_SETTING_SCHEDULE_END_TIME = Constants.DEFAULT_SETTING_TIME_SCHEDULE_END;

    private MediatorLiveData<Boolean> enableServiceState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> enableAirModeState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> serviceRunningState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> ensbleScheduleModeState = new MediatorLiveData<>();
    private MediatorLiveData<Integer> scheduleStartTime = new MediatorLiveData<>();
    private MediatorLiveData<Integer> scheduleEndTime = new MediatorLiveData<>();

    public SettingRepo(Context applicationContext) {
        super(applicationContext);
    }

    public abstract void saveEnableServiceState(boolean enable);
    public abstract void saveEnableAirmodeState(boolean enable);
    public abstract void saveEnableScheduleModeState(boolean enable);
    public abstract void setServiceRunningState(boolean enable);
    public abstract void saveScheduleStartTime(int time);
    public abstract void saveScheduleEndTime(int time);

    public abstract MediatorLiveData<Boolean> loadEnableServiceState();
    public abstract MediatorLiveData<Boolean> loadEnableAirmodeState();
    public abstract MediatorLiveData<Boolean> loadEnableScheduleModeState();
    public abstract MediatorLiveData<Integer> loadScheduleStartTime();
    public abstract MediatorLiveData<Integer> loadScheduleEndTime();

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
    public MediatorLiveData<Boolean> getEnableScheduleModeState() {
        return ensbleScheduleModeState;
    }
    public MediatorLiveData<Integer> getScheduleStartTime() {
        return scheduleStartTime;
    }
    public MediatorLiveData<Integer> getScheduleEndTime() {
        return scheduleEndTime;
    }

}
