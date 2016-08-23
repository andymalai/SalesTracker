package com.webmne.salestracker.actionlog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.AgentAdapter;
import com.webmne.salestracker.actionlog.adapter.DepartmentAdapter;
import com.webmne.salestracker.actionlog.adapter.InChargeAdapter;
import com.webmne.salestracker.actionlog.model.Department;
import com.webmne.salestracker.actionlog.model.InCharge;
import com.webmne.salestracker.agent.model.AgentModel;
import com.webmne.salestracker.databinding.ActivityAddActionLogBinding;

import java.io.File;
import java.util.ArrayList;

public class AddActionLogActivity extends AppCompatActivity implements FileChooserDialog.FileCallback {

    ActivityAddActionLogBinding binding;
    private ArrayList<AgentModel> agentList;
    private ArrayList<Department> deptList;
    private ArrayList<InCharge> inChargeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_action_log);

        init();
    }

    private void init() {
        if (binding.toolbarLayout.toolbar != null)
            binding.toolbarLayout.toolbar.setTitle("");
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_action_log_title));

        setAgentAdapter();

        setDepartmentAdapter();

        setDepartmentInchargeAdapter();

        binding.edtSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooserDialog.Builder(AddActionLogActivity.this)
                        .initialPath("/sdcard")  // changes initial path, defaults to external storage directory
                        .tag("optional-identifier")
                        .show();
            }
        });
    }

    private void setDepartmentInchargeAdapter() {
        inChargeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            InCharge incharge = new InCharge();
            incharge.setInChargeName("PIC " + i);
            incharge.setInChargeId(i);
            inChargeList.add(incharge);
        }
        binding.spinnerInCharge.setAdapter(new InChargeAdapter(this, R.layout.item_adapter, inChargeList));
    }

    private void setDepartmentAdapter() {
        deptList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Department dept = new Department();
            dept.setDepartmentName("Department " + i);
            dept.setDepartmentId(i);
            deptList.add(dept);
        }
        binding.spinnerDepartment.setAdapter(new DepartmentAdapter(this, R.layout.item_adapter, deptList));
    }

    private void setAgentAdapter() {
        agentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AgentModel model = new AgentModel();
            model.setAgentName("Agent " + i);
            agentList.add(model);
        }
        binding.spinnerAgent.setAdapter(new AgentAdapter(this, R.layout.item_adapter, agentList));
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        final String tag = dialog.getTag();
        binding.edtSelectFile.setText(file.getName());
    }
}
