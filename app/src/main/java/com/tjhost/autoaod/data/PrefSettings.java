package com.tjhost.autoaod.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.data.model.UserApps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrefSettings {
    public static final String CONFIG_MAIN_NAME = "main_config";
    public static final String KEY_SERVICE_ENABLE = "enable_service";
    public static final String KEY_AIRMODE_ENABLE = "enable_airmode";
    public static final String KEY_TIME_SCHEDULE_ENABLE = "enable_schedule";
    public static final String KEY_TIME_SCHEDULE_START = "schedule_start";
    public static final String KEY_TIME_SCHEDULE_END = "schedule_end";
    public static final String KEY_LIGHT_SCREEN_ON = "light_screen_on";

    public static final String CONFIG_APPS_NAME = "apps_config";
    public static final String KEY_APPS_LIST = "app_list";


    private SharedPreferences sp;
    @Deprecated
    public PrefSettings(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        init();
    }

    public PrefSettings(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        init();
    }

    private void init() {
//        if (!sp.contains(KEY_SERVICE_ENABLE))
//            edit().putBoolean(KEY_SERVICE_ENABLE, SettingRepo.DEFAULT_SETTING_SERVICE_ENABLE).commit();
//        if (!sp.contains(KEY_AIRMODE_ENABLE))
//            edit().putBoolean(KEY_AIRMODE_ENABLE, SettingRepo.DEFAULT_SETTING_AIRMODE_ENABLE).commit();
    }

    public SharedPreferences.Editor edit() {
        return sp.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    public void putInt(String key, int value) {
        edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void putStringList(String key, List<String> lists) {
        edit().putStringSet(key, list2Set(lists)).apply();
    }

    public List<String> getStringList(String key) {
        return set2List(sp.getStringSet(key, null));
    }

    public void putAppsList(String key, List<UserApps> lists) {
        if (lists == null) {
            putStringList(key, null);
            return;
        }
        List<String> ss = new ArrayList<>();
        for (int i=0; i<lists.size(); i++) {
            ss.add(lists.get(i).pkg);
        }
        putStringList(key, ss);
    }

    public List<UserApps> getAppsList(String key) {
        List<String> ss = getStringList(key);
        if (ss != null) {
            List<UserApps> lists = new ArrayList<>();
            for (int i=0; i<ss.size(); i++) {
                UserApps apps = new UserApps();
                apps.checked = true;
                apps.pkg = ss.get(i);
                lists.add(apps);
            }
            return lists;
        }
        return null;
    }

    private static Set<String> list2Set(List<String> lists) {
        if (lists == null) return null;
        return new HashSet<>(lists);
    }

    private static List<String> set2List(Set<String> sets) {
        if (sets == null) return null;
        return new ArrayList<>(sets);
    }
}
