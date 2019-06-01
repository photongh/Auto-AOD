package com.tjhost.autoaod.ui.main;

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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import java.util.Locale;

public class TimePickerPreference extends DialogPreference {
    private int timeInMinute;

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePickerPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
    }

    public void setTime(int time) {
        final boolean wasBlocking = shouldDisableDependents();

        timeInMinute = time;

        persistInt(time);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }

        showSummary();
        notifyChanged();
    }

    public int getTime() {
        return timeInMinute;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null)
            defaultValue = 0;
        setTime(getPersistedInt((Integer) defaultValue));
    }

    @Override
    public boolean shouldDisableDependents() {
        return super.shouldDisableDependents();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final TimePickerPreference.SavedState myState = new TimePickerPreference.SavedState(superState);
        myState.mTime = getTime();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(TimePickerPreference.SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        TimePickerPreference.SavedState myState = (TimePickerPreference.SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setTime(myState.mTime);
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<TimePickerPreference.SavedState> CREATOR =
                new Parcelable.Creator<TimePickerPreference.SavedState>() {
                    @Override
                    public TimePickerPreference.SavedState createFromParcel(Parcel in) {
                        return new TimePickerPreference.SavedState(in);
                    }

                    @Override
                    public TimePickerPreference.SavedState[] newArray(int size) {
                        return new TimePickerPreference.SavedState[size];
                    }
                };

        int mTime;

        SavedState(Parcel source) {
            super(source);
            mTime = source.readInt();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mTime);
        }
    }

    private void showSummary() {
        int time = timeInMinute;
        setSummary(String.format(Locale.getDefault(), "%02d:%02d", time/60, time%60));
    }
}
