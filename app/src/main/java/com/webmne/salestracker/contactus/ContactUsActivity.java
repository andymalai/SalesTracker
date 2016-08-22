package com.webmne.salestracker.contactus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactUsActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtName)
    TfEditText edtName;
    @BindView(R.id.edtEmail)
    TfEditText edtEmail;
    @BindView(R.id.edtMessage)
    TfEditText edtMessage;
    @BindView(R.id.btnLogin)
    TfButton btnLogin;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        unbinder = ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (toolbar != null)
            toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtCustomTitle.setText(getString(R.string.contact_us_title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
