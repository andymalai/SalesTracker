package com.webmne.salestracker.ui.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.UserProfile;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityUserProfileBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityUserProfileBinding viewBinding;
    private boolean isEditMode = false;
    private UserProfile userProfile;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        userProfile = PrefUtils.getUserProfile(this);

        init();
    }

    private void init() {
        if (viewBinding.toolbarLayout.toolbar != null)
            viewBinding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(viewBinding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewBinding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
            }
        });
        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.profile_title));

        setProfileDetails();

        actionListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
    }

    private void actionListener() {
        viewBinding.txtCancel.setOnClickListener(this);
        viewBinding.btnEdit.setOnClickListener(this);
    }

    private void setProfileDetails() {
        viewBinding.edtEmpName.setText(userProfile.getFirstName());
        viewBinding.edtEmpPosition.setText(userProfile.getPos_name());
        viewBinding.edtEmpPhone.setText(userProfile.getMobile());
        viewBinding.edtEmpEmailId.setText(userProfile.getEmail());
        viewBinding.edtEmpBranch.setText(userProfile.getBranch_name());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
    }

    public void onClick(View view) {
        Functions.hideKeyPad(this, view);

        switch (view.getId()) {
            case R.id.txtCancel:
                isEditMode = !isEditMode;
                disableFields();
                break;

            case R.id.btnEdit:
                isEditMode = !isEditMode;
                if (isEditMode) {
                    enableFields();

                } else {
                    doUpdateProfile();
                }
                break;
        }
    }

    private void enableFields() {
        viewBinding.btnEdit.setText(getString(R.string.btn_save));
        viewBinding.edtEmpName.setFocusableInTouchMode(true);
        viewBinding.edtEmpPhone.setFocusableInTouchMode(true);
        viewBinding.edtEmpEmailId.setFocusableInTouchMode(true);
        viewBinding.txtCancel.setVisibility(View.VISIBLE);
    }

    private void disableFields() {
        viewBinding.edtEmpPhone.setFocusable(false);
        viewBinding.edtEmpPhone.setFocusableInTouchMode(false);
        viewBinding.edtEmpEmailId.setFocusable(false);
        viewBinding.edtEmpName.setFocusable(false);
        viewBinding.edtEmpEmailId.setFocusableInTouchMode(false);
        viewBinding.edtEmpName.setFocusableInTouchMode(false);

        viewBinding.txtCancel.setVisibility(View.GONE);
        viewBinding.btnEdit.setText(getString(R.string.btn_edit));
    }

    private void doUpdateProfile() {

        showProgress(getString(R.string.update_user));

        JSONObject json = new JSONObject();
        try {
            json.put("Name", Functions.toStr(viewBinding.edtEmpName));
            json.put("Phone", Functions.toStr(viewBinding.edtEmpPhone));
            json.put("EmailId", Functions.toStr(viewBinding.edtEmpEmailId));
            json.put("UserId", Integer.parseInt(PrefUtils.getUserId(this)));
            Log.e("update_profile", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.UpdateUserProfile, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(UserProfileActivity.this, getString(R.string.profile_success));
                        disableFields();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        SimpleToast.error(UserProfileActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, UserProfileActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(UserProfileActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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
