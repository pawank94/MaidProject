package com.example.pawank.themaidproject.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.Employees;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.util.ArrayList;

/**
 * Created by pawan.k on 29-03-2017.
 */

public class AttendenceSliderSupportFragment extends Fragment {
    private TextView t;
    private int position;
    private ArrayList<Employees> empArray;
    private String entityname;
    private static AttendenceSliderSupportFragment assf = new AttendenceSliderSupportFragment();
    private TextView name, joining_date,mobile,absent_days;
    private ImageView image;

    public AttendenceSliderSupportFragment(){
    }

    public static Fragment newInstance(ArrayList<Employees> empArray, int position) {
        Bundle args = new Bundle();
        args.putInt("entity",position);
        AttendenceSliderSupportFragment frg = new AttendenceSliderSupportFragment();
        frg.empArray = empArray;
        frg.setArguments(args);
        return frg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("entity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v =(ViewGroup) inflater.inflate(R.layout.layout_at_pager_employee_card,container,false);
        name = (TextView) v.findViewById(R.id.at_emp_card_name);
        mobile = (TextView) v.findViewById(R.id.at_emp_card_mobile);
        joining_date = (TextView) v.findViewById(R.id.at_emp_card_joining_date);
        absent_days = (TextView) v.findViewById(R.id.at_emp_card_absent_days);
        image = (ImageView) v.findViewById(R.id.at_emp_card_image);
        /*set values on card*/
        if(empArray!=null) {
            name.setText(empArray.get(position).getName());
            mobile.setText(empArray.get(position).getMobile());
            joining_date.setText(MiscUtils.getNewDateFormatLongMonth(empArray.get(position).getJoining_date()));
            if (empArray.get(position).getAbsentDays() != null)
                absent_days.setText("No. of Absent shifts: \n"+((empArray.get(position).getAbsentDays().equals("-1")) ? "N/A" : Float.parseFloat(empArray.get(position).getAbsentDays())+""));
            if(empArray.get(position).getType().equals("COOK")) {
                image.setImageResource(R.drawable.ic_chef);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    image.setBackgroundColor(getActivity().getResources().getColor(R.color.ic_chef_background,null));
                }
            }
            else {
                image.setImageResource(R.drawable.ic_maid);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    image.setBackgroundColor(getActivity().getResources().getColor(R.color.ic_maid_background, null));
                }
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////
        return v;
    }
}
