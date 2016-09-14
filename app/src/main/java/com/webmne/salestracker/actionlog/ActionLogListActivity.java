package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.LogListAdapter;
import com.webmne.salestracker.actionlog.model.ActionLogListModel;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityActionLogListBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActionLogListActivity extends AppCompatActivity {

    private ActivityActionLogListBinding binding;
    private ArrayList<ActionLogModel> actionLogList;
    private LogListAdapter adapter;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_action_log_list);

        init();

    }

    private void init() {
        if (binding.toolbarLayout.toolbar != null)
            binding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.action_log_title));

        binding.swipeRefresh.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);

        initRecyclerView();

        getActionLogList();

        actionListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void actionListener() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(ActionLogListActivity.this, AddActionLogActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getActionLogList();
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });

        binding.agentRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Functions.fireIntent(ActionLogListActivity.this, ActionLogDetailsActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private void getActionLogList()
    {
        showProgress(getString(R.string.delete_agents));

        actionLogList = new ArrayList<>();

        Log.e("action_log_list_url", AppConstants.ActionLogList + PrefUtils.getUserId(this));

        new CallWebService(this, AppConstants.ActionLogList + PrefUtils.getUserId(this), CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                Log.e("getResponse", response);

                dismissProgress();

                ActionLogListModel getActionLogListResponse = MyApplication.getGson().fromJson(response, ActionLogListModel.class);

                if (getActionLogListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS))
                {
                    actionLogList = getActionLogListResponse.getData().getAction();

                    for (int i = 0; i < actionLogList.size(); i++)
                    {
                        ActionLogModel model = new ActionLogModel();
                        model.setId(actionLogList.get(i).getId());
                        model.setAgentName(actionLogList.get(i).getAgentName());
                        model.setDescription(actionLogList.get(i).getDescription());
                        model.setCreatedDatetime(actionLogList.get(i).getCreatedDatetime());
                        model.setStatus(actionLogList.get(i).getStatus());
                        model.setDepartmentName(actionLogList.get(i).getDepartmentName());
                        model.setSla(actionLogList.get(i).getSla());
                        model.setReopen(actionLogList.get(i).getReopen());
                        model.setUpdatedDatetime(actionLogList.get(i).getUpdatedDatetime());
                        model.setAttachmentPath(actionLogList.get(i).getAttachmentPath());
                        model.setAttachment(actionLogList.get(i).getAttachment());
                        model.setRemarkCount(actionLogList.get(i).getRemarkCount());
                        actionLogList.add(model);
                    }

                    adapter.setActionList(actionLogList);
                } 
                else 
                {
                    SimpleToast.error(ActionLogListActivity.this, getActionLogListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ActionLogListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ActionLogListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();


//        actionLogList = new ArrayList<>();
//
//        for (int i = 1; i < 10; i++) {
//            ActionLogModel model = new ActionLogModel();
//            model.setAgentName("Agent " + i);
//            model.setDateRaised("18-8-2016");
//            model.setDepartment("Hub Operations");
//            model.setDescription(getString(R.string.dummy));
//            model.setCountRemark(10);
//            model.setLastUpdate("22-8-2016");
//            model.setSLA(2);
//            if (i % 2 == 0) {
//                model.setCompleted(true);
//            } else {
//                model.setCompleted(false);
//            }
//            actionLogList.add(model);
//        }
//
//        adapter.setActionList(actionLogList);

    }

    private void initRecyclerView() {
        actionLogList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.agentRecyclerView.setLayoutManager(layoutManager);
        binding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        adapter = new LogListAdapter(this, actionLogList);
        binding.agentRecyclerView.setAdapter(adapter);
        binding.agentRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
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
