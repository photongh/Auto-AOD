package com.tjhost.autoaod.ui.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.R;
import com.tjhost.autoaod.ui.apps.AppsSelectActivity;
import com.tjhost.autoaod.utils.SettingUtil;

import static com.tjhost.autoaod.data.PrefSettings.KEY_AIRMODE_ENABLE;
import static com.tjhost.autoaod.data.PrefSettings.KEY_LIGHT_SCREEN_ON;
import static com.tjhost.autoaod.data.PrefSettings.KEY_SERVICE_ENABLE;
import static com.tjhost.autoaod.data.PrefSettings.KEY_TIME_SCHEDULE_ENABLE;
import static com.tjhost.autoaod.data.PrefSettings.KEY_TIME_SCHEDULE_END;
import static com.tjhost.autoaod.data.PrefSettings.KEY_TIME_SCHEDULE_START;

public class MainFragment extends PreferenceFragmentCompat {
        //implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static MainViewModel mainViewModel;
    private final boolean DEBUG = Constants.DEBUG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPreferenceHierarchy());
        getPreferenceScreen().findPreference("extra").setDependency(KEY_SERVICE_ENABLE);
        getPreferenceScreen().findPreference(KEY_TIME_SCHEDULE_START).setDependency(KEY_TIME_SCHEDULE_ENABLE);
        getPreferenceScreen().findPreference(KEY_TIME_SCHEDULE_END).setDependency(KEY_TIME_SCHEDULE_ENABLE);
        getPreferenceScreen().findPreference("tool").setDependency(KEY_SERVICE_ENABLE);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        checkPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUi();
    }

    @Override
    public void onDestroy() {
        mainViewModel = null;
        super.onDestroy();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.getEnableServiceStateLd().observe(this, aBoolean -> {
            if (DEBUG) Log.d("MainFragment", "getEnableServiceStateLd change, aBoolean = " + aBoolean);
            if (aBoolean) {
                MainViewModel.startAODService(requireActivity());
            } else {
                MainViewModel.stopAODService(requireActivity());
            }
        });
        mainViewModel.getEnableAirmodeStateLd().observe(this, aBoolean -> {
            if (DEBUG) Log.d("MainFragment", "getEnableAirmodeStateLd change, aBoolean = " + aBoolean);
            mainViewModel.refreshServiceAirmodeConfig();
        });
        mainViewModel.getEnableScheduleModeStateLd().observe(this, aBoolean -> {
            if (DEBUG) Log.d("MainFragment", "getEnableScheduleModeStateLd change, aBoolean = " + aBoolean);
            mainViewModel.refreshScheduleModeConfig();
        });
        mainViewModel.getScheduleStartTimeLd().observe(this, newValue -> {
            if (DEBUG) Log.d("MainFragment", "getScheduleStartTimeLd change, newValue = " + newValue);
            mainViewModel.refreshScheduleTimeConfig();
        });
        mainViewModel.getScheduleEndTimeLd().observe(this, newValue -> {
            if (DEBUG) Log.d("MainFragment", "getScheduleEndTimeLd change, newValue = " + newValue);
            mainViewModel.refreshScheduleTimeConfig();
        });
        mainViewModel.getEnableLightScreenStateLd().observe(this, aBoolean -> {
            if (DEBUG) Log.d("MainFragment", "getEnableLightScreenStateLd change, aBoolean = " + aBoolean);
            mainViewModel.refreshLightScreenConfig();
        });
        mainViewModel.getServiceRunningStateLd().observe(this, aBoolean -> {
            if (DEBUG) Log.d("MainFragment", "getServiceRunningStateLd change, aBoolean = " + aBoolean);
            if (aBoolean) {
                getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)
                        .setSummaryProvider(null);
                getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)
                        .setSummary(R.string.main_switch_summary_running);
            } else {
                if (! ((SwitchPreferenceCompat)getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)).isChecked()) {
                    getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)
                            .setSummaryProvider(null);
                    getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)
                            .setSummary(null);
                    return;
                }
                getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE)
                        .setSummaryProvider(preference -> {
                    String s = getString(R.string.main_switch_summary_error);
                    SpannableString spannableString = new SpannableString(s);
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                            0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    return spannableString;
                });
            }
        });
        mainViewModel.loadServiceRunningState();
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePickerPreference) {
            TimePickerDialogFragmentCompat f = TimePickerDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), TimePickerDialogFragmentCompat.FRAGMENT_TAG);
            return;
        }
        super.onDisplayPreferenceDialog(preference);
    }

    private void setUi() {
        SwitchPreferenceCompat pref = getPreferenceScreen().findPreference(KEY_SERVICE_ENABLE);
        assert pref != null;

        if (!SettingUtil.isWritable(requireActivity())) {
            pref.setEnabled(false);
            TipsDialog.show(requireActivity(), 0, R.string.tips_no_write_settings_permission);
            return;
        }
        if (SettingUtil.getAodTapMode(requireActivity()) == SettingUtil.MODE_AOD_ALWAYS_ON) {
            pref.setEnabled(false);
            TipsDialog.show(requireActivity(), 0, R.string.tips_already_aod);
            return;
        }
        if ((!SettingUtil.isNotificationPermissionGranted(requireActivity()))
                && !pref.isChecked()) {
            pref.setEnabled(false);
            return;
        }

        pref.setEnabled(true);
    }

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(requireActivity());

        SwitchPreferenceCompat mainSwitch = createSwitchPref(null, R.string.main_switch_title, 0, KEY_SERVICE_ENABLE, Constants.DEFAULT_SETTING_SERVICE_ENABLE, true);
        root.addPreference(mainSwitch);

        String title = requireActivity().getString(R.string.app_name);
        try {
            PackageInfo pkg = requireActivity().getPackageManager()
                    .getPackageInfo(requireActivity().getPackageName(), 0);
            title = title + " v" + pkg.versionName;
        } catch (Exception e) {
            // no action
        }
        final String url = "https://github.com/photongh/Auto-AOD";
        String details = "Copyright © 2019-2020 TangJian\n"
                + "Mail: 543708542@qq.com\n"
                + "Source: " + url;

        Preference copyright = createPreference(null, 0, 0, "copyright", true, v -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, ""));
            return true;
        });
        copyright.setTitle(title);
        copyright.setSummary(details);
        root.addPreference(copyright);

        PreferenceCategory category = createCategory(root, R.string.extra_settings_category_title, 0, "extra");

        SwitchPreferenceCompat airmode = createSwitchPref(category, R.string.extra_settings_airmode_title, R.string.extra_settings_airmode_summary,
                KEY_AIRMODE_ENABLE, Constants.DEFAULT_SETTING_AIRMODE_ENABLE, true);

        createSwitchPref(category, R.string.extra_settings_time_schedule_title, R.string.extra_settings_time_schedule_summary,
                KEY_TIME_SCHEDULE_ENABLE, Constants.DEFAULT_SETTING_TIME_SCHEDULE_ENABLE, true);
        createTimePickerPref(category, R.string.extra_settings_time_schedule_start_title,
                KEY_TIME_SCHEDULE_START, Constants.DEFAULT_SETTING_TIME_SCHEDULE_START, true);
        createTimePickerPref(category, R.string.extra_settings_time_schedule_end_title,
                KEY_TIME_SCHEDULE_END, Constants.DEFAULT_SETTING_TIME_SCHEDULE_END, true);

        Preference apps = createPreference(category, R.string.extra_settings_apps_title,R.string.extra_settings_apps_summary,"apps", true,
                v -> {
                    startActivity(new Intent(requireActivity(), AppsSelectActivity.class));
                    return true;
        });

        PreferenceCategory tools = createCategory(root, R.string.tool_settings_category_title, 0, "tool");
        createSwitchPref(tools, R.string.tool_settings_light_screen_title, R.string.tool_settings_light_screen_summary,
                KEY_LIGHT_SCREEN_ON, Constants.DEFAULT_SETTING_LIGHT_SCREEN_ON, true);

        return root;
    }

    private void checkPermission() {
        if (!SettingUtil.isNotificationPermissionGranted(requireActivity())) {
            new OpenNotificationPermissionDialog().show(getFragmentManager(),
                    OpenNotificationPermissionDialog.DIALOG_TAG);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private PreferenceCategory createCategory(PreferenceScreen screen, int titleRes, int summaryRes, String key) {
        PreferenceCategory category = new PreferenceCategory(requireActivity());
        category.setIconSpaceReserved(false);
        if (titleRes > 0) category.setTitle(titleRes);
        if (summaryRes > 0) category.setSummary(summaryRes);
        if (key != null) category.setKey(key);
        screen.addPreference(category);
        return category;
    }

    @SuppressWarnings("ConstantConditions")
    private Preference createPreference(PreferenceCategory category, int titleRes, int summaryRes, String key, boolean enabled, Preference.OnPreferenceClickListener clickListener) {
        Preference pref = new Preference(requireActivity());
        if (titleRes > 0) pref.setTitle(titleRes);
        if (summaryRes > 0) pref.setSummary(summaryRes);
        pref.setEnabled(enabled);
        if (key != null) pref.setKey(key);
        pref.setIconSpaceReserved(false);
        if (clickListener != null) pref.setOnPreferenceClickListener(clickListener);
        if (category != null) category.addPreference(pref);
        return pref;
    }

    @SuppressWarnings("ConstantConditions")
    private SwitchPreferenceCompat createSwitchPref(PreferenceCategory category, int titleRes, int summaryRes, String key, boolean defaultValue, boolean enabled) {
        SwitchPreferenceCompat pref = new SwitchPreferenceCompat(requireActivity());
        if (titleRes > 0) pref.setTitle(titleRes);
        if (summaryRes > 0) pref.setSummary(summaryRes);
        pref.setEnabled(enabled);
        pref.setKey(key);
        pref.setDefaultValue(defaultValue);
        pref.setIconSpaceReserved(false);
        if (category != null) category.addPreference(pref);
        return pref;
    }

    @SuppressWarnings("ConstantConditions")
    private TimePickerPreference createTimePickerPref(PreferenceCategory category, int titleRes, String key, int defaultValue, boolean enabled) {
        TimePickerPreference pref = new TimePickerPreference(requireActivity());
        if (titleRes > 0) pref.setTitle(titleRes);
        pref.setEnabled(enabled);
        pref.setKey(key);
        pref.setDefaultValue(defaultValue);
        pref.setIconSpaceReserved(false);
        if (category != null) category.addPreference(pref);
        return pref;
    }
}