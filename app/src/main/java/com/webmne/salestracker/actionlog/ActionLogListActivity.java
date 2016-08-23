package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.LogListAdapter;
import com.webmne.salestracker.actionlog.model.ActionLogModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.ActivityActionLogListBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import java.util.ArrayList;

public class ActionLogListActivity extends AppCompatActivity {

    private ActivityActionLogListBinding binding;
    private ArrayList<ActionLogModel> actionLogList;
    private LogListAdapter adapter;

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

    private void actionListener() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(ActionLogListActivity.this, AddActionLogActivity.class);
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
            }
        });
    }

    private void getActionLogList() {
        actionLogList = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            ActionLogModel model = new ActionLogModel();
            model.setAgentName("Agent " + i);
            model.setDateRaised("18-8-2016");
            model.setDepartment("Hub Operations");
            model.setDescription("Description");
            model.setLastUpdate("22-8-2016");
            model.setSLA(2);
            if (i % 2 == 0) {
                model.setCompleted(true);
            } else {
                model.setCompleted(false);
            }
            actionLogList.add(model);
        }

        adapter.setActionList(actionLogList);

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
}
