package com.example.pawank.themaidproject.utils;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;

import com.example.pawank.themaidproject.SplashActivity;

/**
 * Created by pawan.k on 20-03-2017.
 */

public class ConsoleCommands {
    private static Context ctx;

    private ConsoleCommands(Context ctx){
    }

    public static void initConsoleCommand(Context context){
        ctx = context;
    }

    public static void execCommand(Activity activity, String command)
    {
        switch (command)
        {
            case "exit":
                String command_to_exec = "<b>>_ "+command+"</b>";
                MiscUtils.logD("Command:",command_to_exec);
                activity.finishAffinity();
                activity.finish();
                break;
            default:
                break;
        }
    }
}
