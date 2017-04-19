package com.example.pawank.themaidproject.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.FoodMenu;
import com.example.pawank.themaidproject.Fragments.FoodMenuFragment;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.Managers.ComManager;

import java.util.ArrayList;

/**
 * Created by nexusstar on 28/1/17.
 */

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.ViewHold> implements View.OnClickListener{
    ArrayList<FoodMenu> foodMenuArrayList;
    private final String TAG="FoodMenuAdapter";
    Context ctx;
    ComManager comManager;
    FoodMenuFragment fragment;

    public FoodMenuAdapter(Context ctx, FoodMenuFragment fr, ArrayList<FoodMenu> fm, ComManager comManager){
        this.ctx=ctx;
        foodMenuArrayList=fm;
        this.comManager=comManager;
        this.fragment=fr;
    }
    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fm_list,parent,false);
        return new ViewHold(v);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.dayName.setText(foodMenuArrayList.get(position).getDay());
        // holder.day.setImageResource(getDayImageResource(foodMenuArrayList.get(position).getDay()));
        String text_for_breakfast = "<b>Breakfast:</b> "+foodMenuArrayList.get(position).getDishName()[0].toLowerCase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.dayMeal.setText(Html.fromHtml(text_for_breakfast,Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            holder.dayMeal.setText(Html.fromHtml(text_for_breakfast));
        }
        String text_for_dinner = "<b>Dinner:</b> "+foodMenuArrayList.get(position).getDishName()[1].toLowerCase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.nightMeal.setText(Html.fromHtml(text_for_dinner,Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            holder.nightMeal.setText(Html.fromHtml(text_for_dinner));
        }

    }


    @Override
    public int getItemCount() {
        return foodMenuArrayList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHold extends RecyclerView.ViewHolder{
        public ImageView menu;
        public TextView dayMeal,nightMeal,dayName;
        public ViewHold(View itemView) {
            super(itemView);
            dayName = (TextView)itemView.findViewById(R.id.fm_week_day);
            nightMeal = (TextView)itemView.findViewById(R.id.fm_dinner);
            dayMeal = (TextView)itemView.findViewById(R.id.fm_breakfast);
//            dayMeal.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"font/SourceSansPro-Regular.ttf"));
//            nightMeal.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"font/SourceSansPro-Regular.ttf"));
        }
    }
}
