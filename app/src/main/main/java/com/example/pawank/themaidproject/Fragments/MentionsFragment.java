package com.example.pawank.themaidproject.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.SQLManager;

import org.json.JSONException;
import org.json.JSONObject;


public class MentionsFragment extends Fragment {

    private ProgressBar parentProgressBar;
    private RecyclerView recyclerView;
    private LinearLayout headerImage;
    private String data;
    private String sqlTable="mentionTable";
    private SQLManager sqlManager;
    private ComManager comManager;
    private String TAG="Mentions Fragment";

    public static MentionsFragment getInstance(){
        MentionsFragment mf = new MentionsFragment();
        return mf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlManager = new SQLManager(getActivity().getApplicationContext());
        comManager= new ComManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mentions,null);
        parentProgressBar = (ProgressBar) ((MainActivity)getContext()).findViewById(R.id.progressBarFragmentContainer);
        recyclerView = (RecyclerView) v.findViewById(R.id.m_recycler_view);
        headerImage = (LinearLayout) v.findViewById(R.id.m_header_image);
        return v;
    }

    @Override
    public void onStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if((data=sqlManager.getToken(sqlTable))==null)
                        {
                            //if this is being called for first time, populate sql table from server first
                            MiscUtils.logE(TAG,"token null,so calling server first");
                            recyclerView.setVisibility(View.GONE);
                            getMentionsData();
                        }
                        else{
                            try {
                                prepareDataOnView(new JSONObject(sqlManager.getData(sqlTable,"json")));
                            } catch (JSONException e) {

                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void prepareDataOnView(JSONObject jObj) {
        if(jObj!=null)
        {
            if(parentProgressBar.getVisibility()==View.VISIBLE)
                parentProgressBar.setVisibility(View.GONE);
            if(recyclerView.getVisibility()==View.GONE || recyclerView.getVisibility() == View.INVISIBLE)
                recyclerView.setVisibility(View.VISIBLE);
            //TODO: make after discussion
//            foodMenuArrayList = FoodMenu.prepareArrayForAdapter(jObj);
//            foodMenuAdapter=new FoodMenuAdapter(getActivity(),FoodMenuFragment.this,foodMenuArrayList,comManager);
//            recyclerView.setAdapter(foodMenuAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            recyclerView.setNestedScrollingEnabled(false);
//            if(getUpcomingMeal(jObj)!=null)
//                upcoming_meal.setText(getUpcomingMeal(jObj));
//            else
//                upcoming_meal.setText("N/A");
//            viewPrepared=true;
        }
        else{
            MiscUtils.logD(TAG,"prepareDataOnView received null");

        }

    }

    private void getMentionsData() {

    }
}
