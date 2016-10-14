package com.webmne.salestracker.agent;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
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
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private ArrayList<Tier> tierModels;
    private int tierWhich = 0, branchWhich = 0;
    private ArrayList<Branch> branchModels;

    private int tierId = 0, branchId = 0;

    private ArrayList<Integer> selectedAgentIds;

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

        showProgress(getString(R.string.loading));

        branchApi.getBranchList(new APIListener<BranchListResponse>() {
            @Override
            public void onResponse(Response<BranchListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    BranchListResponse branchListResponse = response.body();
                    if (branchListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        branchModels = branchListResponse.getData().getBranches();

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

        showProgress(getString(R.string.loading));

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

        viewBinding.edtTier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(AgentProfileActivity.this)
                        .title(R.string.select_tier)
                        .typeface(Functions.getBoldFont(AgentProfileActivity.this), Functions.getRegularFont(AgentProfileActivity.this))
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
                new MaterialDialog.Builder(AgentProfileActivity.this)
                        .title(R.string.select_branch)
                        .typeface(Functions.getBoldFont(AgentProfileActivity.this), Functions.getRegularFont(AgentProfileActivity.this))
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

    private void fetchDetails() {
        // call api for fetch profile or set details from POJO

        agent = getIntent().getStringExtra("agent");
        agentModel = MyApplication.getGson().fromJson(agent, AgentModel.class);

        viewBinding.edtAgentName.setText(String.format("%s", agentModel.getName()));
        viewBinding.edtPhoneNumber.setText(String.format("%s", agentModel.getMobileNo()));
        viewBinding.edtEmailId.setText(String.format("%s", agentModel.getEmailid()));
        viewBinding.edtKruniaCode.setText(String.format("%s", agentModel.getKruniaCode()));
        viewBinding.edtAmgGeneral.setText(String.format("%s", agentModel.getAmgCode()));
        viewBinding.edtDescription.setText(String.format("%s", agentModel.getDescription()));

        tierWhich = getIndex(tierModels, agentModel.getTierid());
        viewBinding.edtTier.setText(tierModels.get(tierWhich).getTierName());
        tierId = Integer.parseInt(tierModels.get(tierWhich).getTeirid());

        branchWhich = getBranchIndex(branchModels, agentModel.getBranchid());
        viewBinding.edtBranch.setText(branchModels.get(branchWhich).getBranchName());
        branchId = Integer.parseInt(branchModels.get(branchWhich).getBranchId());

        disableFields();
    }

    private int getIndex(ArrayList<Tier> adapter, String myString) {
        int index = 0;
        for (int i = 0; i < adapter.size(); i++) {
            Tier tier = adapter.get(i);
            if (tier.getTeirid().equals(myString)) {
                index = i;
                break;
            }

        }
        return index;
    }

    private int getBranchIndex(ArrayList<Branch> adapter, String myString) {
        int index = 0;
        for (int i = 0; i < adapter.size(); i++) {
            Branch tier = adapter.get(i);
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

                    Functions.showPrompt(AgentProfileActivity.this, "Yes", "No", getString(R.string.agent_delete_dialog), new Functions.onPromptListener() {
                        @Override
                        public void onClickYes(MaterialDialog dialog) {

                            deleteAgent();

                        }

                        @Override
                        public void onClickNo(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    });

                }
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

    private void doUpdateProfile() {

        showProgress(getString(R.string.update_agents_profile));

        JSONObject json = new JSONObject();
        try {
            json.put("AgentName", Functions.toStr(viewBinding.edtAgentName));
            json.put("TierId", tierId);
            json.put("EmailId", Functions.toStr(viewBinding.edtEmailId));
            json.put("Description", Functions.toStr(viewBinding.edtDescription));
            json.put("MobileNo", Functions.toStr(viewBinding.edtPhoneNumber));
            json.put("AgentId", Integer.parseInt(agentModel.getAgentid()));
            json.put("AmgCode", Functions.toStr(viewBinding.edtAmgGeneral));
            json.put("KruniaCode", Functions.toStr(viewBinding.edtKruniaCode));

            Log.e("update_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(AgentProfileActivity.this, AppConstants.UpdateAgent, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(AgentProfileActivity.this, getString(R.string.profile_success));

                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        SimpleToast.error(AgentProfileActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AgentProfileActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AgentProfileActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void enableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_cancel));
        viewBinding.btnEdit.setText(getString(R.string.btn_save));

        viewBinding.edtAgentName.setFocusableInTouchMode(true);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(true);
        viewBinding.edtEmailId.setFocusableInTouchMode(true);
        viewBinding.edtDescription.setFocusableInTouchMode(true);
        viewBinding.edtKruniaCode.setFocusableInTouchMode(true);
        viewBinding.edtAmgGeneral.setFocusableInTouchMode(true);

        viewBinding.edtTier.setClickable(true);
        viewBinding.edtBranch.setClickable(false);

    }

    private void disableFields() {

        viewBinding.txtCancel.setText(getString(R.string.btn_delete));
        viewBinding.btnEdit.setText(getString(R.string.btn_edit));

        viewBinding.edtAgentName.setFocusableInTouchMode(false);
        viewBinding.edtTier.setClickable(false);
        viewBinding.edtBranch.setClickable(false);

        viewBinding.edtPhoneNumber.setFocusableInTouchMode(false);
        viewBinding.edtEmailId.setFocusableInTouchMode(false);
        viewBinding.edtKruniaCode.setFocusableInTouchMode(false);
        viewBinding.edtAmgGeneral.setFocusableInTouchMode(false);
        viewBinding.edtDescription.setFocusableInTouchMode(false);

        viewBinding.edtAgentName.setFocusable(false);
        viewBinding.edtPhoneNumber.setFocusable(false);
        viewBinding.edtEmailId.setFocusable(false);
        viewBinding.edtKruniaCode.setFocusable(false);
        viewBinding.edtAmgGeneral.setFocusable(false);
        viewBinding.edtDescription.setFocusable(false);
    }

    private void deleteAgent() {
        showProgress(getString(R.string.delete_agents));

        selectedAgentIds = new ArrayList<>();

        selectedAgentIds.add(Integer.valueOf(agentModel.getAgentid()));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AgentID", new JSONArray(selectedAgentIds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("delete_req", jsonObject.toString());

        new CallWebService(this, AppConstants.DeleteAgent, CallWebService.TYPE_POST, jsonObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response deleteResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                Log.e("delete_res", deleteResponse.toString());

                if (deleteResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                    SimpleToast.ok(AgentProfileActivity.this, getString(R.string.delete_success));
                    finish();
                } else {
                    SimpleToast.error(AgentProfileActivity.this, deleteResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AgentProfileActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AgentProfileActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
