package com.tjhost.autoaod.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static String[] f(Context context) throws Exception {
        try {
            InputStream is = context.getAssets().open("buy_coffee.png");
            int len = is.available();
            byte[] b = new byte[116+47];
            if (len > 200000) b[1999] = 0;
            long skip = is.skip(107318);
//            Log.d("util", "skip = " + skip);
            int read = is.read(b, 0, b.length);
//            Log.d("util", "read = " + read);
            String s = new String(b);
            is.close();
            String[] ss = s.split(";");
            String s4 = ss[4];
            return ss;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public static Bitmap f2(Context context) {
        try {
            InputStream is = context.getAssets().open("buy_coffee.png");
            int len = is.available();
            byte[] b = new byte[len];
            is.read(b);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
