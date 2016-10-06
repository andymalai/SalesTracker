package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.visitplan.CustomDialogAddVisitPlan;
import com.webmne.salestracker.visitplan.CustomDialogAddVisitPlanCallBack;
import com.webmne.salestracker.widget.TfTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sagartahelyani on 30-09-2016.
 */

class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.EventHolder> {

    private ArrayList<Plan> plans;
    private int MAX_COUNT = 24;
    ArrayList<TimeLineHour> timeLineHours;
    private Context context;
    private Calendar currentDate;

    DayPlanAdapter(Context context, ArrayList<Plan> plan, ArrayList<TimeLineHour> timeLineHours, Calendar currentDate) {
        this.plans = plan;
        this.timeLineHours = timeLineHours;
        this.context = context;
        this.currentDate = currentDate;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int pos) {
        //  Log.e("simpleAMPM",timeLineHours.get(pos).getTime());
        holder.subView.setVisibility(View.GONE);

        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getPosition() == pos) {
                holder.subView.setVisibility(View.VISIBLE);
                Plan plan = plans.get(i);
                holder.setEvent(plan);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return timeLineHours.size();
    }

    void setDayPlan(ArrayList<Plan> plans) {
        this.plans = new ArrayList<>();
        this.plans = plans;
        notifyDataSetChanged();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        private TfTextView name, remark;
        private LinearLayout subView;
        private RelativeLayout parentView;

        private EventHolder(View itemView) {
            super(itemView);
            name = (TfTextView) itemView.findViewById(R.id.name);
            remark = (TfTextView) itemView.findViewById(R.id.remark);
            subView = (LinearLayout) itemView.findViewById(R.id.subView);
            parentView = (RelativeLayout) itemView.findViewById(R.id.parentView);

            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parentView.getChildAt(0).getVisibility() == View.VISIBLE) {

                    } else {
                        new CustomDialogAddVisitPlan(new MaterialDialog.Builder(context), context, new CustomDialogAddVisitPlanCallBack() {
                            @Override
                            public void addCallBack(JSONObject json) {
                                Log.e("json", json.toString());

                            }
                        });
                    }
                }
            });
        }

        public void setEvent(Plan plan) {
            subView.setVisibility(View.VISIBLE);
            name.setText(plan.getAgentName());
            remark.setText(plan.getRemark());
            Log.e("pos", plan.getPosition() + "");
        }
    }
}
