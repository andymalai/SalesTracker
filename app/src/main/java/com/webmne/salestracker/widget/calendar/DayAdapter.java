package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webmne.salestracker.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by dhruvil on 23-08-2016.
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.MyViewHolder> {

    //Context
    private Context _ctx;
    // days
    private ArrayList<Date> days;
    // days with events
    private HashSet<Date> eventDays;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    private LinearLayout.LayoutParams paramsLinear;

    private String[] names = {"Sagar",
            "Dhruvil Patel",
            "Raghav Thakkar",
            "Priya Sindkar",
            "Masum Chauhan",
            "Chirag Brahmbhatt"};

    private String[] colors = {"#F48FB1",
            "#A5D6A7",
            "#64B5F6",
            "#FFB74D",
            "#A1887F",
            "#e57373",};

    public DayAdapter(Context _ctx, ArrayList<Date> days, HashSet<Date> eventDays) {
        this._ctx = _ctx;
        this.days = days;
        this.eventDays = eventDays;
        paramsLinear = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsLinear.leftMargin = 8;
        paramsLinear.rightMargin = 8;
        paramsLinear.topMargin = 8;
        paramsLinear.bottomMargin = 8;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_week, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // day in question
        Date date = days.get(position);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();

        // today
        Date today = new Date();

        // if this day has an event, specify event image
        holder.txtDate.setBackgroundResource(0);
        if (eventDays != null) {
            for (Date eventDate : eventDays) {

                if (eventDate.getDate() == day &&
                        eventDate.getMonth() == month &&
                        eventDate.getYear() == year) {

                    break;
                }
            }
        }

        // clear styling
        holder.txtDate.setTypeface(null, Typeface.NORMAL);
        holder.txtDate.setTextColor(Color.BLACK);

        //display only today
        if (day == today.getDate() && month == today.getMonth() && year == today.getYear()) {
            holder.txtDate.setTypeface(null, Typeface.BOLD);
            holder.txtDate.setTextColor(_ctx.getResources().getColor(R.color.today));
        }

        // for capitalize week names
        String[] capitalDays = {
                "", "SUN", "MON",
                "TUE", "WED", "THU",
                "FRI", "SAT"
        };
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("en", "US"));
        symbols.setShortWeekdays(capitalDays);
        // set text
        SimpleDateFormat sdf = new SimpleDateFormat("EEE\ndd", symbols);
        holder.txtDate.setText(sdf.format(date.getTime()));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate;
        public FlowLayout flowWeek;

        public MyViewHolder(View view) {
            super(view);

            txtDate = (TextView) view.findViewById(R.id.txtItemDayWeek);


        }


    }
}
