package com.webmne.salestracker.visitplan.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.DialogRecruitmentCalendarViewBinding;
import com.webmne.salestracker.visitplan.adapter.AgentStatusAdapter;
import com.webmne.salestracker.widget.TfEditText;

import java.util.ArrayList;

/**
 * Created by priyasindkar on 06-10-2016.
 */

public class RecruitmentDialog extends MaterialDialog {
    private Context context;
    private MaterialDialog materialDialog;
    private DialogRecruitmentCalendarViewBinding dialogRecruitmentCalendarViewBinding;
    private AgentStatusAdapter adapter;

    public RecruitmentDialog(Builder builder, Context context) {
        super(builder);
        this.context = context;
        init(builder);
    }

    private void init(final Builder builder) {
        dialogRecruitmentCalendarViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_recruitment_calendar_view, null, false);
        materialDialog = builder.customView(dialogRecruitmentCalendarViewBinding.getRoot(), true)
                .title("Recruitment")
                .cancelable(true)
                .build();

        final ArrayList<String> agentStatusList = new ArrayList<>();
        agentStatusList.add("Blank");
        agentStatusList.add("L1");
        agentStatusList.add("L2");
        agentStatusList.add("P1");
        agentStatusList.add("P2");
        agentStatusList.add("P3");
        agentStatusList.add("R");

        adapter = new AgentStatusAdapter(context, R.layout.item_adapter, agentStatusList);
        dialogRecruitmentCalendarViewBinding.defaultAddRecruitmentItem.spinnerStatus.setAdapter(adapter);

        dialogRecruitmentCalendarViewBinding.txtAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = materialDialog.getLayoutInflater().inflate(R.layout.add_recruitment_item, dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems, false);
                dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.addView(view);
                AppCompatSpinner agentStatusSpinner = (AppCompatSpinner) view.findViewById(R.id.spinnerStatus);
                agentStatusSpinner.setAdapter(adapter);
            }
        });

        dialogRecruitmentCalendarViewBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildCount(); i++) {
                    TfEditText edtAgentName = (TfEditText) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.edtAgentName);
                    TfEditText edtRemarks = (TfEditText) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.edtRemarks);
                    AppCompatSpinner agentStatusSpinner = (AppCompatSpinner) dialogRecruitmentCalendarViewBinding.linearAddRecruitmentItems.getChildAt(i).findViewById(R.id.spinnerStatus);
                }
            }
        });
    }

    public void show() {
        materialDialog.show();
    }

    public void dismiss() {
        materialDialog.dismiss();
    }
}
