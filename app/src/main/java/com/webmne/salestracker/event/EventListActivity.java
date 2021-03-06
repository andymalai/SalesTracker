package com.webmne.salestracker.event;

import android.content.Intent;
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
import com.webmne.salestracker.actionlog.adapter.EventListAdapter;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityEventListBinding;
import com.webmne.salestracker.employee.EmployeeDetailActivity;
import com.webmne.salestracker.event.model.Event;
import com.webmne.salestracker.event.model.EventListMainResponse;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private EventListAdapter adapter;
//    private ArrayList<Event> eventList;
    private ArrayList<Event> mainEventList;

    private ActivityEventListBinding viewBinding;
    private LoadingIndicatorDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);
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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.event_list_title));

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
            getEvents();
        else
            SimpleToast.error(EventListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));

        actionListener();
    }

    private void actionListener() {

        viewBinding.recyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
                intent.putExtra("event", MyApplication.getGson().toJson(mainEventList.get(position)));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getEvents();
                        viewBinding.swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });

        viewBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(EventListActivity.this, AddEventActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getEvents() {

        showProgress(getString(R.string.loading));

        viewBinding.contentLayout.setVisibility(View.INVISIBLE);

        mainEventList = new ArrayList<>();
        adapter.setEventList(mainEventList);

        new CallWebService(this, AppConstants.EventList, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                viewBinding.contentLayout.setVisibility(View.VISIBLE);

                EventListMainResponse eventListModel = MyApplication.getGson().fromJson(response, EventListMainResponse.class);

                if (eventListModel != null) {

                    if (eventListModel.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        mainEventList = eventListModel.getEvents();

//                        for (int i = 0; i < mainEventList.size(); i++) {
//                            if (mainEventList.get(i).getBranchId().contains(PrefUtils.getUserProfile(EventListActivity.this).getBranch())
//                                    && mainEventList.get(i).getRoleId().contains(PrefUtils.getUserProfile(EventListActivity.this).getRoleid())) {
//                                eventList.add(mainEventList.get(i));
//                            }
//                        }

                        adapter.setEventList(mainEventList);

                    } else {
                        SimpleToast.error(EventListActivity.this, eventListModel.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                viewBinding.contentLayout.setVisibility(View.VISIBLE);
                VolleyErrorHelper.showErrorMsg(error, EventListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(EventListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();

    }

    private void initRecyclerView() {
        mainEventList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.recyclerView.setLayoutManager(layoutManager);
        viewBinding.recyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.recyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Event Found.", R.drawable.ic_action_event);

        adapter = new EventListAdapter(EventListActivity.this, mainEventList, new EventListAdapter.onClickListener() {
            @Override
            public void onClick(int position) {

                Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
                intent.putExtra("event", MyApplication.getGson().toJson(mainEventList.get(position)));
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
