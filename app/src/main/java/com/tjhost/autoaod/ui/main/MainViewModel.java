package com.tjhost.autoaod.ui.main;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.tjhost.autoaod.data.DataFactory;
import com.tjhost.autoaod.data.SettingRepo;
import com.tjhost.autoaod.services.NotificationMonitorService;

public class MainViewModel extends AndroidViewModel {
    private SettingRepo mRepo;

    private MediatorLiveData<Boolean> enableServiceState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> enableAirModeState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> serviceRunningState = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> enableScheduleModeState = new MediatorLiveData<>();
    private MediatorLiveData<Integer> scheduleStartTime = new MediatorLiveData<>();
    private MediatorLiveData<Integer> scheduleEndTime = new MediatorLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepo = DataFactory.getSettingRepo(application);
        init();
    }

    @Override
    protected void onCleared() {
        mRepo.release();
        super.onCleared();
    }

    private void init() {
        enableServiceState.addSource(mRepo.getEnableServiceState(),
                enableServiceState::setValue);
        enableAirModeState.addSource(mRepo.getEnableAirmodeState(),
                enableAirModeState::setValue);
        serviceRunningState.addSource(mRepo.getServiceRunningState(),
                serviceRunningState::setValue);
        enableScheduleModeState.addSource(mRepo.getEnableScheduleModeState(),
                enableScheduleModeState::setValue);
        scheduleStartTime.addSource(mRepo.getScheduleStartTime(),
                scheduleStartTime::setValue);
        scheduleEndTime.addSource(mRepo.getScheduleEndTime(),
                scheduleEndTime::setValue);
    }

    public void setEnableServiceState(boolean enable) {
        mRepo.saveEnableServiceState(enable);
    }

    public LiveData<Boolean> getEnableServiceStateLd() {
        return enableServiceState;
    }

    public void setEnableAirmodeState(boolean enable) {
        mRepo.saveEnableAirmodeState(enable);
    }

    public LiveData<Boolean> getEnableAirmodeStateLd() {
        return enableAirModeState;
    }

    public void setServiceRunningState(boolean enable) {
        mRepo.setServiceRunningState(enable);
    }

    public LiveData<Boolean> getServiceRunningStateLd() {
        return serviceRunningState;
    }

    public LiveData<Boolean> loadServiceRunningState() {
        return mRepo.loadServiceRunningState();
    }

    public void setEnableScheduleModeState(boolean enable) {
        mRepo.saveEnableScheduleModeState(enable);
    }

    public LiveData<Boolean> getEnableScheduleModeStateLd() {
        return enableScheduleModeState;
    }

    public void setScheduleStartTime(int time) {
        mRepo.saveScheduleStartTime(time);
    }

    public LiveData<Integer> getScheduleStartTimeLd() {
        return scheduleStartTime;
    }

    public void setScheduleEndTime(int time) {
        mRepo.saveScheduleEndTime(time);
    }

    public LiveData<Integer> getScheduleEndTimeLd() {
        return scheduleEndTime;
    }

    public void refreshServiceAirmodeConfig() {
        if (NotificationMonitorService.INSTANCE == null)
            return;
        NotificationMonitorService.INSTANCE.refreshAirmodeConfig();
    }

    public void refreshScheduleModeConfig() {
        if (NotificationMonitorService.INSTANCE == null)
            return;
        NotificationMonitorService.INSTANCE.refreshScheduleModeConfig();
    }

    public void refreshScheduleTimeConfig() {
        if (NotificationMonitorService.INSTANCE == null)
            return;
        NotificationMonitorService.INSTANCE.refreshScheduleTimeConfig();
    }

    public static void startAODService(Context context) {
        if (NotificationMonitorService.INSTANCE != null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationMonitorService.requestRebind(new ComponentName(context,
                    NotificationMonitorService.class));
        } else {
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(context,
                    NotificationMonitorService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static void stopAODService(Context context) {
        if (NotificationMonitorService.INSTANCE == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationMonitorService.INSTANCE.requestUnbind();
        } else {
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(context,
                    NotificationMonitorService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }


}
