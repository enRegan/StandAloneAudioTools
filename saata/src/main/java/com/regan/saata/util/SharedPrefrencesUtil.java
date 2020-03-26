package com.regan.saata.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefrencesUtil {
    private static final String TAG = "SharedPrefrencesUtil";
    /**
     * 系统配置的配置文件
     */
    public final static String PREFERENCE_FILE_STRING = "audio_tools";

    public final static String LOGIN_TOKEN = "loginToken";
    public final static String IS_LOGINED = "is_login";
    public final static String QQ = "qq";
    public final static String EMAIL = "email";
    public final static String WXCHAT = "wxchat";

    private static SharedPrefrencesUtil mPrefsUtils;
    private SharedPreferences preference;

    public static SharedPrefrencesUtil getInstance(Context context) {
        if (null == mPrefsUtils) {
            mPrefsUtils = new SharedPrefrencesUtil(context);
        }
        return mPrefsUtils;
    }

    private SharedPrefrencesUtil(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
    }

    public static void saveStringByKey(Context context, String key, String value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getStringByKey(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getString(key, "");
    }

    public static void saveIntByKey(Context context, String key, int value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getIntByKey(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getInt(key, -1);
    }

    public static int getIntByKey(Context context, String key, int defValue) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getInt(key, defValue);
    }

    public static void saveLongByKey(Context context, String key, long value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static long getLongByKey(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getLong(key, -1);
    }

    public static long getLongByKey(Context context, String key, long defValue) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getLong(key, defValue);
    }

    public static boolean getBooleanByKey(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getBoolean(key, false);
    }

    public static boolean getBooleanByKey(Context context, String key, boolean defValue) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        return preference.getBoolean(key, defValue);
    }

    public static void saveBooleanByKey(Context context, String key, boolean value) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE_FILE_STRING, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

}
