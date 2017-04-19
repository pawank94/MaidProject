package com.example.pawank.themaidproject.Decorators;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.example.pawank.themaidproject.DataClass.EmployeeAbsentDetails;
import com.example.pawank.themaidproject.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by pawan.k on 31-03-2017.
 */

public class EveningLeaveEventDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> daysToDecorate;
    private Context ctx;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public EveningLeaveEventDecorator(Context ctx, ArrayList<EmployeeAbsentDetails> currentCalendarConfig){
        this.ctx=ctx;
        daysToDecorate =new HashSet<>();
        try {
            for(EmployeeAbsentDetails as : currentCalendarConfig) {
                if(as.getShift().equals("EVENING") && !as.getBoth_shift()) {
                    daysToDecorate.add(CalendarDay.from(sdf.parse(as.getDate())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return daysToDecorate.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5,ctx.getResources().getColor(R.color.material_blue,null)));
        view.addSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.material_blue,null)));
    }
}
