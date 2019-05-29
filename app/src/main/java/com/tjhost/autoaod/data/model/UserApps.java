package com.tjhost.autoaod.data.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.Locale;

public class UserApps implements Comparable{
    public String name;
    String namePinyin; // for sort in Chinese
    public boolean checked;
    public Drawable icon;
    public String pkg;
    public boolean isSystem;

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean r = super.equals(obj);
        if (r)
            return r;
        if (!(obj instanceof UserApps))
            return false;
        if (pkg == null) {
            if (((UserApps)obj).pkg == null)
                return true;
            return false;
        }
        if (pkg.equals(((UserApps)obj).pkg))
            return true;
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof UserApps)) return -1;
        if ("zh".equals(Locale.getDefault().getLanguage())) {
            if (namePinyin == null)
                namePinyin = com.github.promeg.pinyinhelper.Pinyin.toPinyin(name, "");
            if (((UserApps) o).namePinyin == null)
                ((UserApps) o).namePinyin = com.github.promeg.pinyinhelper.Pinyin.toPinyin(((UserApps) o).name, "");
            return namePinyin.compareTo(((UserApps) o).namePinyin);
        }

        return name.compareTo(((UserApps) o).name);
    }
}
