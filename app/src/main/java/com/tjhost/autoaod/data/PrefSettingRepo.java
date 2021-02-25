package com.tjhost.autoaod.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MediatorLiveData;

public class PrefSettingRepo extends SettingRepo{
    private PrefSettings mSettings;

    private SharedPreferences.OnSharedPreferenceChangeListener spListener = (sharedPreferences, key) -> {
        switch (key) {
            case PrefSettings.KEY_SERVICE_ENABLE:
                setMutableLiveDataValue(getEnableServiceState(),
                        mSettings.getBoolean(PrefSettings.KEY_SERVICE_ENABLE,
                                DEFAULT_SETTING_SERVICE_ENABLE));
                break;
            case PrefSettings.KEY_AIRMODE_ENABLE:
                setMutableLiveDataValue(getEnableAirmodeState(),
                        mSettings.getBoolean(PrefSettings.KEY_AIRMODE_ENABLE,
                                DEFAULT_SETTING_AIRMODE_ENABLE));
                break;
            case PrefSettings.KEY_TIME_SCHEDULE_ENABLE:
                setMutableLiveDataValue(getEnableScheduleModeState(),
                        mSettings.getBoolean(PrefSettings.KEY_TIME_SCHEDULE_ENABLE,
                                DEFAULT_SETTING_SCHEDULE_MODE_ENABLE));
            case PrefSettings.KEY_TIME_SCHEDULE_START:
                setMutableLiveDataValue(getScheduleStartTime(),
                        mSettings.getInt(PrefSettings.KEY_TIME_SCHEDULE_START,
                                DEFAULT_SETTING_SCHEDULE_START_TIME));
                break;
            case PrefSettings.KEY_TIME_SCHEDULE_END:
                setMutableLiveDataValue(getScheduleEndTime(),
                        mSettings.getInt(PrefSettings.KEY_TIME_SCHEDULE_END,
                                DEFAULT_SETTING_SCHEDULE_END_TIME));
            case PrefSettings.KEY_LIGHT_SCREEN_ON:
                setMutableLiveDataValue(getEnableLightScreenState(),
                        mSettings.getBoolean(PrefSettings.KEY_LIGHT_SCREEN_ON,
                                DEFAULT_SETTING_LIGHT_SCREEN_ENABLE));
                break;
            default:break;
        }
    };

    public PrefSettingRepo(Context applicationContext) {
        super(applicationContext);
        mSettings = new PrefSettings(getContext());
        mSettings.getSharedPreferences().registerOnSharedPreferenceChangeListener(spListener);
    }

    @Override
    public void saveEnableServiceState(boolean enable) {
        mSettings.putBoolean(PrefSettings.KEY_SERVICE_ENABLE, enable);
    }

    @Override
    public void saveEnableAirmodeState(boolean enable) {
        mSettings.putBoolean(PrefSettings.KEY_AIRMODE_ENABLE, enable);
    }

    @Override
    public void saveEnableScheduleModeState(boolean enable) {
        mSettings.putBoolean(PrefSettings.KEY_TIME_SCHEDULE_ENABLE, enable);
    }

    @Override
    public void setServiceRunningState(boolean enable) {
        setMutableLiveDataValue(getServiceRunningState(), enable);
    }

    @Override
    public void saveScheduleStartTime(int time) {
        mSettings.putInt(PrefSettings.KEY_TIME_SCHEDULE_START, time);
    }

    @Override
    public void saveScheduleEndTime(int time) {
        mSettings.putInt(PrefSettings.KEY_TIME_SCHEDULE_END, time);
    }

    @Override
    public void saveEnableLightScreenState(boolean enable) {
        mSettings.putBoolean(PrefSettings.KEY_LIGHT_SCREEN_ON, enable);
    }

    @Override
    public void saveEnableEdgeLightingState(boolean enable) {
        mSettings.putBoolean(PrefSettings.KEY_EDGE_LIGHTING_ON, enable);
    }

    @Override
    public MediatorLiveData<Boolean> loadEnableServiceState() {
        setMutableLiveDataValue(getEnableServiceState(),
                mSettings.getBoolean(PrefSettings.KEY_SERVICE_ENABLE, DEFAULT_SETTING_SERVICE_ENABLE));
        return getEnableServiceState();
    }

    @Override
    public MediatorLiveData<Boolean> loadEnableAirmodeState() {
        setMutableLiveDataValue(getEnableAirmodeState(),
                mSettings.getBoolean(PrefSettings.KEY_AIRMODE_ENABLE, DEFAULT_SETTING_AIRMODE_ENABLE));
        return getEnableAirmodeState();
    }

    @Override
    public MediatorLiveData<Boolean> loadEnableScheduleModeState() {
        setMutableLiveDataValue(getEnableScheduleModeState(),
                mSettings.getBoolean(PrefSettings.KEY_TIME_SCHEDULE_ENABLE, DEFAULT_SETTING_SCHEDULE_MODE_ENABLE));
        return getEnableScheduleModeState();
    }

    @Override
    public MediatorLiveData<Integer> loadScheduleStartTime() {
        setMutableLiveDataValue(getScheduleStartTime(),
                mSettings.getInt(PrefSettings.KEY_TIME_SCHEDULE_START, DEFAULT_SETTING_SCHEDULE_START_TIME));
        return getScheduleStartTime();
    }

    @Override
    public MediatorLiveData<Integer> loadScheduleEndTime() {
        setMutableLiveDataValue(getScheduleEndTime(),
                mSettings.getInt(PrefSettings.KEY_TIME_SCHEDULE_END, DEFAULT_SETTING_SCHEDULE_END_TIME));
        return getScheduleEndTime();
    }

    @Override
    public MediatorLiveData<Boolean> loadEnableLightScreenState() {
        setMutableLiveDataValue(getEnableLightScreenState(),
                mSettings.getBoolean(PrefSettings.KEY_LIGHT_SCREEN_ON, DEFAULT_SETTING_LIGHT_SCREEN_ENABLE));
        return getEnableLightScreenState();
    }

    @Override
    public MediatorLiveData<Boolean> loadEnableEdgeLightingState() {
        setMutableLiveDataValue(getEnableEdgeLightingState(),
                mSettings.getBoolean(PrefSettings.KEY_EDGE_LIGHTING_ON, DEFAULT_SETTING_EDGE_LIGHTING_ENABLE));
        return getEnableLightScreenState();
    }

    @Override
    public void release() {
        mSettings.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(spListener);
        super.release();
    }
}
