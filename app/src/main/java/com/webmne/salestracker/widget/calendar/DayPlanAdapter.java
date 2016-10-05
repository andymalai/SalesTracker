package com.webmne.salestracker.widget.calendar;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 30-09-2016.
 */

class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.EventHolder> {

    private ArrayList<Plan> plans;
    private int MAX_COUNT=24;

    DayPlanAdapter(ArrayList<Plan> plan) {
        this.plans = plan;
        for(int i=plans.size();i<MAX_COUNT;i++){
            plans.add(new Plan());
        }

        Log.e("##POS>>>",""+plans.size());
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int pos) {

            for(int i=0;i<plans.size();i++){
                if(pos==plans.get(i).getPosition()){
                    Plan plan = plans.get(i);
                    holder.setEvent(plan);
                }else {
                    holder.subView.setVisibility(View.INVISIBLE);
                }
            }

        //try {
           // if (pos == plans.get(pos).getPosition()) {
             //   Plan plan = plans.get(pos);
              //  holder.setEvent(plan);
        //  }else{
         //       holder.setEvent(new Plan());

          //  }
      //  }catch (Exception e){
      //    holder.setEvent(new Plan());
     //  }


    }

    @Override
    public int getItemCount() {
        return MAX_COUNT;
    }

    void setDayPlan(ArrayList<Plan> plans) {
        this.plans = new ArrayList<>();
        this.plans = plans;
        notifyDataSetChanged();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        private TfTextView name, remark;
        private LinearLayout subView;

        private EventHolder(View itemView) {
            super(itemView);
            name = (TfTextView) itemView.findViewById(R.id.name);
            remark = (TfTextView) itemView.findViewById(R.id.remark);
            subView = (LinearLayout) itemView.findViewById(R.id.subView);
        }

        public void setEvent(Plan plan) {
            subView.setVisibility(View.VISIBLE);
            name.setText(plan.getAgentName());
            remark.setText(plan.getRemark());
            Log.e("pos", plan.getPosition() + "");
        }

        public void hide() {
            subView.setVisibility(View.INVISIBLE);
        }

    }
}
