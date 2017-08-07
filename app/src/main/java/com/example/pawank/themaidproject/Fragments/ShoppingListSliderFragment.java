package com.example.pawank.themaidproject.Fragments;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pawank.themaidproject.Adapters.ListDataAdapter;
import com.example.pawank.themaidproject.DataClass.ShoppingList;
import com.example.pawank.themaidproject.DataClass.ShoppingListItem;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.utils.MiscUtils;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
/**
 * Created by pawan.k on 22-05-2017.
 */
public class ShoppingListSliderFragment extends Fragment {
    int position;
    TextView heading;
    RecyclerView sl_recycler;
    ImageButton cloud_button;
    private ComManager comManager;
    private String TAG = "SL slider Fragment";
    private ListDataAdapter staplesAdapter,veggiesAdapter,utilitiesAdapter;
    private static ShoppingListFragment parentInstance;

    public static ShoppingListSliderFragment newInstance(ShoppingListFragment shoppingListFragment, int position){
        ShoppingListSliderFragment instance = new ShoppingListSliderFragment();
        parentInstance = shoppingListFragment;
        Bundle b = new Bundle();
        b.putInt("position",position);
        instance.setArguments(b);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comManager = new ComManager(getActivity());
        position = getArguments() != null?getArguments().getInt("position"):1;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_sl_items_in_pager_view,container,false);
        heading = (TextView) v.findViewById(R.id.sl_sorted_heading);
        cloud_button = (ImageButton) v.findViewById(R.id.sl_sorted_upload);
        sl_recycler = (RecyclerView) v.findViewById(R.id.sl_sorted_recycler_view);
        RelativeLayout toolbar =(RelativeLayout) v.findViewById(R.id.sl_sorted_toolbar);
        heading.setText(getHeading(position));
        toolbar.setBackgroundColor(getResources().getColor(getColor(position),null));
        if(getAdapter(position)!=null)
            sl_recycler.setAdapter(getAdapter(position));
        sl_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cloud_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    seggregateUploadList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return v;
    }

    //this method will get all dirty items and sort them on basis of list ID, after that it'll give call to comManager to upload chunks
    private void seggregateUploadList() throws JSONException {
        ArrayList<ShoppingListItem> listToUpload = new ArrayList<>();
        for(ShoppingListItem sli : ShoppingList.getAllStaples())
        {
            if(sli.is_dirty==1) {
                listToUpload.add(sli);
            }
        }
        for(ShoppingListItem sli : ShoppingList.getAllVeggies())
        {
            if(sli.is_dirty==1) {
                listToUpload.add(sli);
            }
        }
        for(ShoppingListItem sli : ShoppingList.getAllUtilities())
        {
            if(sli.is_dirty==1) {
                listToUpload.add(sli);
            }
        }
        //sorting on basis of ListId to seggregate all items of same list
        Collections.sort(listToUpload,new ShoppingListItem.ListItemListIdComparator());

        Log.d(TAG,"control here");
        for(ShoppingListItem sli:listToUpload){
            Log.d(TAG,sli.getTitle()+" "+sli.getList_id()+" "+sli.getList_title());
            Log.d(TAG,"------------------------------------");
        }

        //call comManager for uploading chunks

        Callback callback1 = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                Snackbar.make(getActivity().findViewById(android.R.id.content)," changes successfully uploaded!! ",Snackbar.LENGTH_SHORT).show();
                MiscUtils.logD(TAG,"chunk uploaded");
                parentInstance.getAllShoppingList();
                notifyAlladapters();
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"upload successfully!! some changes might have failed",Snackbar.LENGTH_SHORT).show();
                MiscUtils.logD(TAG,"chunk upload failed");
                parentInstance.getAllShoppingList();
                notifyAlladapters();
            }
        };
        //dummy callback
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {

            }
            @Override
            public void onFailure(Object obj) throws JSONException {

            }
        };


        Log.d(TAG,listToUpload.size()+"");
        if(listToUpload.size()>0){
            int index = 0;
            int i;
            int list_id_on_index =  listToUpload.get(0).getList_id();
            for(i=1;i<listToUpload.size();i++){
                Log.d(TAG,"chunk in");
                if(listToUpload.get(i).getList_id()!=list_id_on_index){
                    Log.d(TAG,"chunk selected"+i+" "+index);
                    ShoppingList sl = new ShoppingList();
                    sl.setId(listToUpload.get(i-1).getList_id());//get List info and set it to list
                    sl.setTitle(listToUpload.get(i-1).getList_title());
                    Log.d(TAG,"size of new list"+new ArrayList<>(listToUpload.subList(index,i)).size());
                    sl.setItems(new ArrayList<>(listToUpload.subList(index,i)));
                    comManager.uploadShoppingList(getActivity(),callback,MiscUtils.shoppingListJSONParser(sl));
                    index=i;
                    list_id_on_index = listToUpload.get(i).getList_id();
                }
            }
            //for last segment
            ShoppingList sl = new ShoppingList();
            sl.setId(listToUpload.get(i-1).getList_id());//get List info and set it to list
            sl.setTitle(listToUpload.get(i-1).getList_title());
            sl.setItems(new ArrayList<>(listToUpload.subList(index,i)));
            comManager.uploadShoppingList(getActivity(),callback1,MiscUtils.shoppingListJSONParser(sl));
        }
        else{
            Snackbar.make(getActivity().findViewById(android.R.id.content),"No items to upload!!",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void notifyAlladapters() {
        utilitiesAdapter.notifyDataSetChanged();
        veggiesAdapter.notifyDataSetChanged();
        staplesAdapter.notifyDataSetChanged();
    }

    private int getColor(int position) {
        switch(position){
            case 0:
                return R.color.staple_color;
            case 1:
                return R.color.veggy_color;
            case 2:
                return R.color.utilities_color;
        }
        return -1;
    }

    private RecyclerView.Adapter getAdapter(int position) {
        switch(position){
            case 0:
                staplesAdapter = new ListDataAdapter(getActivity(), ShoppingList.getAllStaples());
                return staplesAdapter;
            case 1:
                veggiesAdapter = new ListDataAdapter(getActivity(), ShoppingList.getAllVeggies());;
                return veggiesAdapter;
            case 2:
                utilitiesAdapter = new ListDataAdapter(getActivity(), ShoppingList.getAllUtilities());
                return utilitiesAdapter;
        }
        return null;
    }
    private String getHeading(int position) {
        switch(position){
            case 0:
                return "STAPLE";
            case 1:
                return "VEGGY";
            case 2:
                return "UTILITIES";
        }
        return null;
    }
}
