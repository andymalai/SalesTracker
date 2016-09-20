package com.webmne.salestracker.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.LayoutActionLogBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;

import java.io.File;

/**
 * Created by sagartahelyani on 23-08-2016.
 */
public class ActionLogDetails extends LinearLayout {

    private Context context;
    private LayoutInflater inflater;
    private LayoutActionLogBinding binding;
    private View parentView;
    private File file;

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

        createDir();
    }

    private void createDir() {
        //file = context.getDir("/SalesTracker/ActionLog", Context.MODE_PRIVATE);
        file = new File("/sdcard/SalesTracker/ActionLog/");
        //file = new File(Environment.getExternalStorageDirectory().toString() + "/SalesTracker/ActionLog");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void setActionLog(final ActionLogModel actionLog) {
        binding.txtAgentName.setText(actionLog.getAgentName());
        binding.txtDescription.setText(actionLog.getDescription());
        binding.txtDateRaised.setText(Functions.parseDate(actionLog.getCreatedDatetime(), "dd MMM yyyy, hh:mm a"));
        binding.txtStatus.setText(String.format("%s", Functions.getStatus(context, actionLog.getStatus())));
        binding.txtDepartment.setText(actionLog.getDepartmentName());
        binding.txtSla.setText(String.format("%s Days", actionLog.getSla()));
        binding.txtLastUpdate.setText(String.format("%s", Functions.parseDate(actionLog.getUpdatedDatetime(), "dd MMM yyyy, hh:mm a")));

        if (TextUtils.isEmpty(actionLog.getAttachment())) {
            binding.txtAttachment.setText(String.format("%s", context.getString(R.string.no_attachment)));
            binding.txtAttachment.setTextColor(ContextCompat.getColor(context, R.color.half_black));

        } else {
            binding.txtAttachment.setText(Html.fromHtml(String.format("<u>%s</u>", actionLog.getAttachment())));
            binding.txtAttachment.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }

        binding.txtAttachment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(actionLog.getAttachment())) {
                    String url = AppConstants.ATTACHMENT_PREFIX + actionLog.getAttachmentPath() + "/" + actionLog.getAttachment();

                }
            }
        });
    }
}
