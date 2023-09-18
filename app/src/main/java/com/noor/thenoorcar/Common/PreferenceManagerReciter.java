package com.noor.thenoorcar.Common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class PreferenceManagerReciter {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TheNoorReciter";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_THEME_ID = "key";
    public static final String KEY_THEME_NAME = "name";
    public static final String KEY_THEME_IMAGE = "image";

    public PreferenceManagerReciter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String token,String name,String image){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_THEME_ID, token);
        editor.putString(KEY_THEME_NAME, name);
        editor.putString(KEY_THEME_IMAGE, image);
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
        user.put(KEY_THEME_ID, pref.getString(KEY_THEME_ID, null));
        user.put(KEY_THEME_NAME, pref.getString(KEY_THEME_NAME, null));
        user.put(KEY_THEME_IMAGE, pref.getString(KEY_THEME_IMAGE, null));
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