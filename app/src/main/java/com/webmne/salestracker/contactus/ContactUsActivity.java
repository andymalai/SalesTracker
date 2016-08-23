package com.webmne.salestracker.contactus;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.ActivityContactUsBinding;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding contactUsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactUsBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);

        init();
    }

    private void init() {
        if (contactUsBinding.toolbarLayout.toolbar != null)
            contactUsBinding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(contactUsBinding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contactUsBinding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contactUsBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.contact_us_title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contactUsBinding.unbind();
    }
}
