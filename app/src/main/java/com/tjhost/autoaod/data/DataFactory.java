package com.tjhost.autoaod.data;

import android.content.Context;

public class DataFactory {
    public static SettingRepo getSettingRepo(Context applicationContext) {
        return new PrefSettingRepo(applicationContext);
    }

    public static AppsRepo getAppsRepo(Context applicationContext) {
        return new PrefAppsRepo(applicationContext);
    }
}
