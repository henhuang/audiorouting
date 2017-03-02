package com.ioi.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by henhuang on 10/21/16.
 */
public class BooleanPreference {

    SharedPreferences sharedPreferences;
    String key;
    boolean value;


    public BooleanPreference(@NonNull Context context, @NonNull String key, boolean defaultValue) {
        this.key = key;
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        value = sharedPreferences.getBoolean(key, defaultValue);
    }

    public BooleanPreference(@NonNull Context context, @NonNull String key) {
        this(context, key, false);
    }

    public void edit(boolean value) {
        this.value = value;
        sharedPreferences.edit().putBoolean(key, value).commit();
        Log.d("asdfgh====", "edit= " + sharedPreferences.getBoolean(key, false));
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    public boolean getValue() {
        return value;
    }
}
