package com.example.mafia;

import android.content.Context;
import android.content.SharedPreferences;

public class ClientData {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String NICKNAME_KEY = "nickname";
    private Context context;

    public ClientData(Context context) {
        this.context = context;
    }

    public String getClientNickname() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NICKNAME_KEY, "");
    }

    public Integer getSelectedAvatar() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("selectedAvatar", -1);
    }
}