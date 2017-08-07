package com.example.pawank.themaidproject.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawank.themaidproject.DataClass.EmployeeAbsentDetails;
import com.example.pawank.themaidproject.DataClass.Employees;
import com.example.pawank.themaidproject.Decorators.BothShiftDecorator;
import com.example.pawank.themaidproject.Decorators.EveningLeaveEventDecorator;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.Decorators.MorningLeaveEventDecorator;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.SQLManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.pawank.themaidproject.utils.MiscUtils.getDayImageResource;

public class AttendenceFragment extends Fragment {


    private SQLManager sqlManager;
    private ComManager comManager;
    private ProgressBar parentProgressBar;
    private String sqlTable = "employeeTable";
    private String TAG = "Attendence Fragment";
    private String data;
    private MaterialCalendarView calendarView;
    private FloatingActionButton addFab;
    private LinearLayout noDataLayout;
    private LinearLayout dataDate;
    private Menu menuInstance;
    private boolean multiSelectMode = false;
    private ArrayList<String> entityArray;
    private ViewPager pager;
    private AttendencePagerAdapter vPagerAdapter = null;
    private ArrayList<Employees> empArray;
    private ArrayList<EmployeeAbsentDetails> currentCalendarConfig = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<CalendarDay, EmployeeAbsentDetails> dateHash;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;
    private TextView bsShift;
    private TextView bsReason;
    private TextView bsReportedBy;
    private FloatingActionButton deleteBsfab;
    private CoordinatorLayout coordinatorView;
    private ProgressBar progressBar;
    private Handler mHandler;
    private ImageButton back;
    private ImageButton save;
    private EditText date;
    private EditText reason;
    private Spinner shift;
    private String day;
    private boolean dateFirstTime=true;
    private DatePickerDialog.OnDateSetListener datePickerDialogListener;
    private AlertDialog ad;
    private TextView bsEmp;
    private Calendar cal;
    private List<CalendarDay> multimodeDates;

