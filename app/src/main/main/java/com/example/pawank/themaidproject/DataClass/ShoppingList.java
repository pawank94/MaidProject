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
    private static ArrayList<ShoppingListItem> allUtilities,allVeggies,allStaples;

    public static void initSortedShoppingLists(){
        allUtilities = new ArrayList<>();
        allVeggies = new ArrayList<>();
        allStaples = new ArrayList<>();
    }

    public static void clearArrays(){
        if(allUtilities!=null)
            allUtilities.clear();
        if(allVeggies!=null)
            allVeggies.clear();
        if(allStaples!=null)
            allStaples.clear();
    }

    ArrayList<ShoppingListItem> items;

    public ShoppingList(){
        title=null;
        items=new ArrayList<>();
        last_modified=null;
        last_modified=null;
        is_done="N";
        id=-1;
    }
    public static ArrayList<ShoppingListItem> getAllVeggies() {
        return allVeggies;
    }
    public static ArrayList<ShoppingListItem> getAllUtilities() {
        return allUtilities;
    }
    public static ArrayList<ShoppingListItem> getAllStaples() {
        return allStaples;
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
        initSortedShoppingLists();
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
        /*code to fill data in sorted arrays*/
        clearArrays();
        for(ShoppingList tempsl:tempShoppingLists){
            for(ShoppingListItem sli:tempsl.getItems()){
                if(sli.getType().equalsIgnoreCase("staple"))
                    allStaples.add(sli);
                else if(sli.getType().equalsIgnoreCase("veggy"))
                    allVeggies.add(sli);
                else
                    allUtilities.add(sli);
            }
        }
        //sorting lists according to last modified
        Collections.sort(tempShoppingLists,new shoppingListLastModifiedComparator());
        //sorting other list in accordance to bought status
        Collections.sort(allStaples,new ShoppingListItem.ListItemIsBoughtComparator());
        Collections.sort(allVeggies,new ShoppingListItem.ListItemIsBoughtComparator());
        Collections.sort(allUtilities,new ShoppingListItem.ListItemIsBoughtComparator());
        return tempShoppingLists;
    }


    private static class shoppingListLastModifiedComparator implements Comparator<ShoppingList> {
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
