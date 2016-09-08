package com.webmne.salestracker.agent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.BranchAdapter;
import com.webmne.salestracker.agent.adapter.TierAdapter;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.BranchApi;
import com.webmne.salestracker.api.TierApi;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddAgentBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Response;

public class AddAgentActivity extends AppCompatActivity {

    private ActivityAddAgentBinding viewBinding;
    private TierApi tierApi;
    private BranchApi branchApi;
    private TierAdapter adapter;
    private BranchAdapter branchAdapter;
    private LoadingIndicatorDialog dialog;

    //  private ArrayList<TierModel> tierModels;
    private ArrayList<Branch> branchModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_agent);

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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_agent));

        if (Functions.isConnected(this)) {

            // call all ws one by one
            fetchTier();

        } else {
            SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                        adapter = new TierAdapter(AddAgentActivity.this, R.layout.item_adapter, tierListResponse.getData().getTiers());
                        viewBinding.spinnerTier.setAdapter(adapter);

                        // call branches for set branch spinner
                        fetchBranches();
                    } else {
                        SimpleToast.error(AddAgentActivity.this, tierListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<TierListResponse> call, Throwable t) {
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });
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
                        branchAdapter = new BranchAdapter(AddAgentActivity.this, R.layout.item_adapter, branchListResponse.getData().getBranches());
                        viewBinding.spinnerBranch.setAdapter(branchAdapter);

                    } else {
                        SimpleToast.error(AddAgentActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));

                }
            }

            @Override
            public void onFailure(Call<BranchListResponse> call, Throwable t) {
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });
    }

    public void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, "Loading Agents..", android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
    }
}
