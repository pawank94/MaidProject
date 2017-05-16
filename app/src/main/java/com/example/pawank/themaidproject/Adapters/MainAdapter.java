package com.example.pawank.themaidproject.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.Fragments.AttendenceFragment;
import com.example.pawank.themaidproject.Fragments.ConsoleFragment;
import com.example.pawank.themaidproject.Fragments.FoodMenuFragment;
import com.example.pawank.themaidproject.Fragments.MainFragment;
import com.example.pawank.themaidproject.Fragments.ShoppingListFragment;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by pawan.k on 18-04-2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.vHolder>{
    private ArrayList<ModuleNotification> activities;
    private Context ctx;
    private String TAG="Main Adapter";
    SimpleDateFormat sdf;
    private MainFragment fragmentInstance;
    public MainAdapter(ArrayList<ModuleNotification> activities, Context ctx, MainFragment mainFragment){
        this.activities = activities;
        this.ctx = ctx;
        this.fragmentInstance = mainFragment;
        sdf = new SimpleDateFormat("dd MMM, hh:mm a");
    }

    @Override
    public vHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.layout_m_main_recycler,null);
        return new vHolder(v);
    }

    @Override
    public void onBindViewHolder(vHolder holder, int position) {
        try {
            holder.time.setText(sdf.format(MiscUtils.STRINGDATEFORMAT.parse(activities.get(position).getDate())));
            //Log.d(TAG,activities.get(position).getDate());
            holder.title.setText(MiscUtils.getNotificationStyleTitle(activities.get(position).getModule()));
            holder.activity.setText(activities.get(position).getnContent());
            if(getToolbarBackgroundColor(activities.get(position).getModule())!=-1) {
                holder.toolbar.setBackgroundColor(ctx.getResources().getColor(getToolbarBackgroundColor(activities.get(position).getModule()),null));
            }
            holder.mCard.setTag(R.id.m_module_tag_name,activities.get(position).getModule());
        } catch (ParseException e) {
            e.printStackTrace();
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
        public CardView mCard;
        public vHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.m_time);
            mCard = (CardView) itemView.findViewById(R.id.m_card);
            title = (TextView) itemView.findViewById(R.id.m_mention_title);
            activity = (TextView) itemView.findViewById(R.id.m_mention_content);
            toolbar = (LinearLayout) itemView.findViewById(R.id.m_title_toolbar);
            initListeners();
        }
        public void initListeners(){
            mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String moduleName =(String) v.getTag(R.id.m_module_tag_name);
                    MainActivity activityInstance = (MainActivity)ctx;
                    startFragmentReplace(activityInstance,moduleName);
                }
            });
        }
    }

    private void startFragmentReplace(MainActivity activity,String module){
        switch (module.toUpperCase()){
            case NotificationEngine.FRAGMENT_ATTENDENCE:
                AttendenceFragment af = AttendenceFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, af)
                        .commit();
                break;
            case NotificationEngine.FRAGMENT_FOOD_MENU:
                FoodMenuFragment fm = FoodMenuFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fm)
                        .commit();
                break;
            case NotificationEngine.FRAGMENT_SHOPPING_LIST:
                ShoppingListFragment sl = ShoppingListFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, sl)
                        .commit();
                break;
        }
    }
}
