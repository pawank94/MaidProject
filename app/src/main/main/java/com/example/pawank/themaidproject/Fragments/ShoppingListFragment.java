package com.example.pawank.themaidproject.Fragments;

import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pawank.themaidproject.Adapters.ShoppingListAdapter;
import com.example.pawank.themaidproject.DataClass.ShoppingList;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.SQLManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


public class ShoppingListFragment extends Fragment {
    private SQLManager sqlManager;
    private ComManager comManager;
    private ProgressBar parentProgressBar;
    private RecyclerView recyclerView;
    private final String sqlTable = "shoppingListTable";
    private final String TAG = "Shopping List Fragment";
    private ArrayList<ShoppingList> shoppingLists=new ArrayList<>();
    private FloatingActionButton fab;
    private boolean viewPrepared=false;
    private ShoppingListAdapter shoppingListAdapter;
    private Handler mHandler;
    private RelativeLayout header_image;
    private String header_image_key = "shopping_list_header_image";
    private static ShoppingListFragment fragmentInstance;
    private static Menu mMenu;
    private LinearLayout recycler_view_layout;
    private ViewPager sl_pager=null;
    private TextView total_lists;
    private View v;
    private String data;
    private int NO_OF_PAGER_PAGES = 3;
    public boolean pager_view_enabled = false;
    private View pager_view;
    private SlAdapter sl_adapter_instance = null;

    public ShoppingListFragment(){
        fragmentInstance=ShoppingListFragment.this;
    }

    public static ShoppingListFragment getInstance() {
        return fragmentInstance;
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sqlManager=new SQLManager(getActivity().getApplicationContext());
        comManager=new ComManager(getActivity().getApplication());
        mHandler = new Handler(Looper.getMainLooper());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.sl_recycler_view);
        parentProgressBar = (ProgressBar) ((MainActivity) getContext()).findViewById(R.id.progressBarFragmentContainer);
        fab = (FloatingActionButton) v.findViewById(R.id.sl_add_note);
        header_image = (RelativeLayout) v.findViewById(R.id.sl_header_image);
        recycler_view_layout = (LinearLayout) v.findViewById(R.id.sl_main_layout);
        pager_view = v.findViewById(R.id.sl_pager);
        sl_pager = (ViewPager) v.findViewById(R.id.shopping_list_view_pager);
        total_lists = (TextView) v.findViewById(R.id.sl_total_cards);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*placing data inside handler inside thread*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //addlist feature for floating action button
                        addListFromFab();
                        if ((data = sqlManager.getToken(sqlTable)) == null) {
                            MiscUtils.logE(TAG, "token null");
                            recyclerView.setVisibility(View.GONE);
                            getAllShoppingList();
                        } else {
                            try {
                                //MiscUtils.logD(TAG,"preparedata"+sqlManager.getData(sqlTable));
                                prepareDataOnView(new JSONObject(sqlManager.getData(sqlTable, "json")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Syncing with server", Snackbar.LENGTH_SHORT).show();
                            getAllShoppingList();
                        }
                        header_image.setBackground(new BitmapDrawable(MiscUtils.getBitmap(header_image_key)));
                        recycler_view_layout.setBackground(getResources().getDrawable(R.drawable.sl_stretched_doodle));
                    }
                });
            }
        }).start();
    }

    private void addListFromFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPrepared){
                    final EditText listName =new EditText(getActivity());
                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                    .setTitle("Enter Lists Name")
                                                    .setView(listName)
                                                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            ShoppingList sl=new ShoppingList();
                                                            sl.setTitle(listName.getText().toString());
                                                            sl.setIs_done("N");
                                                            shoppingLists.add(0,sl);
                                                            shoppingListAdapter.notifyDataSetChanged();
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).create();
                    ad.show();
                }
            }
        });
    }

    public void getAllShoppingList() {
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                if(viewPrepared) {
                      shoppingLists.clear();
                      shoppingLists.addAll(ShoppingList.prepareArrayForAdapter((JSONObject) obj));
                      shoppingListAdapter.notifyDataSetChanged();
                }
                else{
                    prepareDataOnView((JSONObject) obj);
                }
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Server Error Occured",Snackbar.LENGTH_LONG)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getAllShoppingList();
                            }
                        }).show();
            }
        };
        comManager.getAllShoppingList(getActivity().getApplicationContext(),callback);
    }

    private void prepareDataOnView(JSONObject jObj) throws JSONException {
        if(jObj!=null)
        {
            if(parentProgressBar.getVisibility()==View.VISIBLE)
                parentProgressBar.setVisibility(View.GONE);
            if(recyclerView.getVisibility()==View.GONE||recyclerView.getVisibility()==View.INVISIBLE)
                recyclerView.setVisibility(View.VISIBLE);
            shoppingLists=ShoppingList.prepareArrayForAdapter(jObj);
            //sorting here

            shoppingListAdapter = new ShoppingListAdapter(getActivity(),ShoppingListFragment.this,shoppingLists,comManager);
            recyclerView.setAdapter(shoppingListAdapter);
            total_lists.setText(shoppingLists.size()+" Cards");
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            viewPrepared=true;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.fm_actionbar_menu,menu);
        mMenu=menu;
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_bar_refresh:
                getAllShoppingList();
                Snackbar.make(getActivity().findViewById(R.id.drawer_layout),"Syncing with server",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_pager_view:
                if(!pager_view_enabled){
                    Log.d(TAG,"enabling pager view");
                    pager_view.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    initializePagerView();
                }else{
                    pager_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                pager_view_enabled = !pager_view_enabled;
                break;
        }
        return true;
    }
    private void initializePagerView() {
        if(sl_adapter_instance==null)
            sl_adapter_instance = new SlAdapter(getActivity().getSupportFragmentManager());
        sl_pager.setAdapter(sl_adapter_instance);
    }

    //pagerAdapter
    public class SlAdapter extends FragmentStatePagerAdapter
    {
        public SlAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ShoppingListSliderFragment.newInstance(ShoppingListFragment.this,position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NO_OF_PAGER_PAGES;
        }
    }
}
