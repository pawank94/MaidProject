package com.example.pawank.themaidproject.Decorators;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

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
 * Created by pawan.k on 05-04-2017.
 */

public class BothShiftDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> daysToDecorate;
    private Context ctx;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public BothShiftDecorator(Context ctx, ArrayList<EmployeeAbsentDetails> currentCalendarConfig){
        this.ctx=ctx;
        daysToDecorate =new HashSet<>();
        try {
            for(EmployeeAbsentDetails as : currentCalendarConfig) {
                //Both Shift Decorators
                if(as.getBoth_shift())
                    daysToDecorate.add(CalendarDay.from(sdf.parse(as.getDate())));
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
        view.addSpan(new UnderlineSpan());
        view.addSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.material_violet,null)));
    }
}
