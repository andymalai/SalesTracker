package com.webmne.salestracker.actionlog;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.AgentAdapter;
import com.webmne.salestracker.actionlog.adapter.DepartmentAdapter;
import com.webmne.salestracker.actionlog.adapter.InChargeAdapter;
import com.webmne.salestracker.actionlog.model.Department;
import com.webmne.salestracker.actionlog.model.InCharge;
import com.webmne.salestracker.agent.model.AgentModel;
import com.webmne.salestracker.databinding.ActivityAddActionLogBinding;
import com.webmne.salestracker.helper.Functions;

import java.io.File;
import java.util.ArrayList;

public class AddActionLogActivity extends AppCompatActivity {

    ActivityAddActionLogBinding binding;
    private ArrayList<AgentModel> agentList;
    private ArrayList<Department> deptList;
    private ArrayList<InCharge> inChargeList;
    private static final int FILE_SELECT_CODE = 0;

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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(AddActionLogActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            // Get the path
            File file = new File(uri.getPath());
            binding.edtSelectFile.setText(file.getName());
            String path = Functions.getPath(this, uri);
        }
    }
}
