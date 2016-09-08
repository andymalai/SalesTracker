package com.webmne.salestracker.ui.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.UserProfile;
import com.webmne.salestracker.databinding.ActivityUserProfileBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.PrefUtils;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityUserProfileBinding viewBinding;
    private boolean isEditMode = false;
    private UserProfile userProfile;

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
                viewBinding.txtCancel.setVisibility(View.GONE);
                viewBinding.btnEdit.setText(getString(R.string.btn_edit));
                viewBinding.edtEmpPhone.setFocusable(false);
                viewBinding.edtEmpPhone.setFocusableInTouchMode(false);
                viewBinding.edtEmpEmailId.setFocusable(false);
                viewBinding.edtEmpEmailId.setFocusableInTouchMode(false);
                break;

            case R.id.btnEdit:
                isEditMode = !isEditMode;
                if (isEditMode) {
                    viewBinding.txtCancel.setVisibility(View.VISIBLE);
                    viewBinding.btnEdit.setText(getString(R.string.btn_save));
                    viewBinding.edtEmpPhone.setFocusableInTouchMode(true);
                    viewBinding.edtEmpEmailId.setFocusableInTouchMode(true);

                } else {
                    SimpleToast.ok(UserProfileActivity.this, getString(R.string.profile_success));
                    viewBinding.txtCancel.setVisibility(View.GONE);
                    viewBinding.btnEdit.setText(getString(R.string.btn_edit));
                    viewBinding.edtEmpPhone.setFocusable(false);
                    viewBinding.edtEmpPhone.setFocusableInTouchMode(false);
                    viewBinding.edtEmpEmailId.setFocusable(false);
                    viewBinding.edtEmpEmailId.setFocusableInTouchMode(false);
                }
                break;
        }
    }

}
