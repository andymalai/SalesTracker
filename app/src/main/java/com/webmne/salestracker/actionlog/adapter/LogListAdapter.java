package com.webmne.salestracker.actionlog.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.LogHolder> {

    private Context context;
    private ArrayList<ActionLogModel> actionList;

    public LogListAdapter(Context context, ArrayList<ActionLogModel> actionList) {
        this.context = context;
        this.actionList = actionList;
    }

    public void setActionList(ArrayList<ActionLogModel> actionList) {
        this.actionList = new ArrayList<>();
        this.actionList = actionList;
        notifyDataSetChanged();
    }

    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_action_log, parent, false);

        return new LogHolder(view);
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {
        ActionLogModel model = actionList.get(position);
        holder.setActionLog(model);
    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }

    class LogHolder extends RecyclerView.ViewHolder {

        LinearLayout dateLayout;
        TfTextView txtDate, txtMonth, txtYear, txtStatus, txtDescription, txtAgentName, txtLastUpdate;
        private View viewIndicate;

        public LogHolder(View itemView) {
            super(itemView);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            txtDate = (TfTextView) itemView.findViewById(R.id.txtDate);
            txtMonth = (TfTextView) itemView.findViewById(R.id.txtMonth);
            txtYear = (TfTextView) itemView.findViewById(R.id.txtYear);
            txtStatus = (TfTextView) itemView.findViewById(R.id.txtStatus);
            txtDescription = (TfTextView) itemView.findViewById(R.id.txtDescription);
            txtAgentName = (TfTextView) itemView.findViewById(R.id.txtAgentName);
            txtLastUpdate = (TfTextView) itemView.findViewById(R.id.txtLastUpdate);
            viewIndicate = itemView.findViewById(R.id.viewIndicate);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setActionLog(ActionLogModel model) {
            txtAgentName.setText(String.format("%s", model.getAgentName()));

            if (model.isCompleted()) {
                txtStatus.setText("Completed");
                viewIndicate.setBackground(ContextCompat.getDrawable(context, R.drawable.completed_shape));
            } else {
                txtStatus.setText("Pending");
                viewIndicate.setBackground(ContextCompat.getDrawable(context, R.drawable.pending_shape));
            }


        }
    }
}
