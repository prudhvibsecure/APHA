package com.bsecure.apha.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 2018-11-19.
 */

public class SharedValues {
    private static final String SHARED_PREFS = "acc_chat";

    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
