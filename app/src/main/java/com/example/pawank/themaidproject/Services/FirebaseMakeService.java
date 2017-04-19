package com.example.pawank.themaidproject.Services;

import android.util.Log;

import com.example.pawank.themaidproject.Managers.ComManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by pawan.k on 16-03-2017.
 */

public class FirebaseMakeService extends FirebaseInstanceIdService {
    private String TAG="Firebase Make";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        new ComManager(getApplicationContext()).sendFirebaseDeviceKeyToServer(getApplicationContext(),refreshedToken);
    }
}
