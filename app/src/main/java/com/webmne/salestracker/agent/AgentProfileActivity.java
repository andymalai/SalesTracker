package com.webmne.salestracker.agent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.BranchAdapter;
import com.webmne.salestracker.agent.adapter.TierAdapter;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.BranchApi;
import com.webmne.salestracker.api.TierApi;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.Tier;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAgentProfileBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.RetrofitErrorHelper;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Response;

public class AgentProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isEditMode = false;

    private ActivityAgentProfileBinding viewBinding;
    private String agent;
    private AgentModel agentModel;
    private TierApi tierApi;
    private BranchApi branchApi;
    private TierAdapter adapter;
    private BranchAdapter branchAdapter;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_agent_profile);
        tierApi = new TierApi();
        branchApi = new BranchApi();

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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.agent_profile_title));

        if (Functions.isConnected(this)) {

            // call all ws one by one
            fetchTier();

        } else {
            SimpleToast.error(AgentProfileActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

        actionListener();
    }

    private void fetchBranches() {
        showProgress();

        branchApi.getBranchList(new APIListener<BranchListResponse>() {
            @Override
            public void onResponse(Response<BranchListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    BranchListResponse branchListResponse = response.body();
                    if (branchListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        branchAdapter = new BranchAdapter(AgentProfileActivity.this, R.layout.item_adapter, branchListResponse.getData().getBranches());
                        viewBinding.spinnerBranch.setAdapter(branchAdapter);

                        // set details after all spinner adapter set
                        fetchDetails();

                    } else {
                        SimpleToast.error(AgentProfileActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AgentProfileActivity.this, getString(R.string.try_again), getString(R.string.fa_error));

                }
            }

            @Override
            public void onFailure(Call<BranchListResponse> call, Throwable t) {
                dismissProgress();
                RetrofitErrorHelper.showErrorMsg(t, AgentProfileActivity.this);
            }
        });
    }

    private void fetchTier() {
        showProgress();

        tierApi.getTierList(new APIListener<TierListResponse>() {
            @Override
            public void onResponse(Response<TierListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {

                    TierListResponse tierListResponse = response.body();
                    if (tierListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        adapter = new TierAdapter(AgentProfileActivity.this, R.layout.item_adapter, tierListResponse.getData().getTiers());
                        viewBinding.spinnerTier.setAdapter(adapter);

                        // call branches for set branch spinner
                        fetchBranches();

                    } else {
                        SimpleToast.error(AgentProfileActivity.this, tierListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AgentProfileActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<TierListResponse> call, Throwable t) {
                dismissProgress();
                RetrofitErrorHelper.showErrorMsg(t, AgentProfileActivity.this);
            }
        });
    }

    private void actionListener() {
        viewBinding.btnEdit.setOnClickListener(this);
        viewBinding.txtCancel.setOnClickListener(this);
    }

    private void fetchDetails() {
        // call api for fetch profile or set details from POJO

        agent = getIntent().getStringExtra("agent");
        agentModel = MyApplication.getGson().fromJson(agent, AgentModel.class);

        viewBinding.edtAgentName.setText(String.format("%s", agentModel.getName()));
        viewBinding.edtPhoneNumber.setText(String.format("%s", agentModel.getMobileNo()));
        viewBinding.edtEmailId.setText(String.format("%s", agentModel.getEmailid()));
        viewBinding.edtKruniaCode.setText(String.format("%s", agentModel.getKruniaCode()));
        viewBinding.edtAmgGeneral.setText(String.format("%s", agentModel.getAmgCode()));
        viewBinding.edtDescription.setText("Hello\nThis is test.\nThank you");

        Log.e("tier", agentModel.getTierid());

        int tierPosition = getIndex(adapter, agentModel.getTierid());
        viewBinding.spinnerTier.setSelection(tierPosition);

        int branchPosition = getBranchIndex(branchAdapter, agentModel.getBranchid());
        viewBinding.spinnerBranch.setSelection(branchPosition);

        adapter.setCanOpen(false);
        branchAdapter.setCanOpen(false);
    }

    private int getIndex(TierAdapter adapter, String myString) {
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            Tier tier = adapter.getItem(i);
            if (tier.getTeirid().equals(myString)) {
                index = i;
                break;
            }

        }
        return index;
    }

    private int getBranchIndex(BranchAdapter adapter, String myString) {
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            Branch tier = adapter.getItem(i);
            if (tier.getBranchId().equals(myString)) {
                index = i;
                break;
            }

        }
        return index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
        tierApi.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCancel:
                if (isEditMode) {
                    disableFields();
                    isEditMode = !isEditMode;
                } else {
                    Toast.makeText(AgentProfileActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnEdit:
                isEditMode = !isEditMode;
                if (isEditMode) {
                    enableFields();

                } else {
                    SimpleToast.ok(AgentProfileActivity.this, getString(R.string.profile_success));
                    disableFields();
                }
                break;
        }
    }

    private void enableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_cancel));
        viewBinding.btnEdit.setText(getString(R.string.btn_save));

        viewBinding.edtAgentName.setFocusableInTouchMode(true);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(true);
        viewBinding.edtEmailId.setFocusableInTouchMode(true);
        viewBinding.edtKruniaCode.setFocusableInTouchMode(true);
        viewBinding.edtAmgGeneral.setFocusableInTouchMode(true);
        viewBinding.edtDescription.setFocusableInTouchMode(true);
    }

    private void disableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_delete));
        viewBinding.btnEdit.setText(getString(R.string.btn_edit));

        viewBinding.edtAgentName.setFocusableInTouchMode(false);
        viewBinding.spinnerTier.setFocusableInTouchMode(false);
        viewBinding.spinnerBranch.setFocusableInTouchMode(false);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(false);
        viewBinding.edtEmailId.setFocusableInTouchMode(false);
        viewBinding.edtKruniaCode.setFocusableInTouchMode(false);
        viewBinding.edtAmgGeneral.setFocusableInTouchMode(false);
        viewBinding.edtDescription.setFocusableInTouchMode(false);

        viewBinding.edtAgentName.setFocusable(false);
        viewBinding.spinnerTier.setFocusable(false);
        viewBinding.spinnerBranch.setFocusable(false);
        viewBinding.edtPhoneNumber.setFocusable(false);
        viewBinding.edtEmailId.setFocusable(false);
        viewBinding.edtKruniaCode.setFocusable(false);
        viewBinding.edtAmgGeneral.setFocusable(false);
        viewBinding.edtDescription.setFocusable(false);
    }

    public void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, "Loading Agent Profile..", android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
