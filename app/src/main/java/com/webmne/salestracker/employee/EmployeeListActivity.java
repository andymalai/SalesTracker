package com.webmne.salestracker.employee;

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
import com.webmne.salestracker.databinding.ActivityEmployeeListBinding;
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

public class EmployeeListActivity extends AppCompatActivity {

    private EmployeeListAdapter adapter;
    private ArrayList<EmployeeModel> employeeList;
    private MenuItem searchItem;

    private ActivityEmployeeListBinding viewBinding;
    private LoadingIndicatorDialog dialog;
    private ArrayList<Integer> selectedEmployeeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_employee_list);
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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.employee_title));

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
            getAgents();
        else
            SimpleToast.error(EmployeeListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));

        actionListener();
    }

    private void actionListener() {
        viewBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.setEmployeeList(employeeList);
                } else {
                    adapter.searchFilter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.setEmployeeList(employeeList);
                } else {
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        viewBinding.agentRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
                intent.putExtra("employee", MyApplication.getGson().toJson(adapter.getEmployeeList().get(position)));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewBinding.agentRecyclerView.setOnScrollListener(new FamiliarRecyclerViewOnScrollListener(viewBinding.agentRecyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {

            }

            @Override
            public void onScrolledToBottom() {
                Toast.makeText(EmployeeListActivity.this, "Load more..", Toast.LENGTH_SHORT).show();
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
                        getAgents();
                        viewBinding.swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });

        viewBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(EmployeeListActivity.this, AddEmployeeActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewBinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("selected", selectedEmployeeIds.toString() + "  #");

//                deleteAgents();
            }

        });
    }

    private void deleteAgents() {
        showProgress(getString(R.string.delete_employee));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AgentID", new JSONArray(selectedEmployeeIds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("delete_req", jsonObject.toString());

        new CallWebService(this, AppConstants.DeleteAgent, CallWebService.TYPE_POST, jsonObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response deleteResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                Log.e("delete_res", deleteResponse.toString());

                if (deleteResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                    SimpleToast.ok(EmployeeListActivity.this, getString(R.string.delete_success));
                    viewBinding.txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    viewBinding.searchView.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.colorPrimaryDark));
                    }
                    getAgents();
                } else {
                    SimpleToast.error(EmployeeListActivity.this, deleteResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, EmployeeListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(EmployeeListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    public void onBackPressed() {
        if (viewBinding.searchView.isSearchOpen()) {
            viewBinding.searchView.closeSearch();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void getAgents() {

//        showProgress(getString(R.string.loading));
//
//        viewBinding.contentLayout.setVisibility(View.GONE);

        employeeList = new ArrayList<>();
        adapter.setEmployeeList(employeeList);


        //////////// SET STATIC EMPLOYEE DATA /////////////////////
        for (int i = 0; i < 10; i++) {
            EmployeeModel model = new EmployeeModel();
            model.setEmpId(String.valueOf(i));
            model.setName("Employee " + i);
            model.setBranch("Ankleshwar");
            model.setRegion("Gujarat");
            model.setEmail("vat@gmail.com");
            model.setPhone("8563245674");
            model.setPosition("DM");
            employeeList.add(model);
        }
        adapter.setEmployeeList(employeeList);
        ////////////////////////////////////////////////////////


//        new CallWebService(this, AppConstants.EmployeeList, CallWebService.TYPE_GET) {
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
//                VolleyErrorHelper.showErrorMsg(error, EmployeeListActivity.this);
//            }
//
//            @Override
//            public void noInternet() {
//                dismissProgress();
//                SimpleToast.error(EmployeeListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
//            }
//        }.call();

    }

    private void initRecyclerView() {
        employeeList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.agentRecyclerView.setLayoutManager(layoutManager);
        viewBinding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.agentRecyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Employee Found.", R.drawable.ic_agent);

        adapter = new EmployeeListAdapter(EmployeeListActivity.this, employeeList, new EmployeeListAdapter.onSelectionListener() {
            @Override
            public void onSelect() {

                if (getSelectedItems() == 0) {
                    viewBinding.txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    viewBinding.searchView.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.colorPrimaryDark));
                    }

                } else {
                    viewBinding.searchView.setVisibility(View.GONE);
                    searchItem.setVisible(false);
                    viewBinding.txtDelete.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.tile1));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(EmployeeListActivity.this, R.color.tile2));
                    }
                }
            }

        });
        viewBinding.agentRecyclerView.setAdapter(adapter);

        viewBinding.agentRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        searchItem = menu.findItem(R.id.action_search);
        viewBinding.searchView.setMenuItem(searchItem);

        viewBinding.searchView.setVoiceSearch(true);
        viewBinding.searchView.setHint(getString(R.string.search_employee));
        return true;
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

    private int getSelectedItems() {
        selectedEmployeeIds = new ArrayList<>();
        int selected = 0;
        for (EmployeeModel model : employeeList) {
            if (model.isChecked()) {
                selectedEmployeeIds.add(Integer.valueOf(model.getEmpId()));
                selected++;
            }
        }
        Log.e("selectedEmployeeIds", selectedEmployeeIds.toString());
        return selected;
    }
}
