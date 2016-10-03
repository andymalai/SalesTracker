package com.webmne.salestracker.widget.calendar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 30-09-2016.
 */

class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.EventHolder> {

    private ArrayList<Event> events;

    DayPlanAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event event = events.get(position);
        holder.setEvent(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        private TfTextView name, remark;

        private EventHolder(View itemView) {
            super(itemView);
            name = (TfTextView) itemView.findViewById(R.id.name);
            remark = (TfTextView) itemView.findViewById(R.id.remark);
        }

        public void setEvent(Event event) {
            name.setText(event.getName());
            remark.setText(event.getRemark());
        }
    }
}
