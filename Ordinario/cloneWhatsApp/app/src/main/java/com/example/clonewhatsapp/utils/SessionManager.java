package com.example.clonewhatsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "WhatsAppCloneSession";
    private static final String KEY_JWT = "jwt_token";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_JWT, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return pref.getString(KEY_JWT, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}