package com.tjhost.autoaod.services;

/*
 * Copyright (C) 2019-2020 "TangJian"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.core.NotifyEngine;
import com.tjhost.autoaod.data.AppsRepo;
import com.tjhost.autoaod.data.DataFactory;
import com.tjhost.autoaod.data.SettingRepo;
import com.tjhost.autoaod.data.model.UserApps;
import com.tjhost.autoaod.ui.main.MainFragment;
import com.tjhost.autoaod.ui.main.MainViewModel;
import com.tjhost.autoaod.utils.SettingUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This service will be
 */
public class NotificationMonitorService extends NotificationListenerService {
    public static boolean DEBUG = Constants.DEBUG;
    private static final String LOG_TAG = "AutoAODService";
    public static NotificationMonitorService INSTANCE;
    private int originalAodMode, originalAodTapMode, originalAodStartTime, originalAodEndTime;
    private LiveData<Boolean> serviceEnableLd;
    private List<String> mPackages;
    private boolean isAirmodeNeed; // ui setting switch value
    private boolean airmodeStatus; // phone airplane mode status, true is on
    private boolean scheduleEnabled; // schedule mode
    private int scheduleStartTime; // schedule start time
    private int scheduleEndTime; // schedule end time
    private boolean isLightScreenNeed; // light screen on
    private NotifyEngine mEngine;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(LOG_TAG, "onCreate");
        super.onCreate();
        INSTANCE = this;
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(LOG_TAG, "onStartCommand, intent = " + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        release();
        INSTANCE = null;
        super.onDestroy();
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onListenerConnected() {
        if (DEBUG) Log.d(LOG_TAG, "onListenerConnected");
        monitorConfigServiceState();
    }

    @Override
    public void onListenerDisconnected() {
        if (DEBUG) Log.d(LOG_TAG, "onListenerDisconnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (DEBUG) Log.d(LOG_TAG, "receive a notification");
        if (DEBUG) Log.d(LOG_TAG, "notification package = " + sbn.getPackageName());
        if (DEBUG) Log.d(LOG_TAG, "notification content = " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        if (DEBUG) Log.d(LOG_TAG, "notification is ongoing = " + sbn.isOngoing());

        if (mEngine != null && !mEngine.onNotificationPosted(sbn))
            return;

        boolean r = enableAodAlwaysOn();
        if (DEBUG) Log.d(LOG_TAG, "change aod mode to always on success ? " + r);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (DEBUG) Log.d(LOG_TAG, "remove a notification");
        if (DEBUG) Log.d(LOG_TAG, "notification package = " + sbn.getPackageName());
        if (DEBUG) Log.d(LOG_TAG, "notification content = " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));

        if (mEngine != null && !mEngine.onNotificationRemoved(sbn))
            return;

        restoreAodMode();
    }

    void onScreenOn() {
        if (mEngine != null) mEngine.onScreenOn();
    }

    void onScreenOff() {
        if (mEngine != null) mEngine.onScreenOff();
    }

    void onUserPresent() {
        synchronized (KeyMonitorService.class) {
            if (KeyMonitorService.INSTANCE != null)
                KeyMonitorService.INSTANCE.calLastInteractionTime(null);
        }
        if (mEngine != null) mEngine.onScreenUnlocked();
        restoreAodMode();
    }

    public void refreshAirmodeConfig() {
        SettingRepo repo = DataFactory.getSettingRepo(this.getApplication());
        // lambda is invalid here because we need the inner class pointer "this"
        repo.getEnableAirmodeState().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (DEBUG) Log.d(LOG_TAG, "refreshAirmodeConfig");
                isAirmodeNeed = aBoolean;
                if (DEBUG) Log.d(LOG_TAG, "now isAirmodeNeed = " + isAirmodeNeed);
                if (mEngine != null) mEngine.setAirmodeNeed(isAirmodeNeed);
                repo.getEnableAirmodeState().removeObserver(this);
            }
        });
        repo.loadEnableAirmodeState();
    }

    public void refreshScheduleModeConfig() {
        SettingRepo repo = DataFactory.getSettingRepo(this.getApplication());
        // lambda is invalid here because we need the inner class pointer "this"
        repo.getEnableScheduleModeState().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (DEBUG) Log.d(LOG_TAG, "refreshScheduleModeConfig");
                scheduleEnabled = aBoolean;
                if (DEBUG) Log.d(LOG_TAG, "now scheduleEnabled = " + scheduleEnabled);
                if (mEngine != null) mEngine.setScheduleEnabled(scheduleEnabled);
                repo.getEnableScheduleModeState().removeObserver(this);
            }
        });
        repo.loadEnableScheduleModeState();
    }

    public void refreshScheduleTimeConfig() {
        SettingRepo repo = DataFactory.getSettingRepo(this.getApplication());
        // lambda is invalid here because we need the inner class pointer "this"
        repo.getScheduleStartTime().observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                if (DEBUG) Log.d(LOG_TAG, "refreshScheduleTimeConfig");
                scheduleStartTime = value;
                if (DEBUG) Log.d(LOG_TAG, "now scheduleStartTime = " + scheduleStartTime);
                if (mEngine != null) mEngine.setScheduleStartTime(scheduleStartTime);
                repo.getScheduleStartTime().removeObserver(this);
            }
        });
        repo.getScheduleEndTime().observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                if (DEBUG) Log.d(LOG_TAG, "refreshScheduleTimeConfig");
                scheduleEndTime = value;
                if (DEBUG) Log.d(LOG_TAG, "now scheduleEndTime = " + scheduleEndTime);
                if (mEngine != null) mEngine.setScheduleEndTime(scheduleEndTime);
                repo.getScheduleEndTime().removeObserver(this);
            }
        });
        repo.loadScheduleStartTime();
        repo.loadScheduleEndTime();
    }

    public void refreshAppsConfig() {
        AppsRepo repo1 = DataFactory.getAppsRepo(this.getApplication());
        // lambda is invalid here because we need the inner class pointer "this"
        repo1.loadApps(app -> app.checked, true).observeForever(new Observer<List<UserApps>>() {
            @Override
            public void onChanged(List<UserApps> userApps) {
                if (DEBUG) Log.d(LOG_TAG, "refreshAppsConfig");
                if (mPackages == null)
                    mPackages = new ArrayList<>();
                mPackages.clear();
                if (userApps != null) {
                    for (UserApps apps : userApps) {
                        mPackages.add(apps.pkg);
                    }
                }
                if (DEBUG) Log.d(LOG_TAG, "now mPackages: ");
                if (DEBUG) Log.d(LOG_TAG, Arrays.toString(mPackages.toArray())+"");
                if (mEngine != null) mEngine.setPackages(mPackages);
                repo1.getApplications().removeObserver(this);
            }
        });
    }

    public void refreshLightScreenConfig() {
        SettingRepo repo = DataFactory.getSettingRepo(this.getApplication());
        // lambda is invalid here because we need the inner class pointer "this"
        repo.getEnableLightScreenState().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (DEBUG) Log.d(LOG_TAG, "getEnableLightScreenState");
                isLightScreenNeed = aBoolean;
                if (DEBUG) Log.d(LOG_TAG, "now isLightScreenNeed = " + isLightScreenNeed);
                if (mEngine != null) mEngine.setLightScreenNeed(isLightScreenNeed);
                repo.getEnableLightScreenState().removeObserver(this);
            }
        });
        repo.loadEnableLightScreenState();
    }

    private void init() {
        if (MainFragment.mainViewModel != null)
            MainFragment.mainViewModel.setServiceRunningState(true);
        originalAodMode = SettingUtil.getAodMode(this);
        originalAodTapMode = SettingUtil.getAodTapMode(this);
        originalAodStartTime = SettingUtil.getAodStartTime(this);
        originalAodEndTime = SettingUtil.getAodEndTime(this);
        //enableForgroundService(this);
        if (DEBUG) Log.d(LOG_TAG, "init originalAodTapMode = " + originalAodTapMode);
        if (DEBUG) Log.d(LOG_TAG, "init originalAodMode = " + originalAodMode);
        if (DEBUG) Log.d(LOG_TAG, "init originalAodStartTime = " + originalAodStartTime);
        if (DEBUG) Log.d(LOG_TAG, "init originalAodEndTime = " + originalAodEndTime);
    }

    private void release() {
        restoreAodMode();
        if (MainFragment.mainViewModel != null)
            MainFragment.mainViewModel.setServiceRunningState(false);
        stopMonitorConfigServiceState();
        unregistReceiver();
        if (mEngine != null) mEngine.release();
    }

    public void restoreAodMode() {
        if (DEBUG) Log.d(LOG_TAG, "restoreAodMode originalAodTapMode = " + originalAodTapMode);
        if (DEBUG) Log.d(LOG_TAG, "restoreAodMode originalAodMode = " + originalAodMode);
        if (DEBUG) Log.d(LOG_TAG, "restoreAodMode originalAodStartTime = " + originalAodStartTime);
        if (DEBUG) Log.d(LOG_TAG, "restoreAodMode originalAodEndTime = " + originalAodEndTime);

        if (originalAodTapMode != SettingUtil.getAodTapMode(this)) {
            boolean r = SettingUtil.changeAodMode(this, originalAodTapMode,
                    originalAodMode, originalAodStartTime, originalAodEndTime);
            if (DEBUG) Log.d(LOG_TAG, "change aod mode to original success ? " + r);
        }
    }

    public boolean enableAodAlwaysOn() {
        return SettingUtil.changeAodMode(this, SettingUtil.MODE_AOD_ALWAYS_ON,
                0, 0, 0);
    }

    private void registReceiver() {
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, screenFilter);

        screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airmodeReceiver, screenFilter);
    }

    private void unregistReceiver() {
        try {
            unregisterReceiver(screenReceiver);
        } catch (Exception e){}
        try {
            unregisterReceiver(airmodeReceiver);
        } catch (Exception e){}
    }

    private BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                if (DEBUG) Log.d(LOG_TAG, "screen on");
                onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                if (DEBUG) Log.d(LOG_TAG, "screen off");
                onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                if (DEBUG) Log.d(LOG_TAG, "screen unlock");
                onUserPresent();
            }
        }
    };

    private BroadcastReceiver airmodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
                airmodeStatus = intent.getBooleanExtra("state", false);
                if (DEBUG) Log.d(LOG_TAG, "airmodeReceiver, airmodeStatus = " + airmodeStatus);
                if (mEngine != null) mEngine.setAirmodeStatus(airmodeStatus);
            }
        }
    };


    private Observer<Boolean> serviceEnableOb = aBoolean -> {
        if (DEBUG) Log.d(LOG_TAG, "serviceEnableOb onchange, aBoolean = " + aBoolean);
        stopMonitorConfigServiceState();
        if (aBoolean) {
            mEngine = NotifyEngine.getInstance(this);
            refreshAirmodeConfig();
            refreshAppsConfig();
            refreshScheduleModeConfig();
            refreshScheduleTimeConfig();
            refreshLightScreenConfig();
            registReceiver();
            return;
        }
        if (SettingUtil.isNotificationPermissionGranted(NotificationMonitorService.this)) {
            if (DEBUG) Log.d(LOG_TAG, "stopAODService");
            MainViewModel.stopAODService(NotificationMonitorService.this);
        } else {
            if (DEBUG) Log.d(LOG_TAG, "stopSelf");
            stopSelf();
        }
    };

    private void monitorConfigServiceState() {
        SettingRepo repo = DataFactory.getSettingRepo(this.getApplication());
        serviceEnableLd = repo.getEnableServiceState();
        // avoid auto boot after device reboot
        serviceEnableLd.observeForever(serviceEnableOb);
        repo.loadEnableServiceState();
    }

    private void stopMonitorConfigServiceState() {
        if (serviceEnableLd != null)
            serviceEnableLd.removeObserver(serviceEnableOb);
    }

    private boolean shouldDisableBySchedule() {
        if (!scheduleEnabled)
            return false;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        if (scheduleEndTime > scheduleStartTime) {
            if (minutes < scheduleStartTime || minutes > scheduleEndTime)
                return false;
        } else {
            if (minutes > scheduleEndTime && minutes < scheduleStartTime)
                return false;
        }
        return true;
    }

    private void enableForgroundService(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("aod_general", "无奈才显示", NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "aod_general");
        startForeground(13672, builder.build());
    }
}
