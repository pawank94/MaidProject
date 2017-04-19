package com.example.pawank.themaidproject.DataClass;

import com.example.pawank.themaidproject.utils.MiscUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by pawan.k on 06-02-2017.
 */

public class ShoppingList {
    String title;
    String last_modified;
    String last_modified_by;
    int id;
    String is_done;

    ArrayList<ShoppingListItem> items;

    public ShoppingList(){
        title=null;
        items=new ArrayList<>();
        last_modified=null;
        last_modified=null;
        is_done="N";
        id=-1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    public String getIs_done() {
        return is_done;
    }

    public void setIs_done(String is_done) {
        this.is_done = is_done;
    }

    public ArrayList<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingListItem> items) {
        this.items = items;
    }

    public static ArrayList<ShoppingList> prepareArrayForAdapter(JSONObject jObj) throws JSONException {
        JSONObject jx=jObj.getJSONObject("DATA");
        ArrayList<ShoppingList> tempShoppingLists=new ArrayList<>();
        ShoppingList sl;
        Iterator<String> keys=jx.keys();
        while(keys.hasNext()){
            String key=keys.next();
            sl = new ShoppingList();
            JSONObject workObject=jx.getJSONObject(key);
            sl.setTitle(workObject.getString("TITLE"));
            sl.setIs_done(workObject.getString("IS_DONE"));
            sl.setId(Integer.parseInt(workObject.getString("LIST_ID")));
            sl.setItems(ShoppingListItem.populateListItems(workObject));
            sl.setLast_modified(workObject.getString("LAST_MODIFIED"));
            sl.setLast_modified_by(workObject.getString("LAST_MODIFIED_BY"));
            tempShoppingLists.add(sl);
        }
        //sorting lists according to last modified
        Collections.sort(tempShoppingLists,new lastModifiedComparator());
        return tempShoppingLists;
    }

    private static class lastModifiedComparator implements Comparator<ShoppingList> {
        Date date1,date2;
        @Override
        public int compare(ShoppingList o1, ShoppingList o2) {
            try {
                date1 = MiscUtils.getDateObject(o1.getLast_modified());
                date2 = MiscUtils.getDateObject(o2.getLast_modified());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date1.before(date2))
                return 1;
            else if(date1.after(date2))
                return -1;
            else
                return 0;
        }
    }
}
