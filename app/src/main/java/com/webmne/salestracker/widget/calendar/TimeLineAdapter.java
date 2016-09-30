package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webmne.salestracker.R;

import java.util.ArrayList;


/**
 * Created by dhruvil on 23-08-2016.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Context
    private Context _ctx;
    // days
    private ArrayList<String> days;

    public TimeLineAdapter(Context _ctx, ArrayList<String> days) {
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
        String date = days.get(position);
        otherViewHolder.txtDate.setText(date);

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate;

        public OtherViewHolder(View view) {
            super(view);

            txtDate = (TextView) view.findViewById(R.id.txtItemDayWeek);
        }
    }

}
