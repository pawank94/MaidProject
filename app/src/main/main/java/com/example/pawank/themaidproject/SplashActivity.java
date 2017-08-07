package com.example.pawank.themaidproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.utils.ConsoleCommands;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.PrefManager;
import com.example.pawank.themaidproject.Managers.SQLManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SplashActivity extends AppCompatActivity {
    private SQLManager sqlManager;
    private String TAG="Splash Screen Activity";
    private ComManager comManager;
    boolean token_table_exists;
    private final int total_permission_count = 1; //change for multiple permission
    private int permission_count=0;
    private AlertDialog alertDialog;
    private String checksum=null;
    private final int WRITE_STORAGE_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashNoActionBar);
        super.onCreate(savedInstanceState);
        //*- splash screen can be made much faster if you dont inflate a layout .. use windowBackground in style and set a PICTURE(png) as background
//        //TODO:permissions
//        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
//        {
//            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_STORAGE_PERMISSION);
//        }
//        else{
//            permission_count++;
//        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_STORAGE_PERMISSION);
        }
        else{
            permission_count++;
        }
        proceed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
//            case READ_STORAGE_PERMISSION:
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    permission_count++;
//                }else {
//                    permissionRequiredAlert(READ_STORAGE_PERMISSION,Manifest.permission.READ_EXTERNAL_STORAGE,"read_storage");
//                }
//                break;
            case WRITE_STORAGE_PERMISSION:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    permission_count++;
                }else {
                    permissionRequiredAlert(WRITE_STORAGE_PERMISSION,Manifest.permission.WRITE_EXTERNAL_STORAGE,"write_storage");
                }
                break;
        }
        proceed();
    }
    //permission utility
    public void permissionRequiredAlert(final int permission, final String code, String permission_name)
    {
        new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("Critical Permission")
                .setMessage(permission_name+" permission is required")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{code},permission);
                    }
                })
                .create().show();
    }
    // if all permission granted then proceed furthur
    public void proceed() {
        //to avoid too much processing on front end
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,permission_count+""+total_permission_count);
                        if(permission_count==total_permission_count) {
                            //Check if google play service is available, if not redirect user
                            if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext())== ConnectionResult.SUCCESS)
                            {
                                Log.d(TAG,"playService Exists");
                            }
                            else {
                                Log.e(TAG,"playService does not Exists");
                                GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(SplashActivity.this);
                            }
                            //***************Initializations****************//
                            comManager=new ComManager(getBaseContext());
                            sqlManager = new SQLManager(getApplicationContext());
                            MiscUtils.initMiscUtil(getApplicationContext());
                            PrefManager.initSharedPref(getApplicationContext());
                            MiscUtils.initPrivateStorage(getApplicationContext());
                            MiscUtils.initCache();
                            MiscUtils.initConsoleFile(getApplicationContext());
                            NotificationEngine.dismissAllNotifications(getApplicationContext());
                            sqlManager.putUrlTableAddress("https://www.firest0ne.me/SHEapp/Api");
                            comManagerInitUrlAddress();
                            initializeBitmapCache();
                            ConsoleCommands.initConsoleCommand(SplashActivity.this);
                            //Initialization of all basic utilities
                            //TODO: ComManager initialization from db
                            //TODO: hardcoding removal
                            //
                            MiscUtils.logD("Proceed","function called");
                            if((checksum=sqlManager.getCheckSum())==null)
                            {
                                //login screen
                                Intent in=new Intent(SplashActivity.this,LoginActivity.class);
                                startActivity(in);
                                finish();
                            }
                            else{
                                //call server for check
                                if(checksum!=null) {
                                    PrefManager.setSharedVal("checksum", checksum);
                                    if(sqlManager.getUserName()!=null)
                                        PrefManager.setSharedVal("username",sqlManager.getUserName());
                                }
                                startMainActivity();
                            }

                        }
                        else {
                            Log.d("proceed function","permissions pending");
                        }
                    }
                });
            }
        }).start();
    }

    private void startMainActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    Intent in = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(in);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //cache bitmaps
    private void initializeBitmapCache() {
        MiscUtils.logD(TAG,"initializing bitmap cache");
        //add all new Images to cache which are going to be used as header
        MiscUtils.cacheBitmap(SplashActivity.this,"food_menu_header_image",R.drawable.ic_food_menu_header,200);
        MiscUtils.cacheBitmap(SplashActivity.this,"shopping_list_header_image",R.drawable.ic_shopping_list_header,200);
        MiscUtils.cacheBitmap(SplashActivity.this,"food_menu_doodle",R.drawable.ic_fm_doodle,500);
        MiscUtils.cacheBitmap(SplashActivity.this,"launcher_icon",R.mipmap.ic_launcher,500);

    }
    //com Manager initializations
    public void comManagerInitUrlAddress() {
        sqlManager.getUrlTableAddress(getBaseContext());
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext())== ConnectionResult.SUCCESS)
//        {
//            proceed();
//        }
//        else {
//            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(SplashActivity.this);
//        }
//    }
}
