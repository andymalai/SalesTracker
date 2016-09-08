package com.webmne.salestracker.actionlog;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.actionlog.adapter.AgentAdapter;
import com.webmne.salestracker.actionlog.adapter.DepartmentAdapter;
import com.webmne.salestracker.actionlog.adapter.InChargeAdapter;
import com.webmne.salestracker.actionlog.model.Department;
import com.webmne.salestracker.actionlog.model.InCharge;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AgentListApi;
import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddActionLogBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.PrefUtils;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Response;

public class AddActionLogActivity extends AppCompatActivity {

    ActivityAddActionLogBinding binding;
    private ArrayList<AgentModel> agentList;
    private ArrayList<Department> deptList;
    private ArrayList<InCharge> inChargeList;
    private static final int FILE_SELECT_CODE = 0;
    private AgentListApi agentListApi;
    private LoadingIndicatorDialog dialog;
    private AgentAdapter agentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_action_log);

        agentListApi = new AgentListApi();

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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_action_log_title));

        if (Functions.isConnected(this)) {
            getAgents();
        } else {
            SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getAgents() {
        showProgress();

        agentList = new ArrayList<>();

        agentListApi.getAgents(PrefUtils.getUserId(this), new APIListener<AgentListResponse>() {
            @Override
            public void onResponse(Response<AgentListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    AgentListResponse listResponse = response.body();
                    if (listResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        agentList.addAll(listResponse.getData().getAgents());
                        agentAdapter = new AgentAdapter(AddActionLogActivity.this, R.layout.item_adapter, agentList);
                        binding.spinnerAgent.setAdapter(agentAdapter);
                    } else {
                        SimpleToast.error(AddActionLogActivity.this, listResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void onFailure(Call<AgentListResponse> call, Throwable t) {
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(AddActionLogActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
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
            model.setName("Agent " + i);
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

    public void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, "Loading..", android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
