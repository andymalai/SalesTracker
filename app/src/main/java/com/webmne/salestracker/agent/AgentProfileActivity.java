package com.webmne.salestracker.agent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AgentProfileActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtCancel)
    TfTextView txtCancel;
    @BindView(R.id.edtAgentName)
    TfEditText edtAgentName;
    @BindView(R.id.spinnerTier)
    AppCompatSpinner spinnerTier;
    @BindView(R.id.spinnerBranch)
    AppCompatSpinner spinnerBranch;
    @BindView(R.id.edtPhoneNumber)
    TfEditText edtPhoneNumber;
    @BindView(R.id.edtEmailId)
    TfEditText edtEmailId;
    @BindView(R.id.edtKruniaCode)
    TfEditText edtKruniaCode;
    @BindView(R.id.edtAmgGeneral)
    TfEditText edtAmgGeneral;
    @BindView(R.id.edtDescription)
    TfEditText edtDescription;
    @BindView(R.id.btnEdit)
    TfButton btnEdit;
    @BindView(R.id.relativeLayout)
    LinearLayout relativeLayout;

    Unbinder unbinder;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);
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

        txtCustomTitle.setText(getString(R.string.agent_profile_title));

        spinnerTier.setEnabled(false);
        spinnerBranch.setEnabled(false);

        fetchDetails();
    }

    private void fetchDetails() {
        // call api for fetch profile or set details from POJO
        edtAgentName.setText("Sagar");
        edtPhoneNumber.setText("9429841328");
        edtEmailId.setText("sagar@gmail.com");
        edtKruniaCode.setText("KRUNIACODE");
        edtAmgGeneral.setText("AMGCODE");
        edtDescription.setText("Hello\nThis is test.\nThank you");

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
                if (isEditMode) {
                    isEditMode = !isEditMode;
                    txtCancel.setText(getString(R.string.btn_delete));
                    btnEdit.setText(getString(R.string.btn_edit));
                    edtAgentName.setEnabled(false);
                    spinnerTier.setEnabled(false);
                    spinnerBranch.setEnabled(false);
                    edtPhoneNumber.setEnabled(false);
                    edtEmailId.setEnabled(false);
                    edtKruniaCode.setEnabled(false);
                    edtAmgGeneral.setEnabled(false);
                    edtDescription.setEnabled(false);
                } else {
                    Toast.makeText(AgentProfileActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnEdit:
                isEditMode = !isEditMode;
                if (isEditMode) {
                    txtCancel.setText(getString(R.string.btn_cancel));
                    btnEdit.setText(getString(R.string.btn_save));
                    edtAgentName.setEnabled(true);
                    spinnerTier.setEnabled(true);
                    spinnerBranch.setEnabled(true);
                    edtPhoneNumber.setEnabled(true);
                    edtEmailId.setEnabled(true);
                    edtKruniaCode.setEnabled(true);
                    edtAmgGeneral.setEnabled(true);
                    edtDescription.setEnabled(true);

                } else {
                    SimpleToast.ok(AgentProfileActivity.this, getString(R.string.profile_success));
                    txtCancel.setText(getString(R.string.btn_delete));
                    btnEdit.setText(getString(R.string.btn_edit));
                    edtAgentName.setEnabled(false);
                    spinnerTier.setEnabled(false);
                    spinnerBranch.setEnabled(false);
                    edtPhoneNumber.setEnabled(false);
                    edtEmailId.setEnabled(false);
                    edtKruniaCode.setEnabled(false);
                    edtAmgGeneral.setEnabled(false);
                    edtDescription.setEnabled(false);
                }
                break;
        }
    }
}
