package com.evan.quickscan.domain;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPrefManager {

    private final SharedPreferences sharedPreferences;

    private static final String KEY_ACCESS_KEY     = "KEY_ACCESS_KEY";
    private static final String KEY_SECRET_KEY     = "KEY_SECRET_KEY";
    private static final String DEFAULT_ACCESS_KEY = null;
    private static final String DEFAULT_SECRET_KEY = null;

    @Inject
    public SharedPrefManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getAccessKey() {
        return sharedPreferences.getString(KEY_ACCESS_KEY, DEFAULT_ACCESS_KEY);
    }

    public String getSecretKey() {
        return sharedPreferences.getString(KEY_SECRET_KEY, DEFAULT_SECRET_KEY);
    }

    public void setAccessKey(String accessKey) {
        sharedPreferences.edit().putString(KEY_ACCESS_KEY, accessKey).apply();
    }

    public void setSecretKey(String secretKey) {
        sharedPreferences.edit().putString(KEY_SECRET_KEY, secretKey).apply();
    }

    public void clear(){
        sharedPreferences.edit()
                .putString(KEY_ACCESS_KEY, DEFAULT_ACCESS_KEY)
                .putString(KEY_SECRET_KEY, DEFAULT_SECRET_KEY)
                .apply();
    }
}
