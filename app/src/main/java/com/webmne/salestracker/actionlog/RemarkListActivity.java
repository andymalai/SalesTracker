package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.RemarkListAdapter;
import com.webmne.salestracker.actionlog.model.RemarkModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.ActivityRemarkListBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by vatsaldesai on 13-09-2016.
 */
public class RemarkListActivity extends AppCompatActivity{

    private ActivityRemarkListBinding activityRemarkListBinding;
    private ArrayList<RemarkModel> remarkModelList;
    private RemarkListAdapter remarkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRemarkListBinding = DataBindingUtil.setContentView(this, R.layout.activity_remark_list);

        init();

    }

    private void init()
    {
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

        getRemarkList();

        actionListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void actionListener()
    {
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
        remarkModelList = new ArrayList<>();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy h:mm aa");

        for (int i = 1; i <= 10; i++) {
            RemarkModel model = new RemarkModel();
            model.setName("Name " + i);
            model.setPosition("BM");
            model.setDetail(getString(R.string.dummy));
            model.setDate(sdf.format(date));
            remarkModelList.add(model);
        }

        remarkListAdapter.setRemarkList(remarkModelList);
    }

    private void initRecyclerView() {
        remarkModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activityRemarkListBinding.agentRecyclerView.setLayoutManager(layoutManager);
        activityRemarkListBinding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        remarkListAdapter = new RemarkListAdapter(this, remarkModelList);
        activityRemarkListBinding.agentRecyclerView.setAdapter(remarkListAdapter);
        activityRemarkListBinding.agentRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRemarkListBinding.unbind();
    }


}
