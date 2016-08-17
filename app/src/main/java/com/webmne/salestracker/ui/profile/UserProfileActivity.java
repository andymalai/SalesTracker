package com.webmne.salestracker.ui.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtEmpId)
    TfEditText edtEmpId;
    @BindView(R.id.edtEmpPosition)
    TfEditText edtEmpPosition;
    @BindView(R.id.edtEmpName)
    TfEditText edtEmpName;
    @BindView(R.id.edtEmpPhone)
    TfEditText edtEmpPhone;
    @BindView(R.id.edtEmpEmailId)
    TfEditText edtEmpEmailId;
    @BindView(R.id.edtEmpBranch)
    TfEditText edtEmpBranch;
    @BindView(R.id.btnEdit)
    TfButton btnEdit;

    Unbinder unbinder;
    @BindView(R.id.txtCancel)
    TfTextView txtCancel;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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
        txtCustomTitle.setText(getString(R.string.profile_title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.txtCancel, R.id.btnEdit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCancel:
                isEditMode = !isEditMode;
                txtCancel.setVisibility(View.GONE);
                btnEdit.setText(getString(R.string.btn_edit));
                edtEmpPhone.setEnabled(false);
                edtEmpEmailId.setEnabled(false);
                break;

            case R.id.btnEdit:
                isEditMode = !isEditMode;
                if (isEditMode) {
                    txtCancel.setVisibility(View.VISIBLE);
                    btnEdit.setText(getString(R.string.btn_save));
                    edtEmpPhone.setEnabled(true);
                    edtEmpEmailId.setEnabled(true);

                } else {
                    SimpleToast.ok(UserProfileActivity.this, getString(R.string.profile_success));
                    txtCancel.setVisibility(View.GONE);
                    btnEdit.setText(getString(R.string.btn_edit));
                    edtEmpPhone.setEnabled(false);
                    edtEmpEmailId.setEnabled(false);
                }
                break;
        }
    }

}
