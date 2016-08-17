package com.webmne.salestracker.ui.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.ui.dashboard.adapter.DashboardAdapter;
import com.webmne.salestracker.ui.dashboard.model.HomeTileBean;
import com.webmne.salestracker.ui.dashboard.model.HomeTileConfiguration;
import com.webmne.salestracker.ui.profile.UserProfileActivity;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DashboadActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.imgLogout)
    ImageView imgLogout;

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ArrayList<HomeTileBean> tiles;
    private DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboad);
        unbinder = ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {
        if (toolbar != null)
            toolbar.setTitle("");
        setSupportActionBar(toolbar);

        txtCustomTitle.setText("Welcome User");

        initRecylerView();
    }

    private void initRecylerView() {
        tiles = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DashboardAdapter(DashboadActivity.this, tiles);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

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

    @OnClick({R.id.imgProfile, R.id.imgLogout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgProfile:
                Functions.fireIntent(this, UserProfileActivity.class);
                break;

            case R.id.imgLogout:
                Functions.showPrompt(this, "Yes", "No", getString(R.string.logout_text), new Functions.onPromptListener() {
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
                break;
        }
    }
}
