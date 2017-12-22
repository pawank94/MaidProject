package com.example.pawank.themaidproject.utils;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.Managers.PrefManager;
import com.example.pawank.themaidproject.Managers.SQLManager;
import com.example.pawank.themaidproject.SplashActivity;

import java.util.StringTokenizer;

/**
 * Created by pawan.k on 20-03-2017.
 */

public class ConsoleCommands {
    private static Context ctx;

    private static String TAG = "Console";

    private ConsoleCommands(Context ctx){
    }

    public static void initConsoleCommand(Context context){
        ctx = context;
    }

    public static void execCommand(Activity activity, String command)
    {
        StringTokenizer st = new StringTokenizer(command, " ");
        String com = st.nextToken();
        SQLManager sqlManager = new SQLManager(activity);
        switch (com)
        {
            case "exit":
                String command_to_exec = "<b>>_ "+command+"</b>";
                MiscUtils.logD("Command:",command_to_exec);
                activity.finishAffinity();
                activity.finish();
                break;
            case "changeIp":
                if (!st.hasMoreTokens()) {
                    MiscUtils.logE(TAG, "Format is changeIp http/s://ip");
                    return;
                }
                String newAddress = st.nextToken();
                String properConnectionAddress = ComManager.getProperUrl(newAddress);
                sqlManager.putUrlToConnectTableAddress(properConnectionAddress);
                PrefManager.setSharedVal("url_to_connect", properConnectionAddress);
                MiscUtils.logE(TAG,"connection address changed to: " + properConnectionAddress);
                Toast.makeText(activity, "address changed", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
