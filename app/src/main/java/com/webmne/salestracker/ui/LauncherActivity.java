package com.webmne.salestracker.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.hanks.htextview.HTextView;
import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.ActivityLauncherBinding;
import com.webmne.salestracker.helper.Functions;

public class LauncherActivity extends AppCompatActivity {

    ActivityLauncherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_launcher);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);

        binding.txtAppName.animateText(getString(R.string.app_name)); // animate

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Functions.fireIntent(LauncherActivity.this, LoginActivity.class);
                finish();
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
