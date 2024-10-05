package com.pasc.business.ota.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pasc.lib.base.AppProxy;

/**
 * @author yangzijian
 * @date 2018/11/27
 * @des
 * @modify
 **/
public class OtaUtil {

    private static SharedPreferences getShared() {
        return AppProxy.getInstance().getApplication().getSharedPreferences(updateShareName, Context.MODE_PRIVATE);
    }
    private final static String updateShareName = "update_preference";
    public static void setInt(String key, int value) {
        getShared().edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defaultValue) {
        return getShared().getInt(key, defaultValue);
    }
    public static void setBoolean(String key, boolean value) {
        getShared().edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean value) {
        return getShared().getBoolean(key, value);
    }

    public static void setString(String key, String value) {
        getShared().edit().putString (key, value).commit();
    }

    public static String getString(String key, String defaultValue) {
        return getShared().getString (key, defaultValue);
    }
}
