package com.tjhost.autoaod.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.tjhost.autoaod.Constants;

import java.util.Arrays;
import java.util.List;

public class KeyMonitorService extends AccessibilityService {
    public static boolean DEBUG = Constants.DEBUG;
    private static final String LOG_TAG = "KeyMonitorService";

    private static final String DESCRIPTION_POWER_GOODLOCK = "Custom navigation button";
    private static final String DESCRIPTION_POWER_GOODLOCK_ZHCN = "自定义导航按钮";
    private static final String[] DESCRIPTION_POWER_ARRAY = new String[]{DESCRIPTION_POWER_GOODLOCK,
            DESCRIPTION_POWER_GOODLOCK_ZHCN};
    private static final List<CharSequence> DESCRIPTION_POWER_LIST = Arrays.asList(DESCRIPTION_POWER_ARRAY);

    public volatile static KeyMonitorService INSTANCE;
    public long lastInteractionTime; // include click, scroll, windows change...
    public long lastManualLockphoneTime; // locked by user, not include system timeout

    public static synchronized void exit() {
        if (INSTANCE != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                INSTANCE.disableSelf();
            } else {
                INSTANCE.stopSelf();
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        if (DEBUG) Log.d(LOG_TAG, "onServiceConnected");
        super.onServiceConnected();
        INSTANCE = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (DEBUG) Log.d(LOG_TAG, "onAccessibilityEvent");
        if (DEBUG) Log.d(LOG_TAG, "event type = " + event.toString());
        processEvent(event);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (DEBUG) Log.d(LOG_TAG, "onKeyEvent");
        if (DEBUG) Log.d(LOG_TAG, "key code = " + event.getKeyCode());
        calLastInteractionTime(null);
        return super.onKeyEvent(event);
    }

    @Override
    protected boolean onGesture(int gestureId) {
        if (DEBUG) Log.d(LOG_TAG, "onGesture");
        calLastInteractionTime(null);
        return super.onGesture(gestureId);
    }

    @Override
    public void onInterrupt() {
        if (DEBUG) Log.d(LOG_TAG, "onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG) Log.d(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(LOG_TAG, "onDestroy");
        synchronized (KeyMonitorService.class) {
            INSTANCE = null;
        }
        super.onDestroy();
    }

    public void calLastInteractionTime(AccessibilityEvent event) {
//        if (event == null) {
//            lastInteractionTime = System.currentTimeMillis();
//            return;
//        }
//        final int type = event.getEventType();
//        if (type == AccessibilityEvent.TYPE_VIEW_CLICKED
//                || type == AccessibilityEvent.TYPE_VIEW_SCROLLED
//                || type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            // user is interacting with phone
//            lastInteractionTime = System.currentTimeMillis();
//        }

        // do nothing
        lastInteractionTime = System.currentTimeMillis();
    }

    private void calLastManualLockphoneTime(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
                && DESCRIPTION_POWER_LIST.contains(event.getContentDescription())) {
            // click power button
            lastManualLockphoneTime = System.currentTimeMillis();
        }
    }

    private void processEvent(AccessibilityEvent event) {
        calLastInteractionTime(event);
        calLastManualLockphoneTime(event);
    }
}
