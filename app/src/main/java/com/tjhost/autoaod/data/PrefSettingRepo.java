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
    public void setServiceRunningState(boolean enable) {
        setMutableLiveDataValue(getServiceRunningState(), enable);
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
    public void release() {
        mSettings.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(spListener);
        super.release();
    }
}
