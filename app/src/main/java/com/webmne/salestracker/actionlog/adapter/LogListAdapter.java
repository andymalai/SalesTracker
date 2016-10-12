package com.webmne.salestracker.actionlog.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.RemarkListActivity;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
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
        TfTextView txtDate, txtMonth, txtYear, txtStatus, txtDescription, txtAgentName, txtLastUpdate, txtRemark;

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
            txtRemark = (TfTextView) itemView.findViewById(R.id.txtRemark);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void setActionLog(final ActionLogModel model) {

            if(model.getAgentName().equals(null))
            {
                txtAgentName.setText(String.format("%s", context.getString(R.string.deleted_agent)));
            }
            else
            {
                txtAgentName.setText(String.format("%s", model.getAgentName()));
            }

            txtDescription.setText(String.format("%s", model.getDescription()));

            txtStatus.setText(Functions.getStatus(context, model.getStatus()));

            // Created Date-Time
            txtMonth.setText(Functions.parseDate(model.getCreatedDatetime(), "MMM"));
            txtDate.setText(Functions.parseDate(model.getCreatedDatetime(), "dd"));

            // Last Update Date-Time Functions.parseDate(model.getCreatedDatetime(), "dd")
            txtLastUpdate.setText(String.format("%s %s", context.getString(R.string.last_update), Functions.parseDate(model.getUpdatedDatetime(), "dd MMM, yyyy hh:mm a")));

            if (TextUtils.isEmpty(model.getRemarkCount()) || model.getRemarkCount().equals("0")) {
                txtRemark.setVisibility(View.GONE);
            } else {
                txtRemark.setVisibility(View.VISIBLE);
                txtRemark.setText(Html.fromHtml(String.format("<u>%s %s</u>", model.getRemarkCount(), context.getString(R.string.remark))));
            }

        }
    }
}
