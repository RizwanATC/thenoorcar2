package com.noor.thenoorcar.Common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class PreferenceAlarmVolume {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TheNoorAlarmVolume";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_STATUS_ALARM = "KEY_STATUS_ALARM";

    public PreferenceAlarmVolume(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String status){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_STATUS_ALARM, status);
        editor.commit();
    }
    public boolean checkLogin(){
        return false;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_STATUS_ALARM, pref.getString(KEY_STATUS_ALARM, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}