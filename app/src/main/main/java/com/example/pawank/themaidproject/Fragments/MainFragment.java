package com.example.pawank.themaidproject.Fragments;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pawank.themaidproject.Adapters.MainAdapter;
import com.example.pawank.themaidproject.DataClass.ModuleNotification;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.Managers.NotificationEngine;
import com.example.pawank.themaidproject.Managers.SearchEngine;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.Services.FirebaseMainService;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    private RecyclerView mainRecyclerView;
    public MainAdapter adapter = null;
    private static String TAG="Main Fragment";
    private SearchView mSearchView;
    private boolean searchActive = false;
    private ImageButton searchBarBackButton;
    private Spinner searchBarSpinner;
    private EditText searchBarQuery = null;
    private ImageButton searchBarCancel;
    private MenuItem searchButton;
    private static MainActivity parent;
    private View searchBarView;
    private ArrayList<ModuleNotification> activitiesArray;
    private SearchEngine searchEngine;
    private Calendar cal;
    private LinearLayout data_view;
    private RelativeLayout no_data_view;
    private TextView recyclerViewHeading;


    public static MainFragment getInstance(MainActivity mToolbar){
        Log.d(TAG,"Main fragment");
        parent = mToolbar;
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        Log.d(TAG,"onCreateView()");
        setHasOptionsMenu(true);
        searchEngine = new SearchEngine(getActivity());
        cal = Calendar.getInstance();
        activitiesArray = new ArrayList<>();
        v = inflater.inflate(R.layout.fragment_main, null);
        mainRecyclerView = (RecyclerView) v.findViewById(R.id.activities_recycler_view);
        data_view =(LinearLayout) v.findViewById(R.id.main_data);
        recyclerViewHeading = (TextView) v.findViewById(R.id.main_recycler_view_heading);
        no_data_view =(RelativeLayout) v.findViewById(R.id.main_no_data);
        data_view.setVisibility(View.GONE);
        no_data_view.setVisibility(View.GONE);
        Log.d(TAG,"all activities"+FirebaseMainService.getAllActivities().size());
        if(FirebaseMainService.getAllActivities().size()!=0) {
            data_view.setVisibility(View.VISIBLE);
        }else{
            no_data_view.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mainRecyclerView!=null){
            if(adapter==null) {
                if(FirebaseMainService.getAllActivities()!=null && FirebaseMainService.getAllActivities().size()!=0)
                    activitiesArray.addAll(FirebaseMainService.getAllActivities());
                adapter = new MainAdapter(activitiesArray , getActivity(), MainFragment.this);
                mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mainRecyclerView.setAdapter(adapter);
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.main_menu,menu);
        searchButton = (MenuItem) menu.findItem(R.id.main_menu_search);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_search:
                searchActive = true;
                recyclerViewHeading.setText("Search Results");
                parent.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                parent.mToolbar.collapseActionView();
                searchButton.setVisible(false);
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT
                );
                parent.mToolbar.addView(getSearchBarView(),params);
                searchBarBackButton.setImageResource(R.drawable.ic_arrow_back_white);
                if(searchBarQuery!=null)
                    searchBarQuery.requestFocus();
                break;
        }
        return false;
    }

    private View getSearchBarView() {
        if(searchBarView==null){
            searchBarView = LayoutInflater.from(parent.getApplicationContext()).inflate(R.layout.layout_main_search_bar,null);

            searchBarBackButton = (ImageButton) searchBarView.findViewById(R.id.main_search_bar_back);
            searchBarSpinner = (Spinner)searchBarView.findViewById(R.id.main_search_bar_spinner);
            searchBarQuery = (EditText) searchBarView.findViewById(R.id.main_search_bar_query);
            searchBarCancel = (ImageButton) searchBarView.findViewById(R.id.main_search_bar_cancel);
            initSearchBar();
        }
        return searchBarView;
    }

    private void initSearchBar() {
        /*Spinner setting*/
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent,R.array.main_search,R.layout.layout_main_search_spinner_layout);
        searchBarSpinner.setAdapter(adapter);
        searchBarSpinner.setPadding(0,searchBarSpinner.getPaddingTop(),0,searchBarSpinner.getPaddingBottom());
        searchBarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchBarQuery.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*backButtonSetting*/
        searchBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.mToolbar.removeView(searchBarView);
                recyclerViewHeading.setText("Recent Activities");
                parent.getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE );
                //*- Action bar showed back button, this sync state function syncs home icon with drawer state*//
                parent.mToggleDrawer.syncState();
                searchButton.setVisible(true);
                searchActive = false;
                resetMainAdapter();

            }
        });

        /*cancel settings*/
        searchBarCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBarQuery.setText("");
            }
        });

        searchBarQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,searchBarSpinner.getSelectedItem().toString().toUpperCase());
                if(searchBarSpinner.getSelectedItem().toString().toUpperCase().equals("DATE")) {
                    MiscUtils.forceHideKeyboard(getActivity());
                    DatePickerDialog dpd = new DatePickerDialog(getActivity(),MainFragment.this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                    dpd.show();

                }

            }
        });

        /*Query function*/
        searchBarQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchBarSpinner.getSelectedItem().toString().toUpperCase().equals("MODULE")||searchBarSpinner.getSelectedItem().toString().toUpperCase().equals("CONTENT")) {
                    changeAdapter();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year,month,dayOfMonth);
        Date d = new Date(c.getTimeInMillis());
        searchBarQuery.setText(MiscUtils.getStringFromDateObject(d));
        changeAdapter();
    }

    public void changeAdapter(){
        activitiesArray.clear();
        activitiesArray.addAll(searchEngine.getQueryResult(searchBarQuery.getText().toString(), searchBarSpinner.getSelectedItem()));
        if(activitiesArray.size()==0) {
            if(no_data_view.getVisibility()==View.GONE)
                no_data_view.setVisibility(View.VISIBLE);
            if(data_view.getVisibility()==View.VISIBLE)
                data_view.setVisibility(View.GONE);
            activitiesArray.addAll(FirebaseMainService.getAllActivities());
        }
        else{
            if(data_view.getVisibility()==View.GONE)
                data_view.setVisibility(View.VISIBLE);
            no_data_view.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    public void resetMainAdapter(){
        activitiesArray.clear();
        activitiesArray.addAll(FirebaseMainService.getAllActivities());
        if(data_view.getVisibility()==View.GONE)
            data_view.setVisibility(View.VISIBLE);
        no_data_view.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

    }
}
