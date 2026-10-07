package com.example.notes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "NOTES_PREFERENCES";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;

    private static SharedPrefManager instance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null)
            instance = new SharedPrefManager(context);
        return instance;
    }

    // Used to save REMEMBERED_EMAIL and CURRENT_USER_EMAIL
    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    // Used to read REMEMBERED_EMAIL and CURRENT_USER_EMAIL
    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Used to save whether the Remember Me checkbox is selected
    public boolean writeBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    // Used to check whether Remember Me was previously selected
    public boolean readBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Used to remove REMEMBERED_EMAIL when Remember Me is unchecked
    // and CURRENT_USER_EMAIL when the user logs out
    public boolean remove(String key) {
        editor.remove(key);
        return editor.commit();
    }
}