    public static AttendenceFragment newInstance()
    {
        AttendenceFragment af = new AttendenceFragment();
        return af;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sqlManager = new SQLManager(getActivity().getApplicationContext());
        comManager = new ComManager(getActivity().getApplicationContext());
        mHandler = new Handler(Looper.getMainLooper());
        entityArray = new ArrayList<String>();
        dateHash = new HashMap<>();
        cal = Calendar.getInstance();
        entityArray.add("Maid");
        entityArray.add("safai");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendence,null);
        parentProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarFragmentContainer);
        calendarView = (MaterialCalendarView) v.findViewById(R.id.at_calendar_view);
        addFab =(FloatingActionButton) v.findViewById(R.id.at_add_fab);
        pager = (ViewPager) v.findViewById(R.id.at_pager);
        bsShift = (TextView) v.findViewById(R.id.at_bottom_sheet_shift);
        bsReason = (TextView) v.findViewById(R.id.at_bottom_sheet_reason);
        bsReportedBy = (TextView) v.findViewById(R.id.at_bottom_sheet_reported_by);
        bsEmp = (TextView) v.findViewById(R.id.at_bottom_sheet_employee_name);
        deleteBsfab = (FloatingActionButton) v.findViewById(R.id.at_bottom_sheet_delete_fab);
        coordinatorView = (CoordinatorLayout) v.findViewById(R.id.at_coordinator_view);
        progressBar = (ProgressBar) v.findViewById(R.id.at_emp_card_progress_bar);
        bottomSheet = v.findViewById(R.id.at_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        return v;
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
                        getEmployees();
                        if((data=sqlManager.getToken(sqlTable))==null)
                        {
                            //if this page is being opened first time, populate sql table
                            MiscUtils.logE(TAG,"token null");
                        }
                        else{
                            try {
                                empArray = Employees.populateEmployeeArray(new JSONObject(sqlManager.getData(sqlTable,"json")));
                                //initial Display configuration
                                getEmployeeAbsentData(0,empArray.get(0).getEid(),Calendar.getInstance().get(Calendar.MONTH)+1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Snackbar.make(getActivity().findViewById(R.id.drawer_layout),"Syncing with server",Snackbar.LENGTH_SHORT).show();
                        }
                        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                        initListeners();
                    }
                });
            }
        }).start();
    }

    //initialize all Listeners
    private void initListeners() {
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, final CalendarDay date) {
                //too much processing , applying thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                int currentEmployeeIndex = pager.getCurrentItem();
                                Log.d(TAG,"getting info for emp index:"+currentEmployeeIndex+" for month:"+date.getMonth()+1);
                                //getting info on basis of pager index and month selected
                                getEmployeeAbsentData(currentEmployeeIndex, empArray.get(currentEmployeeIndex).getEid(),date.getMonth()+1);
                            }
                        },500);
                    }
                }).start();
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    if (pager.getCurrentItem() < empArray.size()) {
                        int currentEmployeeIndex = pager.getCurrentItem();
                        Log.d(TAG, "getting info for emp index:" + currentEmployeeIndex + " for month:" + calendarView.getCurrentDate().getMonth() + 1);
                        //getting info on basis of pager index and month selected
                        getEmployeeAbsentData(currentEmployeeIndex, empArray.get(currentEmployeeIndex).getEid(), calendarView.getCurrentDate().getMonth() + 1);
                    } else {
                    }
                }
            }
        });
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if(selected && !multiSelectMode){
                    if(dateHash.containsKey(date)){
                        EmployeeAbsentDetails workObject = dateHash.get(date);
                        bsEmp.setText(empArray.get(pager.getCurrentItem()).getName());
                        if(workObject.getShift()!=null && !workObject.getShift().equals("null") && workObject.getShift().length()!=0)
                            if(workObject.getBoth_shift())
                                bsShift.setText("Morning,Evening");
                            else
                                bsShift.setText(workObject.getShift().substring(0,1).toUpperCase()+workObject.getShift().substring(1).toLowerCase());
                        else
                            bsShift.setText("N/A");
                        if(workObject.getReason()!=null && !workObject.getReason().equals("null") && workObject.getReason().length()!=0)
                            bsReason.setText(workObject.getReason());
                        else
                            bsReason.setText("N/A");
                        if(workObject.getReported_by()!=null && !workObject.getReported_by().equals("null") && workObject.getReported_by().length()!=0)
                            bsReportedBy.setText(workObject.getReported_by());
                        else
                            bsReportedBy.setText("N/A");
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        deleteBsfab.setVisibility(View.VISIBLE);
                        deleteBsfab.setTag(workObject);
                    }
                    else{
                        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        }
                    }
                }
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_DRAGGING || newState==BottomSheetBehavior.STATE_COLLAPSED)
                    deleteBsfab.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReportAbsentDialog();
            }
        });
        deleteBsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Callback callback = new Callback() {
                    @Override
                    public void onSuccess(Object obj) throws JSONException, ParseException {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Updated on Server", Snackbar.LENGTH_SHORT).show();
                        getEmployeeAbsentData(pager.getCurrentItem(), empArray.get(pager.getCurrentItem()).getEid(),calendarView.getCurrentDate().getMonth()+1);
                    }

                    @Override
                    public void onFailure(Object obj) throws JSONException {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error Occured", Snackbar.LENGTH_SHORT).show();
                    }
                };
               final AlertDialog ad = new AlertDialog.Builder(getActivity())
                                    .setTitle("Confirmation")
                                    .setMessage("Are you sure you want to delete this entry?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            EmployeeAbsentDetails workObject = (EmployeeAbsentDetails) deleteBsfab.getTag();
                                            comManager.removeAbsentEntries(getActivity().getApplicationContext(),workObject.getId(),callback);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .create();
                ad.show();
            }
        });
    }

    private void createReportAbsentDialog() {
        dateFirstTime=true;
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_at__dialog_report_absent,null);
        back = (ImageButton) v.findViewById(R.id.at_absent_dialog_back);
        save = (ImageButton) v.findViewById(R.id.at_absent_dialog_save);
        date = (EditText) v.findViewById(R.id.at_absent_dialog_date);
        reason = (EditText) v.findViewById(R.id.at_absent_dialog_reason);
        shift = (Spinner) v.findViewById(R.id.at_absent_dialog_shift);
        //*******date in dialog********************//
        if(multiSelectMode){
            date.setText("Multiple Dates Selected");
            date.setEnabled(false);
        }
        else {
            date.setEnabled(true);
            day = getDayImageResource(cal.get(Calendar.DAY_OF_WEEK));
            CalendarDay cd = calendarView.getSelectedDate();
            if (cd != null)
                setDateOnEditText(cd.getDay(), cd.getMonth() + 1, cd.getYear());
            else
                setDateOnEditText(cal.get(cal.DAY_OF_MONTH), cal.get(cal.MONTH) + 1, cal.get(cal.YEAR));
            datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    setDateOnEditText(dayOfMonth, month, year);
                }
            };
        }
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiSelectMode)
                    return;
                CalendarDay cd = calendarView.getSelectedDate();
                DatePickerDialog dpd;
                if (cd != null)
                    dpd = new DatePickerDialog(getActivity(), datePickerDialogListener, cd.getYear(), cd.getMonth(), cd.getDay());
                else
                    dpd = new DatePickerDialog(getActivity(), datePickerDialogListener, cal.get(cal.YEAR), cal.get(cal.MONTH) + 1, cal.get(cal.DAY_OF_MONTH));
                dpd.show();
            }
        });
        /*******************************************************************/
        //will update and put varients
        shift.setAdapter(ArrayAdapter.createFromResource(getActivity(),R.array.at_report_absent_dropdown,android.R.layout.simple_spinner_dropdown_item));
        //*Listeners*//
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date tempDate=null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM, yyyy");
                    if(!multiSelectMode)
                        tempDate = sdf1.parse(date.getText().toString());
                    Callback callback = new Callback() {
                        @Override
                        public void onSuccess(Object obj) throws JSONException, ParseException {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Updated on Server", Snackbar.LENGTH_SHORT).show();
                            getEmployeeAbsentData(pager.getCurrentItem(), empArray.get(pager.getCurrentItem()).getEid(),calendarView.getCurrentDate().getMonth()+1);
                        }

                        @Override
                        public void onFailure(Object obj) throws JSONException {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error Occured", Snackbar.LENGTH_SHORT).show();
                        }
                    };
                    if(!multiSelectMode)
                        comManager.reportAbsent(getActivity().getApplicationContext(), sdf.format(tempDate), shift.getSelectedItem().toString(), empArray.get(pager.getCurrentItem()).getEid(), reason.getText().toString(), callback);
                    else {
                        multimodeDates = calendarView.getSelectedDates();
                        Date first  = multimodeDates.get(0).getDate();
                        //to increment day from one day.. bud in API
                        Calendar c = Calendar.getInstance();
                        c.setTime(multimodeDates.get(multimodeDates.size()-1).getDate());
                        c.add(Calendar.DATE,1);
                        Date last = c.getTime();
                        ///////////////////////////////////
                        Log.d(first.toString()+" ",last.toString());
                        comManager.reportAbsentRange(getActivity().getApplicationContext(),sdf.format(first),shift.getSelectedItem().toString().toUpperCase(),sdf.format(last),shift.getSelectedItem().toString().toUpperCase(),empArray.get(pager.getCurrentItem()).getEid(), reason.getText().toString(),callback);
                    }
                    ad.hide();
                    MiscUtils.forceHideKeyboard(getActivity());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        /******************************************************************/
        ad = new AlertDialog.Builder(getActivity())
                            .setView(v).create();
        MiscUtils.forceShowKeyboard(reason,ad);
        ad.show();
    }

    private void getEmployees() {
        //setAllEmployees
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                if(progressBar.getVisibility()==View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                empArray = Employees.populateEmployeeArray(obj);
                prepareDataOnView(new JSONObject());
                //initial Display configuratio
                getEmployeeAbsentData(0, empArray.get(0).getEid(),Calendar.getInstance().get(Calendar.MONTH)+1);
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Server Error Occured",Snackbar.LENGTH_LONG)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getEmployees();
                            }
                        }).show();
            }
        };
        comManager.getAllEmployeeDetails(getActivity().getApplicationContext(),callback);
        /*********************************************/
        //change afterwards put prepare data in view inside
    }

    private void getEmployeeAbsentData(final int index, String eid, int month) {
        final Callback attendenceDataCallback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                currentCalendarConfig = EmployeeAbsentDetails.populateAbsentDaysArray((JSONObject)obj);
                //update data in emp array
                empArray.get(index).setAbsentDays(currentCalendarConfig.size()+"");
                dateHash = updateDateHash(currentCalendarConfig);
                if(currentCalendarConfig!=null)
                    changeCalendarView(currentCalendarConfig);
                if(vPagerAdapter!=null)
                    vPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                JSONObject jObj = (JSONObject)obj;
                calendarView.removeDecorators();
                vPagerAdapter.notifyDataSetChanged();
                if(jObj.getString("STATUS").equals("FALSE"))
                {
                    empArray.get(index).setAbsentDays("-1");
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "No leaves found!", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error Occured", Snackbar.LENGTH_LONG)
                            .setAction("Try Again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getEmployees();
                                }
                            }).show();
                }
                vPagerAdapter.notifyDataSetChanged();
            }
        };
        comManager.getAbsentDaysForEmployee(getActivity().getApplicationContext(),eid, month,attendenceDataCallback);
    }

    private HashMap<CalendarDay,EmployeeAbsentDetails> updateDateHash(ArrayList<EmployeeAbsentDetails> currentCalendarConfig) throws ParseException {
        HashMap<CalendarDay,EmployeeAbsentDetails> mp = new HashMap<>();
        for(EmployeeAbsentDetails abd : currentCalendarConfig){
            if(!mp.containsKey(CalendarDay.from(sdf.parse(abd.getDate()))))
                mp.put(CalendarDay.from(sdf.parse(abd.getDate())),abd);
            else{
                abd.setBoth_shift(true);
                mp.get(CalendarDay.from(sdf.parse(abd.getDate()))).setBoth_shift(true);
            }
        }
        return mp;
    }

    private void prepareDataOnView(JSONObject json) {
        if(json!=null)
        {
            MiscUtils.logD(TAG,"preparing view");
            if(currentCalendarConfig!=null)
                changeCalendarView(currentCalendarConfig);
            if(parentProgressBar.getVisibility()==View.VISIBLE)
                parentProgressBar.setVisibility(View.GONE);
            if(vPagerAdapter==null)
                vPagerAdapter = new AttendencePagerAdapter(getChildFragmentManager());
            if(pager.getAdapter()==null) {
                pager.setAdapter(vPagerAdapter);
                pager.setCurrentItem(0);
            }
            else
                vPagerAdapter.notifyDataSetChanged();

        }
        else
        {
            MiscUtils.logE(TAG,"no data found in SQL table and server error");
        }
    }

    private void changeCalendarView(ArrayList<EmployeeAbsentDetails> currentCalendarConfig) {
        calendarView.removeDecorators();
        MorningLeaveEventDecorator morningDecorator = new MorningLeaveEventDecorator(getActivity().getApplicationContext(),currentCalendarConfig);
        EveningLeaveEventDecorator eveningDecorator = new EveningLeaveEventDecorator(getActivity().getApplicationContext(),currentCalendarConfig);
        BothShiftDecorator bothShiftDecorator = new BothShiftDecorator(getActivity().getApplicationContext(),currentCalendarConfig);
        calendarView.addDecorator(morningDecorator);
        calendarView.addDecorator(eveningDecorator);
        calendarView.addDecorator(bothShiftDecorator);
    }

    //utility function
    private void setDateOnEditText(int dayOfMonth, int month, int year) {
        Calendar c=Calendar.getInstance();
        c.set(year,month,dayOfMonth);
        day = getDayImageResource(c.get(Calendar.DAY_OF_WEEK));
        if(!dateFirstTime)
            date.setText(MiscUtils.getNewDateFormatLongMonth1(dayOfMonth+"/"+(month+1)+"/"+year));
        else {
            date.setText(MiscUtils.getNewDateFormatLongMonth1(dayOfMonth + "/" + month + "/" + year));
            dateFirstTime=false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.at_menu,menu);
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.at_menu_multiple_selection).setVisible(false);
        menu.findItem(R.id.at_menu_single_selection).setVisible(false);
        if(multiSelectMode)
        {
            menu.findItem(R.id.at_menu_multiple_selection).setVisible(false);
            menu.findItem(R.id.at_menu_single_selection).setVisible(true);
        }
        else{
            menu.findItem(R.id.at_menu_multiple_selection).setVisible(true);
            menu.findItem(R.id.at_menu_single_selection).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.at_menu_multiple_selection:
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                Toast.makeText(getActivity().getApplicationContext(),"Multiple Selection Mode On",Toast.LENGTH_SHORT).show();
                multiSelectMode = true;
                getActivity().invalidateOptionsMenu();
                break;
            case R.id.at_menu_single_selection:
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                multiSelectMode = false;
                Toast.makeText(getActivity().getApplicationContext(),"Single Selection Mode On",Toast.LENGTH_SHORT).show();
                getActivity().invalidateOptionsMenu();
                break;
            case R.id.at_menu_refresh:
                if(empArray!=null && empArray.size()>0)
                    getEmployeeAbsentData(pager.getCurrentItem(), empArray.get(pager.getCurrentItem()).getEid(),calendarView.getCurrentDate().getMonth()+1);
                else{
                    Toast.makeText(getActivity(),"No employees or Server Error!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }
    /*Class for View Pager Adapter'*/
    public class AttendencePagerAdapter extends FragmentStatePagerAdapter {

        public AttendencePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AttendenceSliderSupportFragment.newInstance(empArray,position);
        }

        @Override
        public int getItemPosition(Object object) {
            //*-important to load all views again after notifydatasetchanged. This jugaad invalidates and reload all views
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return empArray.size();
        }

    }

}
