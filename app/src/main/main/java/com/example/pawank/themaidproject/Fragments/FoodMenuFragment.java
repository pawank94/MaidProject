package com.example.pawank.themaidproject.Fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawank.themaidproject.Adapters.FoodMenuAdapter;
import com.example.pawank.themaidproject.DataClass.FoodMenu;
import com.example.pawank.themaidproject.DataClass.ShoppingList;
import com.example.pawank.themaidproject.DataClass.ShoppingListItem;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.Managers.PrefManager;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.SQLManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FoodMenuFragment extends Fragment {
    private SQLManager sqlManager;
    private ComManager comManager;
    private RecyclerView recyclerView;
    private String sqlTable = "foodMenuTable";
    String data;
    private View v;
    private final String TAG="FoodMenuFragment";
    private ProgressBar parentProgressBar;
    private FoodMenuAdapter foodMenuAdapter;
    private boolean viewPrepared=false;
    private ArrayList<FoodMenu> foodMenuArrayList;
    private Handler mHandler;
    private FloatingActionButton fab;
    private LinearLayout header_image;
    private int header_height,header_width;
    private String header_image_key = "food_menu_header_image";
    private String doodle_image_key = "food_menu_doodle";
    private Menu mMenu;
    private static FoodMenuFragment fragmentInstance;
    private LinearLayout recyclerViewLayout;
    private LinearLayout main_layout;
    private TextView upcoming_meal;
    Toolbar tb;
    Spinner sp;
    EditText name,date;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener datePickerDialogListener;
    ImageButton change;
    ImageButton cancel;
    Switch aSwitch;
    String Day;
    private String day;
    private ProgressBar uploadProgressBar;
    private NestedScrollView nested_Scroll_view;
    private boolean dateFirstTime=true;
    private CheckBox add_to_sl;
    ArrayList<ShoppingList> shoppingLists;
    private ShoppingListItem sli;
    private EditText newItemName;
    private ImageButton newItemBackButton,newItemSaveButton;
    private Spinner newItemType;
    private Switch newItemIsBought;
    private ArrayList<ShoppingListItem> items_list;
    private String food_update_token;
    private String new_list_title;
    private String shopping_list_update_token;
    public FoodMenuFragment() {
        fragmentInstance=FoodMenuFragment.this;
    }

    public static FoodMenuFragment newInstance() {
        FoodMenuFragment fragment = new FoodMenuFragment();
        return fragment;
    }
    public static FoodMenuFragment getInstance(){
        return fragmentInstance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        recyclerView=(RecyclerView)v.findViewById(R.id.food_menu_list);
        setHasOptionsMenu(true);
        sqlManager=new SQLManager(getActivity().getApplicationContext());
        comManager=new ComManager(getActivity().getApplication());
        mHandler = new Handler(Looper.getMainLooper());
        shoppingLists = new ArrayList<>();
        items_list = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if((data=sqlManager.getToken(sqlTable))==null)
                        {
                            //if this page is being opened first time, populate sql table
                            MiscUtils.logE(TAG,"token null");
                            recyclerView.setVisibility(View.GONE);
                            getMenu();
                        }
                        else{
                            try {
                                prepareDataOnView(new JSONObject(sqlManager.getData(sqlTable,"json")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Snackbar.make(getActivity().findViewById(R.id.drawer_layout),"Syncing with server",Snackbar.LENGTH_SHORT).show();
                            getMenu();
                        }
                        header_image.setBackground(new BitmapDrawable(MiscUtils.getBitmap(header_image_key)));
                        main_layout.setBackground(getResources().getDrawable(R.drawable.fm_stretched_doodle));
                        setupView();
                    }
                });
            }
        }).start();
    }

    private void setupView() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEditFoodMenuDialog();
            }
        });
    }

    public void prepareDataOnView(JSONObject jObj) throws JSONException, ParseException {
        if(jObj!=null)
        {
            if(parentProgressBar.getVisibility()==View.VISIBLE)
                parentProgressBar.setVisibility(View.GONE);
            if(recyclerView.getVisibility()==View.GONE || recyclerView.getVisibility() == View.INVISIBLE)
                recyclerView.setVisibility(View.VISIBLE);
            foodMenuArrayList = FoodMenu.prepareArrayForAdapter(jObj);
            foodMenuAdapter=new FoodMenuAdapter(getActivity(),FoodMenuFragment.this,foodMenuArrayList,comManager);
            recyclerView.setAdapter(foodMenuAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setNestedScrollingEnabled(false);
            if(getUpcomingMeal(jObj)!=null)
                upcoming_meal.setText(getUpcomingMeal(jObj));
            else
                upcoming_meal.setText("N/A");
            viewPrepared=true;
        }
        else{
            MiscUtils.logE(TAG,"prepareDataOnView received null");

        }

    }

    private String getUpcomingMeal(JSONObject jObj) {
        String meal_name = null;
        Calendar c=Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        String meal = (c.get(Calendar.HOUR_OF_DAY) < 12) ? "BREAKFAST" : "DINNER";
        try {
            //Log.d(TAG,jObj.getString("STATUS").toString());
            meal_name = jObj.getJSONObject("DATA").getJSONObject(MiscUtils.getDayImageResource(day)).getJSONObject(meal).getString("DISH_NAME");
            Log.d(TAG,meal_name);
        } catch (JSONException e) {
            e.printStackTrace();
            meal_name=null;
        }

        return meal_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_food_menu, container, false);
        recyclerView=(RecyclerView)v.findViewById(R.id.food_menu_list);
        parentProgressBar = (ProgressBar) ((MainActivity)getContext()).findViewById(R.id.progressBarFragmentContainer);
        header_image = (LinearLayout) v.findViewById(R.id.fm_header_image);
        main_layout = (LinearLayout) v.findViewById(R.id.fm_main_layout);
        fab = (FloatingActionButton) v.findViewById(R.id.fm_fab);
        upcoming_meal = (TextView) v.findViewById(R.id.fm_upcoming_meal);
        nested_Scroll_view = (NestedScrollView) v.findViewById(R.id.fm_nested_scroll);
        return v;
    }

    public void getMenu() {
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                JSONObject jObj = (JSONObject)obj;
                    if(viewPrepared) {
                    foodMenuArrayList.clear();
                    foodMenuArrayList.addAll(FoodMenu.prepareArrayForAdapter(jObj));
                    foodMenuAdapter.notifyDataSetChanged();
                }
                else{
                    prepareDataOnView(jObj);
                }
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Server Error Occured",Snackbar.LENGTH_LONG)
                .setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getMenu();
                    }
                }).show();
            }
        };
        comManager.getFoodMenu(getActivity(),callback);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.fm_actionbar_menu,menu);
        mMenu = menu;
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_bar_refresh:
                    getMenu();
                    Snackbar.make(getActivity().findViewById(R.id.drawer_layout),"Syncing with server",Snackbar.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /*utility function*/
//    private int getDayImageResource(String day) {
//        switch (day)
//        {
//            case "SUNDAY":
//                return R.drawable.ic_sunday;
//            case "MONDAY":
//                return R.drawable.ic_monday;
//            case "TUESDAY":
//                return R.drawable.ic_tuesday;
//            case "WEDNESDAY":
//                return R.drawable.ic_wednesday;
//            case "THURSDAY":
//                return R.drawable.ic_thursday;
//            case "FRIDAY":
//                return R.drawable.ic_friday;
//            case "SATURDAY":
//                return R.drawable.ic_saturday;
//            default:
//                MiscUtils.logE(TAG,"invalid parameter passed:"+day);
//                return -1;
//        }
//    }
//
//    private int getDayImageResourceNumber(int day) {
//        switch (day)
//        {
//            case Calendar.SUNDAY:
//                return 0;
//            case Calendar.MONDAY:
//                return 1;
//            case Calendar.TUESDAY:
//                return 2;
//            case Calendar.WEDNESDAY:
//                return 3;
//            case Calendar.THURSDAY:
//                return 4;
//            case Calendar.FRIDAY:
//                return 5;
//            case Calendar.SATURDAY:
//                return 6;
//            default:
//                MiscUtils.logE(TAG,"invalid parameter passed:"+day);
//                return -1;
//        }
//    }
    /***************************************************/

    public void createEditFoodMenuDialog(){
        /** creation of dialog **/
        final View fm_dialog = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fm__dialog_change,null);
        dateFirstTime=true;
        tb = (Toolbar)fm_dialog.findViewById(R.id.toolbar2);
        sp = (Spinner)fm_dialog.findViewById(R.id.spinner);
        name = (EditText)fm_dialog.findViewById(R.id.editText5);
        date = (EditText)fm_dialog.findViewById(R.id.editText7);
        add_to_sl = (CheckBox) fm_dialog.findViewById(R.id.fm_add_to_sl);
        calendar=Calendar.getInstance();
        change = (ImageButton)fm_dialog.findViewById(R.id.fm_change_dialog_save);
        cancel = (ImageButton)fm_dialog.findViewById(R.id.fm_change_dialog_back);
        aSwitch = (Switch)fm_dialog.findViewById(R.id.switch2);
        uploadProgressBar = (ProgressBar) fm_dialog.findViewById(R.id.fm_change_meal_progressBar);
        Calendar c=Calendar.getInstance();
        day = MiscUtils.getDayImageResource(c.get(Calendar.DAY_OF_WEEK));
        setDateOnEditText(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
        datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDateOnEditText(dayOfMonth,month,year);
            }
        };
        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(getActivity(),R.array.fm_update_meal_dropdown,android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(ar);
        final AlertDialog alertDialog=new AlertDialog.Builder(getActivity())
                .setView(fm_dialog)
                .create();
        MiscUtils.forceShowKeyboard(name,alertDialog);
        alertDialog.show();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),datePickerDialogListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                dpd.show();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(name.getText().toString().length()==0 || name.getText().toString().equals("Food Name"))
                {
                    Toast.makeText(getActivity(),"Enter details first!",Toast.LENGTH_SHORT).show();
                    return;
                }
                changeFoodMenuOnServer(alertDialog,fm_dialog);
                if(add_to_sl.isChecked()){
                    addToShoppingListSelectionDialog();
                }
                alertDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    private void addToShoppingListSelectionDialog() {
        final EditText listName =new EditText(getActivity());
        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setTitle("Enter Lists Name")
                .setView(listName)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new_list_title = listName.getText().toString();
                        final ShoppingList sl=new ShoppingList();
                        sl.setTitle(listName.getText().toString());
                        sl.setIs_done("N");
                        getNewListItemData(sl);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        ad.show();
    }

    private void getNewListItemData(final ShoppingList shoppingList) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_sl__dialog_list_data_entry,null);
        sli = null;
        newItemName = (EditText)v.findViewById(R.id.sl_list_new_item_name);
        newItemBackButton = (ImageButton)v.findViewById(R.id.sl_list_new_item_back);
        newItemSaveButton = (ImageButton)v.findViewById(R.id.sl_list_new_item_save);
        newItemType = (Spinner)v.findViewById(R.id.sl_list_new_item_type);
        newItemIsBought = (Switch)v.findViewById(R.id.sl_list_new_item_is_bought);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,getActivity().getResources().getStringArray(R.array.sl_new_item_type));
        newItemType.setAdapter(spinnerAdapter);
        final AlertDialog newItemDialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
        MiscUtils.forceShowKeyboard(newItemName,newItemDialog);
        newItemDialog.show();
        newItemSaveButton.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    newItemSaveButton.setEnabled(false);
                }
                else {
                    newItemSaveButton.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        newItemName.addTextChangedListener(watcher); // watcher to enable disable submit button
        newItemBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItemDialog.cancel();
            }
        });
        newItemSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sli=new ShoppingListItem();
                sli.setTitle(newItemName.getText().toString());
                sli.setId(-1);
                sli.setBought(newItemIsBought.isChecked()?"Y":"N");
                if(sli.isBought().equals("Y"))
                    sli.setBought_by_name(PrefManager.getSharedVal("username"));
                sli.setIs_dirty(1);
                sli.setType(newItemType.getSelectedItem().toString().toUpperCase());
                items_list.add(0,sli);
                //asking if user wants to add more items
                new AlertDialog.Builder(getActivity())
                        .setTitle("Want to add another item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                getNewListItemData(shoppingList);
                            }
                        })
                        .setNegativeButton("No! upload List", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                shoppingList.getItems().clear();
                                shoppingList.getItems().addAll(items_list);
                                items_list.clear();
                                dialog.cancel();
                                try {
                                    uploadNewItemsToServer(shoppingList);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .create().show();

                newItemDialog.cancel();
            }
        });
    }

    private void uploadNewItemsToServer(ShoppingList shoppingList) throws JSONException {
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                Snackbar.make((getActivity()).findViewById(android.R.id.content),"ShoppingList updated",
                        Snackbar.LENGTH_SHORT).show();
                //hitting API to connect new list to food menu
                shopping_list_update_token = ((JSONObject)obj).getJSONObject("DATA").getString("INSERTION_ID");
                attachNewListToFoodMenuEntry();

            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make((getActivity()).findViewById(android.R.id.content),"Server error occured",
                        Snackbar.LENGTH_SHORT).show();
            }
        };
        comManager.uploadShoppingList(getActivity(),callback,MiscUtils.shoppingListJSONParser(shoppingList));
    }

    private void attachNewListToFoodMenuEntry() {
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Server updated and list attached to food menu!!",Snackbar.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"list attachment failed!!",Snackbar.LENGTH_SHORT).show();
            }
        };
        comManager.attachListToFood(getActivity(),food_update_token, shopping_list_update_token,callback);
    }

    private void changeFoodMenuOnServer(final AlertDialog alertDialog,final View fm_dialog){
        uploadProgressBar.setVisibility(View.VISIBLE);
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                uploadProgressBar.setVisibility(View.GONE);
                if(add_to_sl.isChecked()){
                    food_update_token = ((JSONObject)obj).getJSONObject("DATA").getString("INSERTION_ID");
                }
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Server Updated!!",Snackbar.LENGTH_SHORT).show();
                alertDialog.cancel();
                getMenu();
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                uploadProgressBar.setVisibility(View.GONE);
                Snackbar.make(fm_dialog,"Server error occured!!",Snackbar.LENGTH_SHORT).show();
            }
        };
        try {
            //jugad for getting date in desired format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            Date tempDate = sdf1.parse(date.getText().toString());
            comManager.updateFoodMenu(getActivity(), name.getText().toString(), sdf.format(tempDate), day, sp.getSelectedItem().toString(), aSwitch.isChecked(), callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //utility function
    private void setDateOnEditText(int dayOfMonth, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        date.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        day = MiscUtils.getDayImageResource(c.get(Calendar.DAY_OF_WEEK));
    }
}
