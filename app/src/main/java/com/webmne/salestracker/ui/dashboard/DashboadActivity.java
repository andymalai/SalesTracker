package com.webmne.salestracker.ui.dashboard;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.databinding.ActivityDashboadBinding;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.ui.dashboard.adapter.DashboardAdapter;
import com.webmne.salestracker.ui.dashboard.model.HomeTileBean;
import com.webmne.salestracker.ui.dashboard.model.HomeTileConfiguration;
import com.webmne.salestracker.ui.profile.UserProfileActivity;

import java.util.ArrayList;

public class DashboadActivity extends AppCompatActivity {

    ActivityDashboadBinding dashboadBinding;

    private ArrayList<HomeTileBean> tiles;
    private DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboadBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboad);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dashboadBinding.unbind();
    }

    private void init() {
        if (dashboadBinding.toolbarLayout.toolbar != null)
            dashboadBinding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(dashboadBinding.toolbarLayout.toolbar);

        // change message runtime, welcome <username>
        dashboadBinding.toolbarLayout.txtCustomTitle.setText("Welcome User");

        initRecylerView();

        actionListener();
    }

    private void actionListener() {
        dashboadBinding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(DashboadActivity.this, UserProfileActivity.class);
            }
        });

        dashboadBinding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.showPrompt(DashboadActivity.this, "Yes", "No", getString(R.string.logout_text), new Functions.onPromptListener() {
                    @Override
                    public void onClickYes(MaterialDialog dialog) {
                        // close session
                        finish();
                    }

                    @Override
                    public void onClickNo(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void initRecylerView() {
        tiles = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        dashboadBinding.recyclerView.setLayoutManager(layoutManager);
        adapter = new DashboardAdapter(DashboadActivity.this, tiles);
        dashboadBinding.recyclerView.setAdapter(adapter);
        dashboadBinding.recyclerView.setHasFixedSize(true);

        final HomeTileConfiguration configuration = new HomeTileConfiguration(this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // pass role id
                tiles = configuration.getDashboardOptions(1);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.setTiles(tiles);
            }
        }.execute();

    }
}
