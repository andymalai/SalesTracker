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
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.ui.dashboard.adapter.DashboardAdapter;
import com.webmne.salestracker.ui.dashboard.model.HomeTileBean;
import com.webmne.salestracker.ui.dashboard.model.HomeTileConfiguration;
import com.webmne.salestracker.ui.profile.UserProfileActivity;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void init() {
        if (dashboadBinding.toolbarLayout.toolbar != null)
            dashboadBinding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(dashboadBinding.toolbarLayout.toolbar);

        // change message runtime, welcome <username>
        dashboadBinding.toolbarLayout.txtCustomTitle.setText(String.format("%s, %s", "Welcome", PrefUtils.getUserProfile(this).getFirstName()));

        initRecylerView();

        actionListener();
    }

    private void actionListener() {
        dashboadBinding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(DashboardActivity.this, UserProfileActivity.class);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
            }
        });

        dashboadBinding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.showPrompt(DashboardActivity.this, "Yes", "No", getString(R.string.logout_text), new Functions.onPromptListener() {
                    @Override
                    public void onClickYes(MaterialDialog dialog) {
                        // close session
                        Functions.closeSession(DashboardActivity.this);
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
        adapter = new DashboardAdapter(DashboardActivity.this, tiles);
        dashboadBinding.recyclerView.setAdapter(adapter);
        dashboadBinding.recyclerView.setHasFixedSize(true);

        final HomeTileConfiguration configuration = new HomeTileConfiguration();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // pass role id
                tiles = configuration.getDashboardOptions(PrefUtils.getUserProfile(DashboardActivity.this).getPos_name());
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
