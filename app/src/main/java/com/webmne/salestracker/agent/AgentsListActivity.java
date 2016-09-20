package com.webmne.salestracker.agent;

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
import com.webmne.salestracker.agent.adapter.AgentsListAdapter;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AgentListApi;
import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAgentsListBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.RetrofitErrorHelper;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class AgentsListActivity extends AppCompatActivity {

    private AgentsListAdapter adapter;
    private ArrayList<AgentModel> agentList;
    private MenuItem searchItem;

    private ActivityAgentsListBinding viewBinding;
    private AgentListApi agentListApi;
    private LoadingIndicatorDialog dialog;
    private ArrayList<Integer> selectedAgentIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_agents_list);
        agentListApi = new AgentListApi();
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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.agent_title));

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
            SimpleToast.error(AgentsListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));

        actionListener();
    }

    private void actionListener() {
        viewBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.setAgentList(agentList);
                } else {
                    adapter.searchFilter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.setAgentList(agentList);
                } else {
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        viewBinding.agentRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Intent intent = new Intent(AgentsListActivity.this, AgentProfileActivity.class);
                intent.putExtra("agent", MyApplication.getGson().toJson(adapter.getAgentList().get(position)));
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
                Toast.makeText(AgentsListActivity.this, "Load more..", Toast.LENGTH_SHORT).show();
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
                Functions.fireIntent(AgentsListActivity.this, AddAgentActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewBinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("selected", selectedAgentIds.toString() + "  #");

                deleteAgents();
            }

        });
    }

    private void deleteAgents() {
        showProgress(getString(R.string.delete_agents));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AgentID", new JSONArray(selectedAgentIds));
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
                    SimpleToast.ok(AgentsListActivity.this, getString(R.string.delete_success));
                    viewBinding.txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    viewBinding.searchView.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimaryDark));
                    }
                    getAgents();
                } else {
                    SimpleToast.error(AgentsListActivity.this, deleteResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AgentsListActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AgentsListActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

        viewBinding.contentLayout.setVisibility(View.GONE);
        showProgress(getString(R.string.loading_agents));

        agentList = new ArrayList<>();
        adapter.setAgentList(agentList);

        agentListApi.getAgents(PrefUtils.getUserId(this), new APIListener<AgentListResponse>() {
            @Override
            public void onResponse(Response<AgentListResponse> response) {
                dismissProgress();

                viewBinding.contentLayout.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    AgentListResponse listResponse = response.body();
                    if (listResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        agentList.addAll(listResponse.getData().getAgents());
                        adapter.setAgentList(listResponse.getData().getAgents());

                    } else {
                        SimpleToast.error(AgentsListActivity.this, listResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AgentsListActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<AgentListResponse> call, Throwable t) {
                dismissProgress();
                viewBinding.contentLayout.setVisibility(View.VISIBLE);
                RetrofitErrorHelper.showErrorMsg(t, AgentsListActivity.this);
            }
        });
    }

    private void initRecyclerView() {
        agentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.agentRecyclerView.setLayoutManager(layoutManager);
        viewBinding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.agentRecyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Agents Found.", R.drawable.ic_agent);

        adapter = new AgentsListAdapter(this, agentList, new AgentsListAdapter.onSelectionListener() {
            @Override
            public void onSelect() {

                if (getSelectedItems() == 0) {
                    viewBinding.txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    viewBinding.searchView.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimaryDark));
                    }

                } else {
                    viewBinding.searchView.setVisibility(View.GONE);
                    searchItem.setVisible(false);
                    viewBinding.txtDelete.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile1));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile2));
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
        agentListApi.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        searchItem = menu.findItem(R.id.action_search);
        viewBinding.searchView.setMenuItem(searchItem);

        viewBinding.searchView.setVoiceSearch(true);
        viewBinding.searchView.setHint(getString(R.string.search));
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
        selectedAgentIds = new ArrayList<>();
        int selected = 0;
        for (AgentModel model : agentList) {
            if (model.isChecked()) {
                selectedAgentIds.add(Integer.valueOf(model.getAgentid()));
                selected++;
            }
        }
        Log.e("selectedAgentIds", selectedAgentIds.toString());
        return selected;
    }
}
