package com.example.pawank.themaidproject.DataClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by pawan.k on 06-02-2017.
 */

public class ShoppingListItem {
    public String title;
    public String type;
    public String bought_by;
    public String bought_on;
    public String item;
    public String last_modified;
    public String is_bought;
    public String is_active;
    public String bought_by_name;
    public String requested_by_name;
    public int id;
    public int list_id;
    public int requested_by;
    public String requested_on;
    public int is_dirty;
    public String list_title;

    public ShoppingListItem(){
        is_bought = is_active =title=type= bought_by = bought_on =item= last_modified =null;
        id= list_id = requested_by =-1;
    }

    public String getList_title() {
        return list_title;
    }
    public void setList_title(String list_title) {
        this.list_title = list_title;
    }

    public String getBought_by_name() {
        return bought_by_name;
    }

    public void setBought_by_name(String bought_by_name) {
        this.bought_by_name = bought_by_name;
    }

    public String getRequested_by_name() {
        return requested_by_name;
    }

    public void setRequested_by_name(String requested_by_name) {
        this.requested_by_name = requested_by_name;
    }

    public int getIs_dirty() {
        return is_dirty;
    }

    public void setIs_dirty(int is_dirty) {
        this.is_dirty = is_dirty;
    }

    public String getRequested_on() {
        return requested_on;
    }

    public void setRequested_on(String requested_on) {
        this.requested_on = requested_on;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBought_by() {
        return bought_by;
    }

    public void setBought_by(String bought_by) {
        this.bought_by = bought_by;
    }

    public String getBought_on() {
        return bought_on;
    }

    public void setBought_on(String bought_on) {
        this.bought_on = bought_on;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public int getRequested_by() {
        return requested_by;
    }

    public void setRequested_by(int requested_by) {
        this.requested_by = requested_by;
    }

    public String isBought() {
        return is_bought;
    }

    public void setBought(String bought) {
        is_bought = bought;
    }

    public String isActive() {
        return is_active;
    }

    public void setActive(String active) {
        is_active = active;
    }

    public static ArrayList<ShoppingListItem> populateListItems(JSONObject jObj) throws JSONException {
        ArrayList<ShoppingListItem> workItems = new ArrayList<>();
        JSONObject workObject = jObj.getJSONObject("LIST_ITEMS");
        Iterator<String> it = workObject.keys();
        while(it.hasNext())
        {
            String key=it.next();
            ShoppingListItem sli = new ShoppingListItem();
            JSONObject currentObjectDetails = workObject.getJSONObject(key);
            sli.setTitle(key);
            int id = Integer.parseInt(currentObjectDetails.getString("ID"));
            sli.setId(id);
            int list_id = Integer.parseInt(currentObjectDetails.getString("LIST_ID"));
            sli.setList_id(list_id);
            sli.setType(currentObjectDetails.getString("TYPE"));
            int requester=Integer.parseInt(currentObjectDetails.getString("REQUESTED_BY"));
            sli.setRequested_by(requester);
            sli.setBought(currentObjectDetails.getString("IS_BOUGHT"));
            sli.setList_title(currentObjectDetails.getString("TITLE"));
            sli.setBought_by(currentObjectDetails.getString("BOUGHT_BY"));
            sli.setBought_on(currentObjectDetails.getString("BOUGHT_ON"));
            sli.setActive(currentObjectDetails.getString("IS_ACTIVE"));
            sli.setLast_modified(currentObjectDetails.getString("LAST_MODIFIED"));
            sli.setRequested_on(currentObjectDetails.getString("REQUESTED_ON"));
            sli.setRequested_by_name(currentObjectDetails.getString("REQUESTED_BY_NAME"));
            sli.setBought_by_name(currentObjectDetails.getString("BOUGHT_BY_NAME"));
            workItems.add(sli);
        }
        //sorting on basis of isbought
        Collections.sort(workItems,new ListItemIsBoughtComparator());
        return workItems;
    }

    public static class ListItemIsBoughtComparator implements Comparator<ShoppingListItem>{
        @Override
        public int compare(ShoppingListItem o1, ShoppingListItem o2) {
            if(o1.isBought().equals("Y")&&o2.isBought().equals("N")) {
                return 1;
            }
            else if(o1.isBought().equals("N")&&o2.isBought().equals("Y")){
                return -1;
            }
            else{
                return 0;
            }
        }
    }

    public static class ListItemListIdComparator implements Comparator<ShoppingListItem>{
        @Override
        public int compare(ShoppingListItem o1, ShoppingListItem o2) {
            if(o1.getList_id()>o2.getList_id())
                return 1;
            else if(o1.getList_id()<o2.getList_id())
                return -1;
            else
                return 0;
        }
    }

}
