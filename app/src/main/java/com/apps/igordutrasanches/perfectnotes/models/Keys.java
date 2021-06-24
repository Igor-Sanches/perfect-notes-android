package com.apps.igordutrasanches.perfectnotes.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by igord on 06/04/2019.
 */

public class Keys {
    public static String PREFERENCE_NAME = "Igor_Sanches";

    private static final String PREFERENCE_FILE_NAME = "Faby";
    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences getmSharedPreferencesEditor(Context context){
        if(mSharedPreferences ==null){
            mSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        }
        return mSharedPreferences;
    }

    public static void set(String key, long value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long get(String key, long Default, Context context){
        return getmSharedPreferencesEditor(context).getLong(key, Default);
    }

    public static void set(String key, float value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float get(String key, float Default, Context context){
        return getmSharedPreferencesEditor(context).getFloat(key, Default);
    }

    public static void set(String key, int value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int get(String key, int Default, Context context){
        return getmSharedPreferencesEditor(context).getInt(key, Default);
    }

    public static void set(String key, boolean value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean get(String key, boolean Default, Context context){
        return getmSharedPreferencesEditor(context).getBoolean(key, Default);
    }


    public static void remove(String key, Context context){
        getmSharedPreferencesEditor(context).edit().remove(key).commit();
    }

    public static void set(String key, String value, Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String get(String key, String Default, Context context){
        return getmSharedPreferencesEditor(context).getString(key, Default);
    }


}
