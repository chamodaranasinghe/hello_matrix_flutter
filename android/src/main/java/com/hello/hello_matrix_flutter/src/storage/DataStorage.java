package com.hello.hello_matrix_flutter.src.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

public class DataStorage {
    private static final String SP_NAME  = "hello";
    public static final String KEY_PROFILE_STORAGE  = "profile";
    SharedPreferences sharedPref;

    public DataStorage(){
       sharedPref = SessionHolder.appContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
    }

    public void storeStringData(String key,String data){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,data);
        editor.apply();
    }

    public String getStringData(String key){
        String value = sharedPref.getString(key, null);
        return value;
    }

    public void eraseAllData(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}

