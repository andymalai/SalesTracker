package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.RemarkListAdapter;
import com.webmne.salestracker.api.model.Remark;
import com.webmne.salestracker.api.model.RemarksListResponse;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityRemarkListBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 13-09-2016.
 */
public class RemarkListActivity extends AppCompatActivity {

    private ActivityRemarkListBinding activityRemarkListBinding;
    private ArrayList<Remark> remarkModelList;
    private RemarkListAdapter remarkListAdapter;
    private String actionlog;
    private String[] actionlogSplit;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRemarkListBinding = DataBindingUtil.setContentView(this, R.layout.activity_remark_list);

        init();

    }

    private void init() {

        getIntentData();

        if (activityRemarkListBinding.toolbarLayout.toolbar != null)
            activityRemarkListBinding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(activityRemarkListBinding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityRemarkListBinding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        activityRemarkListBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.remark_title));

        activityRemarkListBinding.swipeRefresh.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);

        initRecyclerView();

        actionListener();
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

    private void getIntentData() {
        actionlog = getIntent().getStringExtra("actionlog");
        actionlogSplit = actionlog.split("_");
        Log.e("id", actionlogSplit[1]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void actionListener() {
        activityRemarkListBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getRemarkList();
                        activityRemarkListBinding.swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });

    }

    private void getRemarkList() {

        showProgress(getString(R.string.loading));

        remarkModelList = new ArrayList<>();

        new CallWebService(this, AppConstants.ActionLogRemarksList + actionlogSplit[1], CallWebService.TYPE_GET) {
            @Override
            public void response(String response) {

                dismissProgress();

                RemarksListResponse remarksListResponse = MyApplication.getGson().fromJson(response, RemarksListResponse.class);

                if (remarksListResponse != null) {

                    Log.e("remarksListResponse", MyApplication.getGson().toJson(remarksListResponse));

                    if (remarksListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        remarkListAdapter.setRemarkList(remarksListResponse.getData().getRemarks());
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, RemarkListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(RemarkListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void initRecyclerView() {

        remarkModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activityRemarkListBinding.agentRecyclerView.setLayoutManager(layoutManager);
        activityRemarkListBinding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        remarkListAdapter = new RemarkListAdapter(this, remarkModelList);
        activityRemarkListBinding.agentRecyclerView.setAdapter(remarkListAdapter);
        activityRemarkListBinding.agentRecyclerView.setHasFixedSize(true);

        getRemarkList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRemarkListBinding.unbind();
    }


}
