package com.tjhost.autoaod.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.services.KeyMonitorService;
import com.tjhost.autoaod.services.NotificationMonitorService;
import com.tjhost.autoaod.utils.SettingUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotifyEngine {
    public static boolean DEBUG = Constants.DEBUG;
    private static final String LOG_TAG = "AutoAODService";

    private NotificationMonitorService service;
    private List<String> mPackages;
    private boolean isAirmodeNeed; // ui setting switch value
    private boolean airmodeStatus; // phone airplane mode status, true is on
    private boolean scheduleEnabled; // schedule mode
    private int scheduleStartTime; // schedule start time
    private int scheduleEndTime; // schedule end time
    private int screenTimeoutTime; // screen timeout time in system settings, ms
    private long lastNotificationTime; // the last notification from applications you selected while screen stays on
    private boolean isLightScreenNeed; // light screen on
    private StatusBarNotification lastPendingNotification; // last notification in schedule time
    private boolean isTimeTickerReceiverRegisted;

    private static volatile NotifyEngine instance;

    private NotifyEngine(NotificationMonitorService service) {
        this.service = service;
        init();
    }

    public static NotifyEngine getInstance(NotificationMonitorService service) {
        if (instance == null) {
            synchronized (NotifyEngine.class) {
                if (instance == null) {
                    instance = new NotifyEngine(service);
                }
            }
        }
        return instance;
    }

    public void release() {
        instance = null;
        if (isTimeTickerReceiverRegisted) {
            service.unregisterReceiver(timeTickerReceiver);
        }
    }

    private void init() {
        screenTimeoutTime = SettingUtil.getScreenTimeoutTime(service);
        if (DEBUG) Log.d(LOG_TAG, "init screenTimeoutTime = " + screenTimeoutTime);
        if (mPackages == null) mPackages = new ArrayList<>();
    }


    /**
     * process any logic here
     * @param sbn
     * @return false if this notification will be ignored, otherwise showing the AOD
     */
    public boolean onNotificationPosted(StatusBarNotification sbn) {
        //  only for edge lighting
        if (isLightScreenNeed && !SettingUtil.isScreenOn(service)) {
            if (!sbn.isOngoing() && containsPackage(sbn.getPackageName())) {
                if (DEBUG) Log.d(LOG_TAG, "package matches, light screen on now");
                SettingUtil.lightScreenOn(service);
            }
        } else {
            if (DEBUG) Log.d(LOG_TAG, "condition not matches, light screen on fail");
        }

        if (isAirmodeNeed && airmodeStatus) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, isAirmodeNeed && airmodeStatus");
            return false;
        }
        if (SettingUtil.isScreenOnAndUnlocked(service)) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, isScreenOnAndUnlocked");
            // more intelligent while screen stays on
            if (containsPackage(sbn.getPackageName()) && !shouldDisableBySchedule()) {
                if (DEBUG) Log.d(LOG_TAG, "in mPackages, save last notification time");
                lastNotificationTime = System.currentTimeMillis();
            }
            return false;
        }
        if (SettingUtil.getAodTapMode(service) == SettingUtil.MODE_AOD_ALWAYS_ON) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, getAodTapMode = MODE_AOD_ALWAYS_ON");
            return false;
        }
        if (sbn.isOngoing()) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, notification isOngoing");
            return false;
        }
        if (shouldDisableBySchedule()) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, shouldDisableBySchedule");
            enterScheduleTime(sbn);
            return false;
        }
        if (!containsPackage(sbn.getPackageName())) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, not in mPackages");
            return false;
        }

        lastNotificationTime = 0L;
        return true;
    }

    /**
     * process any logic here
     * @param sbn
     * @return false if this notification will be ignored, otherwise showing the AOD
     */
    public boolean onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn.isOngoing()) {
            if (DEBUG) Log.d(LOG_TAG, "ignore this notification, notification isOngoing");
            return false;
        }
        if (SettingUtil.isScreenOn(service)) {
            if (DEBUG) Log.d(LOG_TAG, "you have removed a notification when screen on, so " +
                    "maybe all other notifications will be ignore, restore AOD");
            lastNotificationTime = 0L;
            exitScheduleTime();
            return true;
        }
        if (service.getActiveNotifications().length == 0) {
            lastNotificationTime = 0L;
            //lastPendingNotification = null;
            exitScheduleTime();
            if (DEBUG) Log.d(LOG_TAG, "no notifications, restore AOD");
            return true;
        }

        return true;
    }

    public void onScreenOn() {

    }

    public void onScreenOff() {
        if ((System.currentTimeMillis() - lastNotificationTime) < screenTimeoutTime) {
            if (DEBUG) Log.d(LOG_TAG, "this notification may be missed by you, enable AOD");
            if (!shoouldDisableByAccessibilityService()) {
                service.enableAodAlwaysOn();
            }
        }
    }

    public void onScreenUnlocked() {
    }


    void enterScheduleTime(StatusBarNotification sbn) {
        if (DEBUG) Log.d(LOG_TAG, "enterScheduleTime");
        if (sbn == null || !containsPackage(sbn.getPackageName()))
            return;
        lastPendingNotification = sbn;
        if (!isTimeTickerReceiverRegisted) {
            isTimeTickerReceiverRegisted = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            service.registerReceiver(timeTickerReceiver, filter);
        }
    }

    void exitScheduleTime() {
        if (DEBUG) Log.d(LOG_TAG, "exitScheduleTime");
        lastPendingNotification = null;
        if (isTimeTickerReceiverRegisted) {
            service.unregisterReceiver(timeTickerReceiver);
            isTimeTickerReceiverRegisted = false;
        }
    }

    public List<String> getPackages() {
        return mPackages;
    }

    public void setPackages(List<String> packages) {
        if (packages == null) return;
        this.mPackages = packages;
    }

    private boolean containsPackage(String pkg) {
        if (mPackages == null)
            return false;
        return mPackages.contains(pkg);
    }

    private boolean shoouldDisableByAccessibilityService() {
        synchronized (KeyMonitorService.class) {
            if (KeyMonitorService.INSTANCE == null)
                return false;
            if ((System.currentTimeMillis() - KeyMonitorService.INSTANCE.lastManualLockphoneTime) < 5000) {
                if (DEBUG) Log.d(LOG_TAG, "power key pressed by user, disable AOD");
                return true;
            }
            if ((System.currentTimeMillis() - KeyMonitorService.INSTANCE.lastInteractionTime) < screenTimeoutTime) {
                if (DEBUG) Log.d(LOG_TAG, "user had interacted with phone, the notification " +
                        "may be not important, disable AOD");
                return true;
            }
            return false;
        }
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

    public boolean isAirmodeNeed() {
        return isAirmodeNeed;
    }

    public void setAirmodeNeed(boolean airmodeNeed) {
        isAirmodeNeed = airmodeNeed;
    }

    public boolean isAirmodeStatus() {
        return airmodeStatus;
    }

    public void setAirmodeStatus(boolean airmodeStatus) {
        this.airmodeStatus = airmodeStatus;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public int getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(int scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public int getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(int scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    public boolean isLightScreenNeed() {
        return isLightScreenNeed;
    }

    public void setLightScreenNeed(boolean lightScreenNeed) {
        isLightScreenNeed = lightScreenNeed;
    }

    private BroadcastReceiver timeTickerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                if (DEBUG) Log.d(LOG_TAG, "onReceive ACTION_TIME_TICK");
                boolean r = shouldDisableBySchedule();
                if (service.getActiveNotifications().length == 0) {
                    exitScheduleTime();
                }
                if (!r) {
                    if (DEBUG) Log.d(LOG_TAG, "going to re-post notification after " +
                            "schedule time");
                    if (lastPendingNotification != null) {
                        service.onNotificationPosted(lastPendingNotification);
                        if (DEBUG) Log.d(LOG_TAG, "re-posted");
                    }
                    exitScheduleTime();
                }
            }
        }
    };
}
