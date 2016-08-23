package com.webmne.salestracker.agent;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.AgentsListAdapter;
import com.webmne.salestracker.agent.model.AgentModel;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.widget.TfTextView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AgentsListActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.agentRecyclerView)
    FamiliarRecyclerView agentRecyclerView;

    Unbinder unbinder;
    @BindView(R.id.relativeLayout)
    LinearLayout relativeLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.txtDelete)
    TfTextView txtDelete;
    @BindView(R.id.emptyTextView)
    TfTextView emptyTextView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private AgentsListAdapter adapter;
    private ArrayList<AgentModel> agentList;
    private boolean isDeleteMode = false;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agents_list);
        unbinder = ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (toolbar != null)
            toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtCustomTitle.setText(getString(R.string.agent_title));

        swipeRefresh.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);

        initRecyclerView();

        getAgents();

        actionListener();
    }

    private void actionListener() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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

        agentRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Functions.fireIntent(AgentsListActivity.this, AgentProfileActivity.class);
            }
        });

        agentRecyclerView.setOnScrollListener(new FamiliarRecyclerViewOnScrollListener(agentRecyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {

            }

            @Override
            public void onScrolledToBottom() {
                Toast.makeText(AgentsListActivity.this, "Load more..", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getAgents();
                        swipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void getAgents() {
        agentList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            AgentModel agent = new AgentModel();
            agent.setAgentId(i);
            agent.setAgentName("Agent " + i);
            agent.setColor(ContextCompat.getColor(this, R.color.tile2));
            agentList.add(agent);
        }
        adapter.setAgentList(agentList);
    }

    private void initRecyclerView() {
        agentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        agentRecyclerView.setLayoutManager(layoutManager);
        agentRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
        adapter = new AgentsListAdapter(this, agentList, new AgentsListAdapter.onSelectionListener() {
            @Override
            public void onSelect(boolean isSelect) {
                if (isSelect) {
                    searchItem.setVisible(false);
                    txtDelete.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile1));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.tile2));
                    }
                } else {
                    txtDelete.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    searchView.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(ContextCompat.getColor(AgentsListActivity.this, R.color.colorPrimaryDark));
                    }
                }
                /*int selected = PrefUtils.getDeleteAgents(AgentsListActivity.this).size();

                Log.e("selected", selected + " ###");

                if (selected == 0) {
                    isDeleteMode = false;
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                } else {
                    isDeleteMode = true;
                    toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
                }*/
            }

        });
        agentRecyclerView.setAdapter(adapter);
        agentRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.fab)
    public void onClick() {
        Functions.fireIntent(this, AddAgentActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(searchItem);

        searchView.setVoiceSearch(true);
        searchView.setHint(getString(R.string.search));
        return true;
    }
}
