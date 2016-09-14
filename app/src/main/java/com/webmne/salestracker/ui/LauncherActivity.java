package com.webmne.salestracker.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.ActivityLauncherBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.ui.dashboard.DashboardActivity;

public class LauncherActivity extends AppCompatActivity {

    ActivityLauncherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_launcher);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);

        binding.txtAppName.animateText(getString(R.string.app_name)); // animate

        new CountDownTimer(3500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (PrefUtils.getLoggedIn(LauncherActivity.this)) {
                    Functions.fireIntent(LauncherActivity.this, DashboardActivity.class);
                } else {
                    Functions.fireIntent(LauncherActivity.this, LoginActivity.class);
                }
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
