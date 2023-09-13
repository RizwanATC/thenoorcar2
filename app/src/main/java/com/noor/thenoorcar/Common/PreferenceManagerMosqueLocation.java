package com.noor.thenoorcar.Common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class PreferenceManagerMosqueLocation {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TheNoorMosqueLocation";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ADDRESS = "KEY_ADDRESS";
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    public static final String KEY_DETAILS = "KEY_DETAILS";

    public PreferenceManagerMosqueLocation(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String address,String lat, String log, String details){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_LATITUDE, lat);
        editor.putString(KEY_LONGITUDE, log);
        editor.putString(KEY_DETAILS, details);
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
        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        user.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, null));
        user.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, null));
        user.put(KEY_DETAILS, pref.getString(KEY_DETAILS, null));
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