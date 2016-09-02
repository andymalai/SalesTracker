package com.webmne.salestracker.agent;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.AgentsListAdapter;
import com.webmne.salestracker.agent.model.AgentModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.databinding.ActivityAgentsListBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;

import butterknife.OnClick;

public class AgentsListActivity extends AppCompatActivity {

    private AgentsListAdapter adapter;
    private ArrayList<AgentModel> agentList;
    private boolean isDeleteMode = false;
    private MenuItem searchItem;

    private ActivityAgentsListBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_agents_list);

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
            }
        });

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.agent_title));

        viewBinding.swipeRefresh.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);

        initRecyclerView();

        getAgents();

        actionListener();
    }

    private void actionListener() {
        viewBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(AgentsListActivity.this, "Do search " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        viewBinding.agentRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Functions.fireIntent(AgentsListActivity.this, AgentProfileActivity.class);
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
                new CountDownTimer(3000, 1000) {
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
    }

    @Override
    public void onBackPressed() {
        if (viewBinding.searchView.isSearchOpen()) {
            viewBinding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void getAgents() {

        agentList = new ArrayList<>();
        /*for (int i = 1; i <= 20; i++) {
            AgentModel agent = new AgentModel();
            agent.setAgentId(i);
            agent.setAgentName("Agent " + i);
            agent.setAgentContactNo("9429841325");
            agent.setAgentEmail("sagar@webmyne.com");
            agent.setColor(ContextCompat.getColor(this, R.color.tile2));
            agentList.add(agent);
        }
        adapter.setAgentList(agentList);*/

        if (agentList.size() == 0) {

        }
    }

    private void initRecyclerView() {
        agentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.agentRecyclerView.setLayoutManager(layoutManager);
        viewBinding.agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));

        viewBinding.agentRecyclerView.setEmptyView(viewBinding.emptyLayout);
        viewBinding.emptyLayout.setContent("No Agents", R.drawable.ic_agent);

        adapter = new AgentsListAdapter(this, agentList, new AgentsListAdapter.onSelectionListener() {
            @Override
            public void onSelect(boolean isSelect) {

                if (isSelect) {
                    searchItem.setVisible(false);
                    viewBinding.txtDelete.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile1));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile2));
                    }
                } else {
                    viewBinding.txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    viewBinding.searchView.setVisibility(View.VISIBLE);
                    viewBinding.toolbarLayout.toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimaryDark));
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

    @OnClick(R.id.fab)
    public void onClick() {
        Functions.fireIntent(this, AddAgentActivity.class);
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
}
