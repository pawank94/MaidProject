package com.example.pawank.themaidproject.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.Managers.SQLManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

public class FirebaseMainService extends FirebaseMessagingService {
    private String TAG="Firebase Service";
    private static NotificationEngine nEngine = null;
    String targetFragment;
    JSONArray activities;
    public static ArrayList<ModuleNotification> all_activities=null;
    private static ArrayList<ModuleNotification> stored_activities=null;

    public FirebaseMainService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());
            Map<String,String> receivedMessage = remoteMessage.getData();
            targetFragment = receivedMessage.get("MODULE");
            activities = new JSONArray(receivedMessage.get("ACTIVITIES"));
            getNotificationEngine().showNotification(targetFragment.toUpperCase(),activities);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*
        *in case we need to handle Application Open case
         */
//        if(!MainActivity.isOpened) {
//        } else{
//            Log.d(TAG,"main activity is open");
//        }
    }

    private NotificationEngine getNotificationEngine(){
        if(nEngine == null) {
            MiscUtils.logD(TAG,"Notification Engine new instance created");
            nEngine = new NotificationEngine(getApplicationContext());
        }
        return nEngine;
    }

    /*all_activities array in service*/
    public static void initAllActivities(Context applicationContext) {
        if(all_activities==null)
            all_activities = new ArrayList<>();
        if(stored_activities==null) {
            stored_activities = new ArrayList<>();
            stored_activities = new SQLManager(applicationContext).getActivities();
        }
        stored_activities.addAll(0,all_activities);
        all_activities.clear();
        all_activities.addAll(stored_activities);
        stored_activities.clear();
    }
    public static ArrayList<ModuleNotification> getAllActivities() {
        if(all_activities==null)
            all_activities = new ArrayList<>();
        return all_activities;
    }
}
