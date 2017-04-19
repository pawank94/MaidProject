package com.example.pawank.themaidproject.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Map;

public class FirebaseMainService extends FirebaseMessagingService {
    private String TAG="Firebase Service";
    private static NotificationEngine nEngine = null;
    String targetFragment;
    JSONArray activities;

    public FirebaseMainService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            MiscUtils.logD(TAG, "From: " + remoteMessage.getFrom());
            MiscUtils.logD(TAG, "Notification Message Body: " + remoteMessage.getData().toString());
            Map<String,String> receivedMessage = remoteMessage.getData();
            targetFragment = receivedMessage.get("MODULE");
            // Log.d(TAG,receivedMessage.get("ACTIVITIES")+targetFragment+" ");
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

}
