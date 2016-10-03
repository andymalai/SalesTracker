package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;


/**
 * Created by dhruvil on 23-08-2016.
 */
class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Context
    private Context _ctx;
    // days
    private ArrayList<TimeLineHour> days;

    public TimeLineAdapter(Context _ctx, ArrayList<TimeLineHour> days) {
        this._ctx = _ctx;
        this.days = days;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_week, parent, false);
        return new OtherViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
        // day in question
        TimeLineHour date = days.get(position);
        otherViewHolder.txtDate.setText(date.getTime());
        otherViewHolder.txtItemDayType.setText(date.getFormat());

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    private class OtherViewHolder extends RecyclerView.ViewHolder {

        private TfTextView txtDate, txtItemDayType;

        private OtherViewHolder(View view) {
            super(view);

            txtItemDayType = (TfTextView) view.findViewById(R.id.txtItemDayType);
            txtDate = (TfTextView) view.findViewById(R.id.txtItemDayWeek);
        }
    }

}
