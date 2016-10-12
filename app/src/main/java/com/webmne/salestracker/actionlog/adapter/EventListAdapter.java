package com.webmne.salestracker.actionlog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.event.model.EventModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventHolder> {

    private Context context;
    private ArrayList<EventModel> eventList;
    private onClickListener onClickListener;

    public EventListAdapter(Context context, ArrayList<EventModel> eventList, onClickListener onClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.onClickListener = onClickListener;
    }

    public void setEventList(ArrayList<EventModel> eventList) {
        this.eventList = new ArrayList<>();
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_event_list, parent, false);

        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        EventModel model = eventList.get(position);
        holder.setEvent(model, position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        LinearLayout dateLayout,parentView;
        TfTextView txtMonth,txtDate,txtName,txtDesc;

        public EventHolder(View itemView) {
            super(itemView);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
            txtDate = (TfTextView) itemView.findViewById(R.id.txtDate);
            txtMonth = (TfTextView) itemView.findViewById(R.id.txtMonth);
            txtName = (TfTextView) itemView.findViewById(R.id.txtName);
            txtDesc = (TfTextView) itemView.findViewById(R.id.txtDesc);
        }

        public void setEvent(EventModel model, final int position) {

            txtName.setText(String.format("%s", model.getName()));

            txtDesc.setText(String.format("%s", model.getDesc()));

            // Created Date-Time
            txtMonth.setText(Functions.parseDate(model.getCreateDate(), "MMM"));
            txtDate.setText(Functions.parseDate(model.getCreateDate(), "dd"));


            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickListener.onClick(position);

                }
            });

        }
    }

    public interface onClickListener {
        void onClick(int position);
    }

}
