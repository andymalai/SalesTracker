package com.webmne.salestracker.event;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.EventListAdapter;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityEventListBinding;
import com.webmne.salestracker.employee.EmployeeDetailActivity;
import com.webmne.salestracker.event.model.EventModel;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private EventListAdapter adapter;
    private ArrayList<EventModel> eventList;

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
                Intent intent = new Intent(EventListActivity.this, EmployeeDetailActivity.class);
                intent.putExtra("event", MyApplication.getGson().toJson(eventList.get(position)));
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

//        showProgress(getString(R.string.loading));
//
//        viewBinding.contentLayout.setVisibility(View.GONE);

        eventList = new ArrayList<>();
        adapter.setEventList(eventList);


        //////////// SET STATIC EVENT DATA /////////////////////
        for (int i = 0; i < 10; i++) {
            EventModel model = new EventModel();
            model.setName("Event " + i);
            model.setDesc("Description");
            model.setCreateDate("2016/11/22 08:18:17");
            eventList.add(model);
        }
        adapter.setEventList(eventList);
        ////////////////////////////////////////////////////////


//        new CallWebService(this, AppConstants.EventList, CallWebService.TYPE_GET) {
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
//                VolleyErrorHelper.showErrorMsg(error, EventListActivity.this);
//            }
//
//            @Override
//            public void noInternet() {
//                dismissProgress();
//                SimpleToast.error(EventListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
//            }
//        }.call();

    }

    private void initRecyclerView() {
        eventList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.recyclerView.setLayoutManager(layoutManager);
        viewBinding.recyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.recyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Event Found.", R.drawable.ic_action_event);

        adapter = new EventListAdapter(EventListActivity.this, eventList, new EventListAdapter.onClickListener() {
            @Override
            public void onClick(int position) {

                Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
                intent.putExtra("event", MyApplication.getGson().toJson(eventList.get(position)));
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
