package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.databinding.ActivityActionLogDetailsBinding;
import com.webmne.salestracker.helper.MyApplication;

public class ActionLogDetailsActivity extends AppCompatActivity {

    private ActivityActionLogDetailsBinding binding;
    private String action;
    private ActionLogModel actionLog;

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

        fetchActionLogDetails();
    }

    private void fetchActionLogDetails() {
        action = getIntent().getStringExtra("action");
        actionLog = MyApplication.getGson().fromJson(action, ActionLogModel.class);
        binding.actionLog.setActionLog(actionLog);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
