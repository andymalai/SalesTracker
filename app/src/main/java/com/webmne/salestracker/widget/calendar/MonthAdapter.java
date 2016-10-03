package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


/**
 * Created by dhruvil on 23-08-2016.
 */
class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MyViewHolder> {

    //Context
    private Context _ctx;
    // days
    private ArrayList<Date> days;
    // days with events
    private HashSet<Date> eventDays;

    void setOnDateSelectListener(MonthAdapter.onDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

    private onDateSelectListener onDateSelectListener;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    MonthAdapter(Context _ctx, ArrayList<Date> days, HashSet<Date> eventDays) {
        this._ctx = _ctx;
        this.days = days;
        this.eventDays = eventDays;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_month, parent, false);
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
                    // mark this day for event
                    holder.txtDate.setBackgroundResource(R.drawable.reminder);
                    break;
                }
            }
        }

        // clear styling
        holder.txtDate.setTextColor(Color.BLACK);

        //display grey if not in selected month
        if (month != currentDate.get(Calendar.MONTH)) {
            holder.txtDate.setTextColor(_ctx.getResources().getColor(R.color.greyed_out));
        }

        //display only today
        if (day == today.getDate() && month == today.getMonth() && year == today.getYear()) {
            holder.txtDate.setTypeface(null, Typeface.BOLD);
            holder.txtDate.setTextColor(_ctx.getResources().getColor(R.color.today));
        }

        holder.txtDate.setText(String.valueOf(date.getDate()));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TfTextView txtDate;

        MyViewHolder(View view) {
            super(view);
            txtDate = (TfTextView) view.findViewById(R.id.txtItemDayMonth);
            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calendar calendar = Calendar.getInstance();
                    //calendar.setTime(currentDate.getTime());
                    //calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(((TextView) v).getText().toString()));
                    if (days.get(getPosition()).getMonth() != currentDate.get(Calendar.MONTH)) {

                    } else {

                        Calendar tempCal = (Calendar) currentDate.clone();
                        tempCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(((TfTextView) v).getText().toString()));
                        if (onDateSelectListener != null) {
                            onDateSelectListener.onDateSelect(tempCal);
                        }
                    }

                }
            });
        }
    }

    interface onDateSelectListener {
        void onDateSelect(Calendar currentDate);
    }
}
