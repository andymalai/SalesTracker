package com.webmne.salestracker.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.LayoutActionLogBinding;
import com.webmne.salestracker.helper.Functions;

/**
 * Created by sagartahelyani on 23-08-2016.
 */
public class ActionLogDetails extends LinearLayout {

    private Context context;
    private LayoutInflater inflater;
    private LayoutActionLogBinding binding;
    private View parentView;

    public ActionLogDetails(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    public ActionLogDetails(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    public ActionLogDetails(Context context) {
        super(context);
        if (!isInEditMode()) {
            this.context = context;
            init();
        }
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_action_log, this, true);
        parentView = binding.getRoot();

    }

    public void setActionLog(ActionLogModel actionLog) {
        binding.txtAgentName.setText(actionLog.getAgentName());
        binding.txtDescription.setText(actionLog.getDescription());
        binding.txtDateRaised.setText(Functions.parseDate(actionLog.getCreatedDatetime(), "dd MMM yyyy, hh:mm a"));
        binding.txtStatus.setText(String.format("%s", Functions.getStatus(context, actionLog.getStatus())));
        binding.txtDepartment.setText(actionLog.getDepartmentName());
        binding.txtSla.setText(String.format("%s Days", actionLog.getSla()));
        binding.txtLastUpdate.setText(String.format("%s", Functions.parseDate(actionLog.getUpdatedDatetime(), "dd MMM yyyy, hh:mm a")));

    }
}
