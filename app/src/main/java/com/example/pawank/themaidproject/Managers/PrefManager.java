package com.example.pawank.themaidproject.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by pawan.k on 23-01-2017.
 */

public class PrefManager {
    private static final String globalPreferenceKey = "MyPrefs";
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;
    private PrefManager() {

    }
    public static void initSharedPref(Context c){
        sPref = c.getSharedPreferences(globalPreferenceKey,Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public static void setSharedVal(String Key,String Value) {
        if(sEditor!=null) {
            sEditor.putString(Key, Value);
            sEditor.commit();
        }
        else{
            try {
                throw new Exception("Preference Editor not initalized");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getSharedVal(String Key){
        if(sPref!=null)
            return sPref.getString(Key,null);
        else
            return null;
    }

}
