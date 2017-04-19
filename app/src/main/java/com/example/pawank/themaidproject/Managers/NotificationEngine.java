package com.example.pawank.themaidproject.Managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.Services.FirebaseMainService;
import com.example.pawank.themaidproject.utils.MiscUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

/**
 * Created by nexusstar on 8/4/17.
 */

public class NotificationEngine {
    private static Context ctx;
    public static final String FRAGMENT_ATTENDENCE = "ATTENDANCE";
    public static final String FRAGMENT_CONSOLE = "CONSOLE";
    public static final String FRAGMENT_FOOD_MENU = "FOOD_MENU";
    public static final String FRAGMENT_SHOPPING_LIST = "SHOPPING_LIST";
    private static final String N_ACTION_FILTER = "NOTIFICATION_DISMISS";
    public static final int MAX_ACTIVITY_COUNT = 10;
    private TextView nModule;
    private ListView nList;
    private ArrayList<ModuleNotification> array_attendence,array_food_menu,array_shopping_list;
    private String TAG="Notification Engine";
    public static String EXTRA_FRAGMENT="fragment";
    private NotificationManager notificationManager=null;
    public static ArrayList<ModuleNotification> all_activities;
    SimpleDateFormat sdf;

    public NotificationEngine(Context context){
        ctx=context;
        array_attendence = new ArrayList<>();
        array_food_menu = new ArrayList<>();
        array_shopping_list = new ArrayList<>();
        sdf = new SimpleDateFormat("dd MMM, HH a");
        initReceiver();
    }

    public static void initActivitiesArray(Context ctx){
        all_activities = new SQLManager(ctx).getActivities();
    }

    private void initReceiver() {
        final BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(N_ACTION_FILTER)){
                    String fragment = intent.getStringExtra(EXTRA_FRAGMENT);
                    getFragmentNotificationArray(fragment).clear();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(N_ACTION_FILTER);
        ctx.getApplicationContext().registerReceiver(br,filter);
    }

    public void showNotification(String fragment, JSONArray activities){
        fragment = fragment.toUpperCase(); // :p upper case fragment name
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        //*********************ringtone and vibration*****************************//
        Uri ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(ctx,ring);
        r.play();
        long[] pattern = {0,200,100,500};
        vibrator.vibrate(pattern,-1);
        /////////////////////////////////////////////////////////////
        //**************content *******************//
        amendArrayList(fragment,activities);
        ArrayList<ModuleNotification> array = getFragmentNotificationArray(fragment);
        SpannableStringBuilder s = new SpannableStringBuilder();
        NotificationCompat.BigTextStyle nStyle = new NotificationCompat.BigTextStyle();
        if(array == null) {
            MiscUtils.logE(TAG,"wrong module name sent from backend");
            try {
                throw new Exception("wrong module name from backend");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(ModuleNotification mn : array) {
            String content = mn.getDate()+":  "+mn.getnContent()+"\n";
            Spannable temp = new SpannableString(content);
            temp.setSpan(new StyleSpan(Typeface.BOLD),0,mn.getnContent().indexOf(":")+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.append(temp);
            //nStyle.addLine(mn.getDate()+": "+ MiscUtils.notificationContentFormat(mn.getnContent()));
        }
        nStyle.bigText(s);
        //*****************pending Intent**************//
        Intent in = new Intent(ctx,MainActivity.class);
        in.putExtra(EXTRA_FRAGMENT,getNotificationCode(fragment));
        PendingIntent pi = PendingIntent.getActivity(ctx,1,in,PendingIntent.FLAG_CANCEL_CURRENT);
        Intent dismiss = new Intent(N_ACTION_FILTER);
        dismiss.putExtra(EXTRA_FRAGMENT,fragment);
        PendingIntent di = PendingIntent.getBroadcast(ctx,2,dismiss,PendingIntent.FLAG_CANCEL_CURRENT);
        //********************************************//
        Notification n = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(MiscUtils.getBitmap("launcher_icon"))
                .setContentTitle(MiscUtils.getNotificationStyleTitle(fragment))
                .setContentText(array.size()+" "+((array.size()==1)?"activity":"activities"))
                .setStyle(nStyle)
                .setContentIntent(pi)
                .setDeleteIntent(di)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        //*- to cancel notification after click*//
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(getNotificationCode(fragment), n);
    }

//    private StringBuilder getNotificationText(ArrayList<ModuleNotification> fragmentNotificationArray) {
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < fragmentNotificationArray.size(); i++){
//            sb.append(fragmentNotificationArray.get(i).getDate()+" "+ fragmentNotificationArray.get(i).getnContent());
//        }
//        return sb;
//    }

    private void amendArrayList(String fragment, JSONArray activities) {
            Log.d(TAG,sdf.format(new Date()));
        try {
            if(all_activities.size()>MAX_ACTIVITY_COUNT)
                all_activities.remove(all_activities.size()-1);
            switch (fragment) {
                case FRAGMENT_ATTENDENCE:
                    for (int i = 0; i < activities.length(); i++) {
                        ModuleNotification mn = new ModuleNotification();
                        JSONObject workObject = activities.getJSONObject(i);
                        mn.setDate(sdf.format(new Date()));
                        mn.setnContent(workObject.getString("CONTENT"));
                        mn.setModule("Attendance");
                        array_attendence.add(mn);
                        all_activities.add(0,mn);
                    }
                    break;
                case FRAGMENT_SHOPPING_LIST:
                    for (int i = 0; i < activities.length(); i++) {
                        ModuleNotification mn = new ModuleNotification();
                        JSONObject workObject = activities.getJSONObject(i);
                        mn.setDate(sdf.format(new Date()));
                        mn.setnContent(workObject.getString("CONTENT"));
                        mn.setModule("Shopping_List");
                        array_shopping_list.add(mn);
                        all_activities.add(0,mn);
                    }
                    break;
                case FRAGMENT_FOOD_MENU:
                    for (int i = 0; i < activities.length(); i++) {
                        ModuleNotification mn = new ModuleNotification();
                        JSONObject workObject = activities.getJSONObject(i);
                        mn.setDate(sdf.format(new Date()));
                        mn.setnContent(workObject.getString("CONTENT"));
                        mn.setModule("food_menu");
                        array_food_menu.add(mn);
                        all_activities.add(0,mn);
                    }
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<ModuleNotification> getFragmentNotificationArray(String fragment){
        switch (fragment){
            case FRAGMENT_ATTENDENCE:
                return array_attendence;
            case FRAGMENT_FOOD_MENU:
                return array_food_menu;
            case FRAGMENT_SHOPPING_LIST:
                return array_shopping_list;
        }
        return null;
    }

    private int getNotificationCode(String fragment){
        switch (fragment){
            case FRAGMENT_ATTENDENCE:
                return 1;
            case FRAGMENT_FOOD_MENU:
                return 2;
            case FRAGMENT_SHOPPING_LIST:
                return 3;
        }
        return -1;
    }

    public void dismissNotification(String fragment){
        if(notificationManager!=null){
            notificationManager.cancel(getNotificationCode(fragment));
        }
    }
}
