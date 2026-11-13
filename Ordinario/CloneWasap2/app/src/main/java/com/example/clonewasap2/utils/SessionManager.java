package com.example.clonewasap2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("CloneWasapSession", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveAuthToken(String token, String userId) {
        editor.putString("JWT_TOKEN", token);
        editor.putString("USER_ID", userId);
        editor.apply();
    }

    public String getAuthToken() {
        return prefs.getString("JWT_TOKEN", "");
    }

    public String getUserId() {
        return prefs.getString("USER_ID", "");
    }

    public void logout() {
        editor.clear().apply();
    }
}