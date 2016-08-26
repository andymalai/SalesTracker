package com.webmne.salestracker.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.LoginApi;
import com.webmne.salestracker.contactus.ContactUsActivity;
import com.webmne.salestracker.databinding.ActivityLoginBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.ui.dashboard.DashboadActivity;
import com.webmne.salestracker.ui.model.UserProfile;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private LoginApi loginApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginApi = new LoginApi();

        init();
    }

    private void init() {
        loginBinding.txtContactUs.setText(Html.fromHtml("<u>" + getString(R.string.contact_us) + "</u>"));

        actionListener();
    }

    private void actionListener() {
        loginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Functions.toStr(loginBinding.edtEmpId))) {
                    SimpleToast.error(LoginActivity.this, getString(R.string.emp_error), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(loginBinding.edtPassword))) {
                    SimpleToast.error(LoginActivity.this, getString(R.string.pwd_error), getString(R.string.fa_error));
                    return;
                }

                if (Functions.toLength(loginBinding.edtPassword) < getResources().getInteger(R.integer.pwd_length)) {
                    SimpleToast.error(LoginActivity.this, getString(R.string.pwd_len_error), getString(R.string.fa_error));
                    return;
                }

                doLogin();
            }
        });

        loginBinding.txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(LoginActivity.this, ContactUsActivity.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginBinding.unbind();
        loginApi.onDestroy();
    }

    private void doLogin() {

        loginApi.login("json", "MKT_guj_001", "MKT_guj_001", "9", new APIListener<UserProfile>() {
            @Override
            public void onResponse(Response<UserProfile> response) {
                if (response.body() != null) {

                    UserProfile profile = response.body();

                    Log.e("profile_res", MyApplication.getGson().toJson(profile));

                    if (profile.getResponseCode().equals(AppConstants.SUCCESS)) {

                        PrefUtils.setUserProfile(LoginActivity.this, profile);

                        SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
                        Functions.fireIntent(LoginActivity.this, DashboadActivity.class);

                    } else {

                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

            }
        });
    }
}