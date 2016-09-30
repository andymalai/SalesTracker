package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dhruvil on 24-08-2016.
 */
public class TimeLineView extends LinearLayout {


    public TimeLineView(Context context) {
        super(context);
        init(context);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        ArrayList<String> hours = getTimeLineHours();

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (String str : hours) {

            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setText(str);
            addView(txt,100, 200);
        }

    }

    public static ArrayList<String> getTimeLineHours() {

        // temp set calender...
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 0, 0, 0);

        // initial array to hold 24
        ArrayList<String> hours = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            Date date = calendar.getTime();
            SimpleDateFormat sdf_day = new SimpleDateFormat("hh a");
            hours.add(sdf_day.format(date));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        return hours;

    }

}
