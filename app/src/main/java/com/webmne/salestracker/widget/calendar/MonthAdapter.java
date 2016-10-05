package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.DatePlan;
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

    private ArrayList<DatePlan> plans;

    Calendar currentCalendar;

    // days with events
    private HashSet<Date> eventDays;

    void setOnDateSelectListener(MonthAdapter.onDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

    private onDateSelectListener onDateSelectListener;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    MonthAdapter(Context _ctx, ArrayList<Date> days, HashSet<Date> eventDays, Calendar currentCalendar) {
        this._ctx = _ctx;
        this.days = days;
        this.eventDays = eventDays;
        this.currentCalendar = currentCalendar;
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        // today
        Calendar todayCalendar = Calendar.getInstance();

        if (plans != null) {

            for (DatePlan eventDate : plans) {

                if (eventDate == null || eventDate.getDate() == null) {

                } else {
                    String[] fullDate = eventDate.getDate().split("-");

                    int y = Integer.parseInt(fullDate[0]);
                    int m = Integer.parseInt(fullDate[1]);
                    int d = Integer.parseInt(fullDate[2]);

                    if (d == day
                            && m == month
                            && y == year) {
                        // mark this day for event
                        holder.markView.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        // clear styling
        holder.txtDate.setTextColor(Color.BLACK);

        //display grey if not in selected month
        if (month != currentCalendar.get(Calendar.MONTH) + 1) {
            holder.txtDate.setTextColor(_ctx.getResources().getColor(R.color.greyed_out));
        }

        //display only today
        if (day == todayCalendar.get(Calendar.DAY_OF_MONTH)
                && month == todayCalendar.get(Calendar.MONTH) + 1
                && year == todayCalendar.get(Calendar.YEAR)) {
            holder.txtDate.setBold(true);
            holder.txtDate.setTextColor(ContextCompat.getColor(_ctx, R.color.today));
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

    public void setPlans(ArrayList<DatePlan> plans) {
        this.plans = new ArrayList<>();
        this.plans = plans;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TfTextView txtDate;
        View markView;

        MyViewHolder(View view) {
            super(view);
            txtDate = (TfTextView) view.findViewById(R.id.txtItemDayMonth);
            markView = view.findViewById(R.id.markView);

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
