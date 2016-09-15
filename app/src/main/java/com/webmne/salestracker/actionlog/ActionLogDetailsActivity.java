package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityActionLogDetailsBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

public class ActionLogDetailsActivity extends AppCompatActivity {

    private ActivityActionLogDetailsBinding binding;
    private String action;
    private ActionLogModel actionLog;
    private LoadingIndicatorDialog dialog;
    private String[] reopenIdSplit;
    private String reopen_url,str_desc;

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

        // static, change this fetch from intent
        fetchActionLogDetails();
    }

    private void actionListener() {
        binding.btnReopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reopenAction();
            }
        });

        binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new MaterialDialog.Builder(ActionLogDetailsActivity.this)
                        .title(R.string.dialog_title)
                        .customView(R.layout.custom_dialog_action_log_edit, true)
                        .positiveText(R.string.btn_ok)
                        .negativeText(R.string.btn_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                EditText editText = (EditText) view.findViewById(R.id.edtDesc);

                                if (!editText.getText().toString().equals("")) {
                                    str_desc = editText.getText().toString();
                                    updateActionlog();
                                }
                            }
                        })
                        .show();

            }
        });

    }

    private void fetchActionLogDetails() {
        action = getIntent().getStringExtra("action");
        actionLog = MyApplication.getGson().fromJson(action, ActionLogModel.class);
        binding.actionLog.setActionLog(actionLog);

        if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.MARKETER)) {
            binding.btnRemark.setVisibility(View.GONE);
        } else {
            binding.btnRemark.setVisibility(View.VISIBLE);
        }

        if (actionLog.getReopen().equalsIgnoreCase(getString(R.string.str_true))) {
            binding.btnReopen.setEnabled(true);
        } else {
            binding.btnReopen.setEnabled(false);
        }
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

    private void updateActionlog() {

        showProgress(getString(R.string.update_user));

        reopenIdSplit = actionLog.getId().split("_");

        JSONObject json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("Description", str_desc);
            json.put("Status", actionLog.getStatus());
            json.put("ActionLogId", reopenIdSplit[1]);

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

                Log.e("tag", "update_action_log_response:-"+MyApplication.getGson().toJson(updateActionLogResponse));

                if (updateActionLogResponse != null) {
                    if (updateActionLogResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
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
