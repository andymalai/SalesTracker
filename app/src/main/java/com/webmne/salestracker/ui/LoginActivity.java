package com.webmne.salestracker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.webmne.salestracker.R;
import com.webmne.salestracker.contactus.ContactUsActivity;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.ui.dashboard.DashboadActivity;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txtLoginTitle)
    TfTextView txtLoginTitle;
    @BindView(R.id.edtEmpId)
    MaterialEditText edtEmpId;
    @BindView(R.id.edtPassword)
    MaterialEditText edtPassword;
    @BindView(R.id.btnLogin)
    TfButton btnLogin;
    @BindView(R.id.txtContactUs)
    TfTextView txtContactUs;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);

        init();
    }

    private void init() {
        txtContactUs.setText(Html.fromHtml("<u>" + getString(R.string.contact_us) + "</u>"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void doLogin() {
        SimpleToast.ok(this, getString(R.string.login_success));
        Functions.fireIntent(this, DashboadActivity.class);
    }

    @OnClick({R.id.btnLogin, R.id.txtContactUs})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (TextUtils.isEmpty(Functions.toStr(edtEmpId))) {
                    SimpleToast.error(this, getString(R.string.emp_error), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(edtPassword))) {
                    SimpleToast.error(this, getString(R.string.pwd_error), getString(R.string.fa_error));
                    return;
                }

                if (Functions.toLength(edtPassword) < getResources().getInteger(R.integer.pwd_length)) {
                    SimpleToast.error(this, getString(R.string.pwd_len_error), getString(R.string.fa_error));
                    return;
                }

                doLogin();
                break;

            case R.id.txtContactUs:
                Functions.fireIntent(this, ContactUsActivity.class);
                break;
        }
    }
}

