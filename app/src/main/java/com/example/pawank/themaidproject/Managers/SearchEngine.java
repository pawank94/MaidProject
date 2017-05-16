package com.example.pawank.themaidproject.Managers;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.Services.FirebaseMainService;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.util.ArrayList;

/**
 * Created by pawan.k on 24-04-2017.
 */

public class SearchEngine {
    private Context ctx;
    private ArrayList<ModuleNotification> originalActivitites;
    private String TAG = "Search Engine";

    public SearchEngine(Context ctx){
        this.ctx=ctx;
        this.originalActivitites = FirebaseMainService.getAllActivities();
    }


    public ArrayList<ModuleNotification> getQueryResult(String s, Object selectedItem) {
        ArrayList<ModuleNotification> returnList = new ArrayList<>();
        for(ModuleNotification mn : FirebaseMainService.getAllActivities()){
            Log.d(TAG,selectedItem.toString().toUpperCase());
            if(selectedItem.toString().toUpperCase().equals("MODULE")){
                if(mn.getModule().toUpperCase().contains(s.toUpperCase()) && !s.equals("")){
                    returnList.add(mn);
                }
            }else if(selectedItem.toString().toUpperCase().equals("CONTENT")){
                Log.d(mn.getnContent(),s.toUpperCase());
                if(mn.getnContent().toUpperCase().contains(s.toUpperCase()) && !s.equals("")){
                    returnList.add(mn);
                }
            }else{
                if(mn.getDate().toUpperCase().contains(s.substring(0,s.indexOf(",")-1).toUpperCase()) && !s.equals("")){
                    returnList.add(mn);
                }
            }
        }
        return returnList;
    }
}
