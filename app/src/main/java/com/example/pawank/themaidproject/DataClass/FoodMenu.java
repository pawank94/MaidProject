package com.example.pawank.themaidproject.DataClass;

import android.util.Log;

import com.example.pawank.themaidproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class FoodMenu {
    public final int noOfMeal=2; //change if meal number changes
    public int id;
    public String dishName[],type[],day;
    public String isDefault[];
    Date lastModified[];
    public FoodMenu(){
        dishName = new String[noOfMeal];
        type = new String[noOfMeal];
        isDefault = new String[noOfMeal];
        lastModified = new Date[noOfMeal];
    }
    public String[] getDishName() {
        return dishName;
    }

    public void setDishName(String[] dishName) {
        this.dishName = dishName;
    }

    public int getNoOfMeal() {
        return noOfMeal;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String[] getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String[] isDefault) {
        this.isDefault = isDefault;
    }

    public Date[] getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date[] lastModified) {
        this.lastModified = lastModified;
    }

    public static ArrayList<FoodMenu> prepareArrayForAdapter(JSONObject jObj) throws JSONException, ParseException {
        ArrayList<FoodMenu> fm = new ArrayList<>();
        JSONObject jx = jObj.getJSONObject("DATA");
        Iterator<String> j = jx.keys();
        while(j.hasNext())
        {
            //important logic
            String key = j.next();
            JSONObject array = jx.getJSONObject(key);
            FoodMenu menuObject = new FoodMenu();
            //dishname
            String[] dishName = new String[]{array.getJSONObject("BREAKFAST").getString("DISH_NAME"), array.getJSONObject("DINNER").getString("DISH_NAME")};
            menuObject.setDishName(dishName);
            //type of dish
            String[] type = new String[]{array.getJSONObject("BREAKFAST").getString("TYPE"), array.getJSONObject("DINNER").getString("TYPE")};
            menuObject.setType(type);
            //if it is default
            String[] isDefault = new String[]{array.getJSONObject("BREAKFAST").getString("IS_DEFAULT"), array.getJSONObject("DINNER").getString("IS_DEFAULT")};
            menuObject.setIsDefault(isDefault);
            //last_modified
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1=sdf.parse(array.getJSONObject("BREAKFAST").getString("LAST_MODIFIED"));
            Date date2=sdf.parse(array.getJSONObject("DINNER").getString("LAST_MODIFIED"));
            Date[] lastModified = new Date[]{date1,date2};
            menuObject.setLastModified(lastModified);
            //set_day
            menuObject.setDay(array.getJSONObject("BREAKFAST").getString("DAY"));
            fm.add(menuObject);
        }
        //sorting fm on basis of days
        Collections.sort(fm,new daySorter());
        return fm;
    }
    //utility function for sorting
    private static int getDayIndex(String day) {
        switch (day)
        {
            case "SUNDAY":
                return 7;
            case "MONDAY":
                return 1;
            case "TUESDAY":
                return 2;
            case "WEDNESDAY":
                return 3;
            case "THURSDAY":
                return 4;
            case "FRIDAY":
                return 5;
            case "SATURDAY":
                return 6;
            default:

                return -1;
        }
    }
    static class daySorter implements Comparator<FoodMenu>{

        @Override
        public int compare(FoodMenu o1, FoodMenu o2) {
            if(getDayIndex(o1.getDay())>getDayIndex(o2.getDay()))
                return 1;
            else if(getDayIndex(o1.getDay())<getDayIndex(o2.getDay()))
                return -1;
            else
                return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
