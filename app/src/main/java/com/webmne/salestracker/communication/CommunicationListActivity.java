package com.webmne.salestracker.communication;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.webmne.salestracker.R;
import com.webmne.salestracker.communication.adapter.CommunicationListAdapter;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityCommunicationListBinding;
import com.webmne.salestracker.databinding.ActivityEmployeeListBinding;
import com.webmne.salestracker.employee.AddEmployeeActivity;
import com.webmne.salestracker.employee.EmployeeDetailActivity;
import com.webmne.salestracker.employee.adapter.EmployeeListAdapter;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommunicationListActivity extends AppCompatActivity {

    private CommunicationListAdapter adapter;
    private ArrayList<CommunicationModel> communicationList;

    private ActivityCommunicationListBinding viewBinding;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_communication_list);
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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.communication_list_title));

        viewBinding.swipeRefresh.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);

        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Functions.isConnected(this))
            getCommunication();
        else
            SimpleToast.error(CommunicationListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));

        actionListener();
    }

    private void actionListener() {

        viewBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getCommunication();
                        viewBinding.swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getCommunication() {

//        showProgress(getString(R.string.loading));
//
//        viewBinding.contentLayout.setVisibility(View.GONE);

        communicationList = new ArrayList<>();
        adapter.setEmployeeList(communicationList);


        //////////// SET STATIC COMMUNICATION DATA /////////////////////
        for (int i = 0; i < 6; i++) {
            CommunicationModel model = new CommunicationModel();
            model.setTitle("Test "+i);
            model.setDesc("description");
            communicationList.add(model);
        }
        adapter.setEmployeeList(communicationList);
        ////////////////////////////////////////////////////////


//        new CallWebService(this, AppConstants.CommunicationList, CallWebService.TYPE_GET) {
//
//            @Override
//            public void response(String response) {
//
//                dismissProgress();
//
//                viewBinding.contentLayout.setVisibility(View.VISIBLE);
//
////                ActionLogListResponse getActionLogListResponse = MyApplication.getGson().fromJson(response, EmployeeListActivity.class);
////
////                if (getActionLogListResponse != null) {
////
////                    if (getActionLogListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
////                        actionLogList = getActionLogListResponse.getData().getAction();
////                        adapter.setActionList(actionLogList);
////
////                    } else {
////                        SimpleToast.error(EmployeeListActivity.this, getActionLogListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
////                    }
////
////                } else {
////                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
////                }
//
//            }
//
//            @Override
//            public void error(VolleyError error) {
//                dismissProgress();
//                viewBinding.contentLayout.setVisibility(View.VISIBLE);
//                VolleyErrorHelper.showErrorMsg(error, CommunicationListActivity.this);
//            }
//
//            @Override
//            public void noInternet() {
//                dismissProgress();
//                SimpleToast.error(CommunicationListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
//            }
//        }.call();

    }

    private void initRecyclerView() {
        communicationList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.recyclerView.setLayoutManager(layoutManager);
        viewBinding.recyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.recyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Communication Found.", R.drawable.ic_communication_chat);

        adapter = new CommunicationListAdapter(CommunicationListActivity.this, communicationList, new CommunicationListAdapter.onClickListener() {
            @Override
            public void onClick() {

                Toast.makeText(CommunicationListActivity.this, "Click For Download...", Toast.LENGTH_SHORT).show();

            }
        });

        viewBinding.recyclerView.setAdapter(adapter);

        viewBinding.recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
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
