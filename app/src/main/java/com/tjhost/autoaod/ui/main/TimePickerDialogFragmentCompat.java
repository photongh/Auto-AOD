package com.tjhost.autoaod.ui.main;

/*
 * Copyright (C) 2019-2022 "TangJian"
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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.tjhost.autoaod.R;

public class TimePickerDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    public static final String FRAGMENT_TAG = "Time_picker_dialog";
    private static final String SAVE_STATE_TIME = "TimePickerDialogFragmentCompat.time";

    private TimePicker picker;
    private int timeInMinute;

    public static TimePickerDialogFragmentCompat newInstance(String key) {
        final TimePickerDialogFragmentCompat
                f = new TimePickerDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            timeInMinute = getTimePickerPreference().getTime();
        } else {
            timeInMinute = savedInstanceState.getInt(SAVE_STATE_TIME);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_TIME, calCurrentTime());
    }

    @Override
    protected View onCreateDialogView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.preference_time_pick, null);

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker = view.findViewById(R.id.time_picker);
        picker.setIs24HourView(true);

        picker.setCurrentHour(timeInMinute / 60);
        picker.setCurrentMinute(timeInMinute % 60);
    }

    private TimePickerPreference getTimePickerPreference() {
        return (TimePickerPreference) getPreference();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            final TimePickerPreference preference = getTimePickerPreference();
            timeInMinute = calCurrentTime();
            if (preference.callChangeListener(timeInMinute)) {
                preference.setTime(timeInMinute);
            }
        }
    }

    private int calCurrentTime() {
        if (picker == null) return 0;
        return picker.getCurrentHour() * 60 + picker.getCurrentMinute();
    }
}
