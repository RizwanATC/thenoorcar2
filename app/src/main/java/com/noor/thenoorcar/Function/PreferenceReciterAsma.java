package com.noor.thenoorcar.Function;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class PreferenceReciterAsma {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TheNoorReciterAsma";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "KEY_ID";

    public PreferenceReciterAsma(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
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
        user.put(KEY_ID, pref.getString(KEY_ID, null));
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
