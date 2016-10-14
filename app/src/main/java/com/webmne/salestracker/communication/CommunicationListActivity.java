package com.webmne.salestracker.communication;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.CommunicationResponse;
import com.webmne.salestracker.communication.adapter.CommunicationListAdapter;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityCommunicationListBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import java.util.ArrayList;

public class CommunicationListActivity extends AppCompatActivity {

    private CommunicationListAdapter adapter;
    private ArrayList<Communication> communicationList;

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
        fetchCommunication();

        actionListener();
    }

    private void fetchCommunication() {

        showProgress(getString(R.string.loading));

        communicationList = new ArrayList<>();

        new CallWebService(this, AppConstants.CommunicationList, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                CommunicationResponse communicationResponse = MyApplication.getGson().fromJson(response, CommunicationResponse.class);

                if (communicationResponse != null) {

                    if (communicationResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        adapter.setCommunicationData(communicationResponse.getCommunication());

                    } else {
                        SimpleToast.error(CommunicationListActivity.this, communicationResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, CommunicationListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(CommunicationListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
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
                        fetchCommunication();
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
