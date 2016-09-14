package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.ActivityActionLogDetailsBinding;

public class ActionLogDetailsActivity extends AppCompatActivity {

    private ActivityActionLogDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_action_log_details);

        init();

    }

    private void init() {

        if (binding.toolbarLayout.toolbar != null) {
            binding.toolbarLayout.toolbar.setTitle("");

        }
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.action_log_details_title));

        // static, change this fetch from intent
//        ActionLogModel actionLog = new ActionLogModel();
//        actionLog.setAgentName("Sagar");
//        actionLog.setCompleted(false);
//        actionLog.setSLA(2);
//        actionLog.setDescription(getString(R.string.dummy));
//        actionLog.setDepartment("Hub Operation");
//        actionLog.setDateRaised("18-08-2016");
//        actionLog.setLastUpdate("22-08-2016");

//        binding.actionLog.setActionLog(actionLog);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
