package com.tjhost.autoaod.data;

import android.content.Context;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

public class Repo {
    protected Context mContext;
    public Repo(Context applicationContext) {
        setContext(applicationContext);
    }
    protected void setContext(Context applicationContext) {
        mContext = applicationContext;
    }

    protected Context getContext() {
        return mContext;
    }

    public void release() {}

    protected static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    protected static <E> void setMutableLiveDataValue(MutableLiveData<E> ld, E value) {
        if (isMainThread())
            ld.setValue(value);
        else
            ld.postValue(value);
    }
}
