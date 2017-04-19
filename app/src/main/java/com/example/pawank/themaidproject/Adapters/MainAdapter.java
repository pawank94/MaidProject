package com.example.pawank.themaidproject.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.util.ArrayList;

/**
 * Created by pawan.k on 18-04-2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.vHolder> {
    private ArrayList<ModuleNotification> activities;
    private Context ctx;
    private String TAG="Main Adapter";
    public MainAdapter(ArrayList<ModuleNotification> activities,Context ctx){
        this.activities = activities;
        this.ctx = ctx;
    }

    @Override
    public vHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.layout_m_main_recycler,null);
        return new vHolder(v);
    }

    @Override
    public void onBindViewHolder(vHolder holder, int position) {
        holder.time.setText(activities.get(position).getDate());
        holder.title.setText(MiscUtils.getNotificationStyleTitle(activities.get(position).getModule()));
        holder.activity.setText(activities.get(position).getnContent());
        if(getToolbarBackgroundColor(activities.get(position).getModule())!=-1) {
            holder.toolbar.setBackgroundColor(ctx.getResources().getColor(getToolbarBackgroundColor(activities.get(position).getModule()),null));
        }
    }

    private int getToolbarBackgroundColor(String module) {
        //Log.d(TAG,module.toLowerCase());
        switch (module.toLowerCase()){
            case "attendance":
                return R.color.material_blue;
            case "shopping_list":
                return R.color.material_violet;
            case "food_menu":
                return R.color.colorPrimary;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return (activities.size()<=NotificationEngine.MAX_ACTIVITY_COUNT)?activities.size():10;
    }

    public class vHolder extends RecyclerView.ViewHolder{
        public TextView time,title,activity;
        public LinearLayout toolbar;
        public vHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.m_time);
            title = (TextView) itemView.findViewById(R.id.m_mention_title);
            activity = (TextView) itemView.findViewById(R.id.m_mention_content);
            toolbar = (LinearLayout) itemView.findViewById(R.id.m_title_toolbar);
        }
    }
}
