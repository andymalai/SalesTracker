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
import com.webmne.salestracker.api.model.LoginResponse;
import com.webmne.salestracker.contactus.ContactUsActivity;
import com.webmne.salestracker.databinding.ActivityLoginBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.ui.dashboard.DashboardActivity;

import java.util.concurrent.TimeoutException;

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

                if (!Functions.isConnected(LoginActivity.this)) {
                    SimpleToast.error(LoginActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

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

       /*SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
        Functions.fireIntent(LoginActivity.this, DashboardActivity.class);*/

        loginApi.login(Functions.toStr(loginBinding.edtPassword), Functions.toStr(loginBinding.edtEmpId), new APIListener<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response) {
                if (response.isSuccessful()) {

                    LoginResponse loginResponse = response.body();

                    Log.e("profile_res", MyApplication.getGson().toJson(loginResponse));

                    if (loginResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        PrefUtils.setUserProfile(LoginActivity.this, loginResponse.getData());

                        SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
                        Functions.fireIntent(LoginActivity.this, DashboardActivity.class);
                        finish();

                    } else {
                        SimpleToast.error(LoginActivity.this, loginResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(LoginActivity.this, "Something went wrong. Please try again", getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (t instanceof TimeoutException) {
                    SimpleToast.error(LoginActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(LoginActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });
    }
}