package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.RemarkListAdapter;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.api.model.Remark;
import com.webmne.salestracker.api.model.RemarksListResponse;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityActionLogDetailsBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ActionLogDetailsActivity extends AppCompatActivity {

    private ActivityActionLogDetailsBinding binding;
    private String action;
    private ActionLogModel actionLog;
    private LoadingIndicatorDialog dialog;
    private String[] reopenIdSplit;
    private ArrayList<Remark> remarkModelList;
    private RemarkListAdapter remarkListAdapter;
    private String reopen_url, str_desc;
    private String actionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_action_log_details);

        init();

    }

    private void init() {

        if (binding.toolbarLayout.toolbar != null) {
            binding.toolbarLayout.toolbar.setTitle("");
        }
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.action_log_details_title));

        actionListener();

        initRecyclerView();

        // static, change this fetch from intent
        fetchActionLogDetails();
    }

    private void initRecyclerView() {
        remarkModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.agentRecyclerView.setLayoutManager(layoutManager);
        binding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        remarkListAdapter = new RemarkListAdapter(this, remarkModelList);
        binding.agentRecyclerView.setAdapter(remarkListAdapter);
        binding.agentRecyclerView.setHasFixedSize(true);
    }

    private void actionListener() {

        binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveRejectActionlog("A");
            }
        });

        binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveRejectActionlog("R");
            }
        });

        binding.btnReopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reopenAction();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Functions.toStr(binding.edtRamark))) {
                    SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.empty_remark));

                } else {
                    updateActionlog();
                }
            }
        });

    }

    private void initDialog(final MaterialDialog dialog) {
        View view = dialog.getCustomView();
        if (view != null) {
            LinearLayout parentView = (LinearLayout) view.findViewById(R.id.parentView);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            final TfEditText editText = (TfEditText) view.findViewById(R.id.edtDesc);
            TfButton btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
            TfButton btnOk = (TfButton) view.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(Functions.toStr(editText))) {
                        str_desc = editText.getText().toString();
                        updateActionlog();
                        dialog.dismiss();
                    } else {
                        SimpleToast.error(ActionLogDetailsActivity.this, "Cannot send empty description");
                    }

                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }

    private void fetchActionLogDetails() {
        action = getIntent().getStringExtra("action");
        actionLog = MyApplication.getGson().fromJson(action, ActionLogModel.class);
        binding.actionLog.setActionLog(actionLog);

        actionId = actionLog.getId().split("_")[1];

        if (actionLog.getReopen().equalsIgnoreCase(getString(R.string.str_true))) {
            binding.btnReopen.setVisibility(View.VISIBLE);
        } else {
            binding.btnReopen.setVisibility(View.GONE);
        }

        if (actionLog.getPosition().equals(AppConstants.MARKETER) && !actionLog.getCreaterId().equals(PrefUtils.getUserId(this))) {
            binding.btnApprove.setVisibility(View.VISIBLE);
            binding.btnReject.setVisibility(View.VISIBLE);
        } else {
            binding.btnApprove.setVisibility(View.GONE);
            binding.btnReject.setVisibility(View.GONE);
        }

        fetchRemark();
    }

    private void fetchRemark() {

        showProgress(getString(R.string.loading));

        remarkModelList = new ArrayList<>();

        new CallWebService(this, AppConstants.ActionLogRemarksList + actionId, CallWebService.TYPE_GET) {
            @Override
            public void response(String response) {

                dismissProgress();

                RemarksListResponse remarksListResponse = MyApplication.getGson().fromJson(response, RemarksListResponse.class);

                if (remarksListResponse != null) {

                    Log.e("remarksListResponse", MyApplication.getGson().toJson(remarksListResponse));

                    if (remarksListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        remarkListAdapter.setRemarkList(remarksListResponse.getData().getRemarks());
                        binding.txtRemarkCount.setText(String.format(Locale.US, "%d Remarks", remarksListResponse.getData().getRemarks().size()));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ActionLogDetailsActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void reopenAction() {

        showProgress(getString(R.string.update_user));

        reopenIdSplit = actionLog.getId().split("_");

        reopen_url = AppConstants.ReopenRemark + reopenIdSplit[1];

        Log.e("reopen_remark_url", reopen_url);

        new CallWebService(this, reopen_url, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response reopenRemarkResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);

                Log.e("reopen_remark_response", MyApplication.getGson().toJson(reopenRemarkResponse));

                if (reopenRemarkResponse != null) {
                    if (reopenRemarkResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(ActionLogDetailsActivity.this, getString(R.string.reopen_success));
                    } else {
                        SimpleToast.error(ActionLogDetailsActivity.this, reopenRemarkResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ActionLogDetailsActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void approveRejectActionlog(String strFlag) {

        showProgress(getString(R.string.loading));

        String[] splitId = actionLog.getId().split("_");

        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", splitId[1]);
            json.put("Status", strFlag);
            json.put("UserId", PrefUtils.getUserId(this));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("action_req", json.toString());

        new CallWebService(this, AppConstants.ApproveRejectActionLog, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response updateActionLogResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);

                if (updateActionLogResponse != null) {
                    if (updateActionLogResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        Log.e("updateActionLogResponse", MyApplication.getGson().toJson(updateActionLogResponse));

                        SimpleToast.ok(ActionLogDetailsActivity.this, updateActionLogResponse.getResponse().getResponseMsg());

                        finish();

                    } else {
                        SimpleToast.error(ActionLogDetailsActivity.this, updateActionLogResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ActionLogDetailsActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void updateActionlog() {

        showProgress(getString(R.string.loading));

        reopenIdSplit = actionLog.getId().split("_");

        JSONObject json = new JSONObject();
        try {
            json.put("UserId", Integer.parseInt(PrefUtils.getUserId(this)));
            json.put("Description", Functions.toStr(binding.edtRamark));
            // json.put("Status", actionLog.getStatus());
            json.put("ActionLogId", Integer.parseInt(reopenIdSplit[1]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("update_action_log_url", AppConstants.UpdateActionLog);

        Log.e("action_req", json.toString());

        new CallWebService(this, AppConstants.UpdateActionLog, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response updateActionLogResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);

                if (updateActionLogResponse != null) {
                    if (updateActionLogResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(ActionLogDetailsActivity.this, updateActionLogResponse.getResponse().getResponseMsg());
                        fetchRemark();
                        binding.edtRamark.setText("");

                    } else {
                        SimpleToast.error(ActionLogDetailsActivity.this, updateActionLogResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ActionLogDetailsActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ActionLogDetailsActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
