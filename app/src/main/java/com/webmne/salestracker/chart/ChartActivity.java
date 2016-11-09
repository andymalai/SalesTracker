package com.webmne.salestracker.chart;

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
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityChartBinding;
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

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityChartBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_chart);
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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.chart_title));

        actionListener();

    }

    private void actionListener() {
        viewBinding.txtBranch.setOnClickListener(this);
        viewBinding.txtDept.setOnClickListener(this);
        viewBinding.txtDeptSla.setOnClickListener(this);
        viewBinding.txtVisitPlan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ChartContentActivity.class);
        switch (v.getId()) {
            case R.id.txtBranch:
                intent.putExtra(AppConstants.CHART_TYPE, AppConstants.BRANCH_CHART);
                break;

            case R.id.txtDept:
                intent.putExtra(AppConstants.CHART_TYPE, AppConstants.DEPARTMENT_CHART);
                break;

            case R.id.txtDeptSla:
                intent.putExtra(AppConstants.CHART_TYPE, AppConstants.DEPARTMENT_SLA_CHART);
                break;

            case R.id.txtVisitPlan:
                intent.putExtra(AppConstants.CHART_TYPE, AppConstants.VISIT_PLAN_CHART);
                break;
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
