package com.example.pawank.themaidproject.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawank.themaidproject.Adapters.MainAdapter;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.R;

public class MainFragment extends Fragment {
    private RecyclerView mainRecyclerView;
    public MainAdapter adapter = null;
    private static String TAG="Main Fragment";

    public static MainFragment getInstance(){
        Log.d(TAG,"Main fragment");
        return new MainFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        Log.d(TAG,"onCreateView()");
        if(NotificationEngine.all_activities.size()!=0) {
            v = inflater.inflate(R.layout.fragment_main, null);
            mainRecyclerView = (RecyclerView) v.findViewById(R.id.activities_recycler_view);
        }else{
            v= inflater.inflate(R.layout.no_data,null);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mainRecyclerView!=null){
            if(adapter==null) {
                adapter = new MainAdapter(NotificationEngine.all_activities, getActivity());
                mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mainRecyclerView.setAdapter(adapter);
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }
}
