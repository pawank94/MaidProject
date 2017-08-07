package com.example.pawank.themaidproject;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pawank.themaidproject.Fragments.AttendenceFragment;
import com.example.pawank.themaidproject.Fragments.ConsoleFragment;
import com.example.pawank.themaidproject.Fragments.FoodMenuFragment;
import com.example.pawank.themaidproject.Fragments.MainFragment;
import com.example.pawank.themaidproject.Fragments.ShoppingListFragment;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.Managers.SQLManager;
import com.example.pawank.themaidproject.Services.FirebaseMainService;
import com.example.pawank.themaidproject.Services.FirebaseMakeService;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.Managers.PrefManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    public ActionBarDrawerToggle mToggleDrawer;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ArrayList<String> mArrayForDrawer;
    public Toolbar mToolbar;
    private View v;
    private ComManager comManager;
    private int backCounter=0;
    private ProgressBar progressBar;
    private Handler mHandler;
    private SQLManager sqlManager;
    private View searchBarView = null;
    public static boolean isOpened;
    public static final int FRAGMENT_ATTENDENCE = 1;
    public static final int FRAGMENT_CONSOLE = 4;
    public static final int FRAGMENT_FOOD_MENU = 2;
    public static final int FRAGMENT_SHOPPING_LIST = 3;
    public static final int FRAGMENT_MAIN = 5;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String TAG="Main Activity";

    public static MainActivity getInstance() {
        return new MainActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backCounter=1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //just in case if splash screen is skipped
        MiscUtils.initMiscUtil(getApplicationContext());
        MiscUtils.initPrivateStorage(getApplicationContext());
        //*********************Firebase Initializations*******************//
        Intent firebaseMakeService,firebaseMainService;
        firebaseMainService = new Intent(MainActivity.this,FirebaseMainService.class);
        firebaseMakeService = new Intent(MainActivity.this, FirebaseMakeService.class);
        startService(firebaseMainService);
        startService(firebaseMakeService);
        FirebaseMainService.initAllActivities(getApplicationContext());
        /////////////////////////////////////////////////////////////////////////////////
        v = findViewById(R.id.drawer_layout);
        isOpened = true;
        comManager = new ComManager(getBaseContext());
        sqlManager = new SQLManager(getBaseContext());
        mHandler = new Handler();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBarFragmentContainer);
        setSupportActionBar(mToolbar);
        this.setUpNavigationBar();
        mToggleDrawer = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggleDrawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Snackbar.make(v, "Logged in as " + PrefManager.getSharedVal("username"), Snackbar.LENGTH_SHORT).show();
        setUpNavigationBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
        }
        else{
            mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        ///////////////////////////////////////////////////////////////////////////////////
        comManager.sendFirebaseDeviceKeyToServer(getApplicationContext(),FirebaseInstanceId.getInstance().getToken());
        if(getIntent().getIntExtra(NotificationEngine.EXTRA_FRAGMENT,-1)!=-1)
        {
            startFragmentTransaction(getIntent().getIntExtra(NotificationEngine.EXTRA_FRAGMENT,-1));
            NotificationEngine.clearActivitiesArray();
        }
        else{
            startFragmentTransaction(FRAGMENT_MAIN);
        }
        //*************************code for main view***********************************//

        ///////////////////////////////////////////////////////////////////////////////////
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
            }
            else{
                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            Fragment frg = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(frg!=null) {
                if(frg instanceof MainFragment){
                    if(backCounter == 2) {
                        super.onBackPressed();
                    }
                    else {
                        backCounter++;
                        Toast.makeText(MainActivity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                else{
                    backCounter = 1;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MainFragment.getInstance(MainActivity.this)).commit();
            }
            //super.onBackPressed();
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggleDrawer.syncState();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy() called");
        isOpened=false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOpened = false;
    }

    public void setUpNavigationBar() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                }
                else{
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                //remove all existing fragments on frame
                progressBar.setVisibility(View.VISIBLE);
                Fragment frg = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if(frg!=null)
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
                switch (item.getItemId()) {
                    case R.id.drawer_food_menu:
                            startFragmentTransaction(FRAGMENT_FOOD_MENU);
                        break;
                    case R.id.drawer_shopping_list:
                            startFragmentTransaction(FRAGMENT_SHOPPING_LIST);
                        break;
                    case R.id.drawer_console:
                            startFragmentTransaction(FRAGMENT_CONSOLE);
                        break;
                    case R.id.drawer_attendence:
                            startFragmentTransaction(FRAGMENT_ATTENDENCE);
                        break;
                    default:

                        break;
                }
                mDrawerLayout.closeDrawer(Gravity.LEFT, true);
                return false;
            }
        });
    }

    private void startFragmentTransaction(int fragment){
        switch (fragment){
            case FRAGMENT_FOOD_MENU:
                     /*
                             *Loading Fragment using thread to avoid blockage of UI thread
                             */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler inside thread;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FoodMenuFragment fm = FoodMenuFragment.newInstance();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container, fm)
                                        .commit();
                            }
                        },300);
                    }
                }).start();
                break;
            case FRAGMENT_SHOPPING_LIST:
                      /*
                             *Loading Fragment using thread to avoid blockage of UI thread
                             */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler inside thread;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ShoppingListFragment sl = ShoppingListFragment.newInstance();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container, sl)
                                        .commit();
                            }
                        },300);
                    }
                }).start();
                break;
            case FRAGMENT_ATTENDENCE:
                     /*
                     *Loading Fragment using thread to avoid blockage of UI thread
                     */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler inside thread;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AttendenceFragment af = AttendenceFragment.newInstance();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container, af)
                                        .commit();
                            }
                        },300);
                    }
                }).start();
                break;
            case FRAGMENT_CONSOLE:
                 /*
                         *Loading Fragment using thread to avoid blockage of UI thread
                         */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler inside thread;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //MakingActionBarTransparent
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    mToolbar.setBackground(getResources().getDrawable(R.color.textDarkBlack,null));

                                }
                                else{
                                    mToolbar.setBackground(getResources().getDrawable(R.color.textDarkBlack));
                                }
                                /////////////////////////////////////////////////////////////////
                                ConsoleFragment fm = ConsoleFragment.newInstance();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container, fm)
                                        .commit();
                            }
                        },300);
                    }
                }).start();

                break;
            case FRAGMENT_MAIN:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //handler inside thread;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MainFragment mf = MainFragment.getInstance(MainActivity.this);
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment_container, mf)
                                        .commit();
                            }
                        });
                    }
                }).start();

                break;
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
        sqlManager.saveActivities();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

}
