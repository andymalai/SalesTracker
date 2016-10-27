package com.webmne.salestracker.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.LoginApi;
import com.webmne.salestracker.api.model.LoginResponse;
import com.webmne.salestracker.contactus.ContactUsActivity;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityLoginBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.RetrofitErrorHelper;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.ui.dashboard.DashboardActivity;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private LoginApi loginApi;
    private LoadingIndicatorDialog dialog;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginBinding.unbind();
        loginApi.onDestroy();
    }

//    private void doLogin() {
//
//       /*SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
//        Functions.fireIntent(LoginActivity.this, DashboardActivity.class);*/
//
//        showProgress();
//
//        loginApi.login(Functions.toStr(loginBinding.edtPassword), Functions.toStr(loginBinding.edtEmpId), new APIListener<LoginResponse>() {
//            @Override
//            public void onResponse(Response<LoginResponse> response) {
//                dismissProgress();
//                if (response.isSuccessful()) {
//
//                    LoginResponse loginResponse = response.body();
//
//                    Log.e("profile_res", MyApplication.getGson().toJson(loginResponse));
//
//                    if (loginResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
//
//                        PrefUtils.setUserProfile(LoginActivity.this, loginResponse.getData());
//
//                        SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
//                        Functions.fireIntent(LoginActivity.this, DashboardActivity.class);
//                        finish();
//
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
//                    } else {
//                        SimpleToast.error(LoginActivity.this, loginResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
//
//                } else {
//                    SimpleToast.error(LoginActivity.this, "Something went wrong. Please try again", getString(R.string.fa_error));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                dismissProgress();
//                RetrofitErrorHelper.showErrorMsg(t, LoginActivity.this);
//            }
//        });
//    }

    private void doLogin() {

        showProgress(getString(R.string.loading));

        new CallWebService(this, AppConstants.Login + "&password=" + Functions.toStr(loginBinding.edtPassword) + "&username=" + Functions.toStr(loginBinding.edtEmpId), CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                LoginResponse loginResponse = MyApplication.getGson().fromJson(response, LoginResponse.class);

                if (loginResponse != null) {

                    Log.e("profile_res", MyApplication.getGson().toJson(loginResponse));

                    if (loginResponse.getResponse() != null && loginResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        PrefUtils.setUserProfile(LoginActivity.this, loginResponse.getData());

                        SimpleToast.ok(LoginActivity.this, getString(R.string.login_success));
                        Functions.fireIntent(LoginActivity.this, DashboardActivity.class);
                        finish();

                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {
                        SimpleToast.error(LoginActivity.this, loginResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, LoginActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(LoginActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();

    }

    public void showProgress(String string) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, string, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}