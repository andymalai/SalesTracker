package com.webmne.salestracker.agent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.BranchAdapter;
import com.webmne.salestracker.agent.adapter.TierAdapter;
import com.webmne.salestracker.agent.model.BranchModel;
import com.webmne.salestracker.agent.model.TierModel;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddAgentActivity extends AppCompatActivity {

    @BindView(R.id.txtCustomTitle)
    TfTextView txtCustomTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtAgentName)
    TfEditText edtAgentName;
    @BindView(R.id.spinnerTier)
    AppCompatSpinner spinnerTier;
    @BindView(R.id.spinnerBranch)
    AppCompatSpinner spinnerBranch;
    @BindView(R.id.edtPhoneNumber)
    TfEditText edtPhoneNumber;
    @BindView(R.id.edtEmailId)
    TfEditText edtEmailId;
    @BindView(R.id.edtKruniaCode)
    TfEditText edtKruniaCode;
    @BindView(R.id.edtAmgGeneral)
    TfEditText edtAmgGeneral;
    @BindView(R.id.relativeLayout)
    LinearLayout relativeLayout;

    Unbinder unbinder;
    @BindView(R.id.edtDescription)
    TfEditText edtDescription;
    @BindView(R.id.btnAdd)
    TfButton btnAdd;

    private ArrayList<TierModel> tierModels;
    private ArrayList<BranchModel> branchModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);
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

        txtCustomTitle.setText(getString(R.string.add_agent));

        setTierAdapter();

        setBranchAdapter();

    }

    private void setBranchAdapter() {
        // fetch branches
        branchModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BranchModel model = new BranchModel(i, "Branch " + (i + 1));
            branchModels.add(model);
        }

        // set branches to adapter
        spinnerBranch.setAdapter(new BranchAdapter(AddAgentActivity.this, R.layout.item_adapter, branchModels));
    }

    private void setTierAdapter() {
        // get tier list
        tierModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TierModel model = new TierModel(i, "Tier " + (i + 1));
            tierModels.add(model);
        }

        // set tiers to adapter
        spinnerTier.setAdapter(new TierAdapter(AddAgentActivity.this, R.layout.item_adapter, tierModels));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
