package com.webmne.salestracker.visitplan.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 28-09-2016.
 */
public class CustomDialogVisitPlanAgentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<AgentListModel> agentModelList;

    public void setAgentList(ArrayList<AgentListModel> agentModelList) {
        this.agentModelList = new ArrayList<>();
        this.agentModelList = agentModelList;
        notifyDataSetChanged();
    }

    public CustomDialogVisitPlanAgentListAdapter(Context context, ArrayList<AgentListModel> agentModelList) {
        this.context = context;
        this.agentModelList = agentModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_agent_list, parent, false);
        return new AgentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AgentViewHolder agentViewHolder = (AgentViewHolder) holder;
        AgentListModel agentModel = agentModelList.get(position);
        agentViewHolder.setAgentDetails(agentModel);
    }

    @Override
    public int getItemCount() {
        return agentModelList.size();
    }

    class AgentViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtName, txtTier, txtTotalPlan, txtActualPlan, txtAdditionPlan;
        LinearLayout parentView;

        public AgentViewHolder(View itemView) {
            super(itemView);
            txtName = (TfTextView) itemView.findViewById(R.id.txtName);
            txtTier = (TfTextView) itemView.findViewById(R.id.txtTier);
            txtTotalPlan = (TfTextView) itemView.findViewById(R.id.txtTotalPlan);
            txtActualPlan = (TfTextView) itemView.findViewById(R.id.txtActualPlan);
            txtAdditionPlan = (TfTextView) itemView.findViewById(R.id.txtAdditionPlan);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);
        }

        public void setAgentDetails(final AgentListModel agentModel) {

            txtName.setText(String.format("%s", agentModel.getAgentName()));
            txtTier.setText(String.format("%s", agentModel.getAgentTier()));
            txtTotalPlan.setText(String.format("%s", agentModel.getTotalPlan()));
            txtActualPlan.setText(String.format("%s", agentModel.getActualPlan()));
            txtAdditionPlan.setText(String.format("%s", agentModel.getAdditionPlan()));

            if (agentModel.isChecked()) {
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            } else {
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }

}
