package com.webmne.salestracker.agent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.BranchAdapter;
import com.webmne.salestracker.agent.adapter.TierAdapter;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.api.BranchApi;
import com.webmne.salestracker.api.TierApi;
import com.webmne.salestracker.api.model.AddAgentResponse;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.Tier;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddAgentBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

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
    private int tierId = 0, branchId = 0;
    private AppApi appApi;

    private ArrayList<Tier> tierModels;
    private int tierWhich = 0, branchWhich = 0;
    private ArrayList<Branch> branchModels;
    private AdapterView.OnItemSelectedListener branchSelectListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_agent);

        tierApi = new TierApi();
        branchApi = new BranchApi();
        appApi = MyApplication.getRetrofit().create(AppApi.class);

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

        actionListener();

    }

    private void actionListener() {
        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Functions.isConnected(AddAgentActivity.this)) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtAgentName))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_agent_name), getString(R.string.fa_error));
                    return;
                }

                if (tierId == 0) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.select_tier), getString(R.string.fa_error));
                    return;
                }

                if (branchId == 0) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.select_branch), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPhoneNumber))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_phone), getString(R.string.fa_error));
                    return;
                }

                if (Functions.toLength(viewBinding.edtPhoneNumber) < 10) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_valid_phone), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_email_id), getString(R.string.fa_error));
                    return;
                }

                if (!Functions.emailValidation(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_valid_email_id), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtKruniaCode))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_krunia_code), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtAmgGeneral))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_amg_code), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtDescription))) {
                    SimpleToast.error(AddAgentActivity.this, getString(R.string.enter_description), getString(R.string.fa_error));
                    return;
                }

                doAddAgent();

            }
        });

        viewBinding.edtTier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(AddAgentActivity.this)
                        .title(R.string.select_tier)
                        .typeface(Functions.getBoldFont(AddAgentActivity.this), Functions.getRegularFont(AddAgentActivity.this))
                        .items(tierModels)
                        .itemsCallbackSingleChoice(tierWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                tierWhich = which;

                                Tier tier = tierModels.get(which);
                                viewBinding.edtTier.setText(tier.getTierName());
                                tierId = Integer.parseInt(tier.getTeirid());

                                return true;
                            }
                        })
                        .positiveText(R.string.btn_ok)
                        .show();
            }
        });

        viewBinding.edtBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(AddAgentActivity.this)
                        .title(R.string.select_branch)
                        .typeface(Functions.getBoldFont(AddAgentActivity.this), Functions.getRegularFont(AddAgentActivity.this))
                        .items(branchModels)
                        .itemsCallbackSingleChoice(branchWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                branchWhich = which;

                                Branch branch = branchModels.get(which);
                                viewBinding.edtBranch.setText(branch.getBranchName());
                                branchId = Integer.parseInt(branch.getBranchId());

                                return true;
                            }
                        })
                        .positiveText(R.string.btn_ok)
                        .show();
            }
        });
    }

    private void doAddAgent() {

        showProgress();

        JSONObject json = new JSONObject();
        try {
            json.put("AgentName", Functions.toStr(viewBinding.edtAgentName));
            json.put("AmgCode", Functions.toStr(viewBinding.edtAmgGeneral));
            json.put("BranchId", branchId);
            json.put("Description", Functions.toStr(viewBinding.edtDescription));
            json.put("EmailId", Functions.toStr(viewBinding.edtEmailId));
            json.put("KruniaCode", Functions.toStr(viewBinding.edtKruniaCode));
            json.put("MobileNo", Functions.toStr(viewBinding.edtPhoneNumber));
            json.put("TierId", tierId);
            json.put("UserId", PrefUtils.getUserId(this));
            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.AddAgent, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                AddAgentResponse addAgentResponse = MyApplication.getGson().fromJson(response, AddAgentResponse.class);

                if (addAgentResponse != null) {
                    Log.e("add_res", MyApplication.getGson().toJson(addAgentResponse));

                    if (addAgentResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(AddAgentActivity.this, getString(R.string.add_agent_success));
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    } else {
                        SimpleToast.error(AddAgentActivity.this, addAgentResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddAgentActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
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

                        tierModels = tierListResponse.getData().getTiers();

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
                        branchModels = branchListResponse.getData().getBranches();

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
            dialog = new LoadingIndicatorDialog(this, "Please wait..", android.R.style.Theme_Translucent_NoTitleBar);
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
