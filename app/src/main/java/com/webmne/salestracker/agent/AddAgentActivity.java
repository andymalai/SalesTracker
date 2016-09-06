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
import com.webmne.salestracker.api.TierApi;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.databinding.ActivityAddAgentBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class AddAgentActivity extends AppCompatActivity {

    private ActivityAddAgentBinding viewBinding;
    private TierApi tierApi;
    private TierAdapter adapter;

    //  private ArrayList<TierModel> tierModels;
    private ArrayList<Branch> branchModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_agent);

        tierApi = new TierApi();

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
            }
        });

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_agent));

        if (Functions.isConnected(this)) {
            fetchTier();
        } else {
            SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

        setBranchAdapter();

    }

    private void fetchTier() {
        tierApi.getTierList(new APIListener<TierListResponse>() {
            @Override
            public void onResponse(Response<TierListResponse> response) {
                if (response.isSuccessful()) {

                    TierListResponse tierListResponse = response.body();
                    if (tierListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        adapter = new TierAdapter(AddAgentActivity.this, R.layout.item_adapter, tierListResponse.getData().getTiers());
                        viewBinding.spinnerTier.setAdapter(adapter);
                    } else {
                        SimpleToast.error(AddAgentActivity.this, tierListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<TierListResponse> call, Throwable t) {
                SimpleToast.error(AddAgentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
            }
        });
    }

    private void setBranchAdapter() {
        // fetch branches
       /* branchModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Branch model = new Branch();
            branchModels.add(model);
        }

        // set branches to adapter
        viewBinding.spinnerBranch.setAdapter(new BranchAdapter(AddAgentActivity.this, R.layout.item_adapter, branchModels));*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
    }
}
