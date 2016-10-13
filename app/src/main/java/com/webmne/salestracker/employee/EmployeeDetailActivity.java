package com.webmne.salestracker.employee;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.adapter.BranchAdapter;
import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.api.BranchApi;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddEmployeeBinding;
import com.webmne.salestracker.employee.adapter.PositionAdapter;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.employee.model.PositionModel;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by vatsaldesai on 27-09-2016.
 */

public class EmployeeDetailActivity extends AppCompatActivity {

    private boolean isEditMode = false;

    private ActivityAddEmployeeBinding viewBinding;
    private BranchApi branchApi;
    private PositionAdapter positionAdapter;
    private BranchAdapter branchAdapter;
    private LoadingIndicatorDialog dialog;
    private int branchId = 0;
    private AppApi appApi;
    private String positionName,employee;
    private EmployeeModel employeeModel;

    //  private ArrayList<TierModel> tierModels;
    private ArrayList<Branch> branchModels;
    private AdapterView.OnItemSelectedListener branchSelectListener;
    private AdapterView.OnItemSelectedListener positionSelectListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_employee);

        branchApi = new BranchApi();
        appApi = MyApplication.getRetrofit().create(AppApi.class);

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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_employee_title));

        viewBinding.txtCancel.setVisibility(View.VISIBLE);
        viewBinding.btnAdd.setText(getString(R.string.btn_edit));

//        disableFields();

        employee = getIntent().getStringExtra("employee");
        employeeModel = MyApplication.getGson().fromJson(employee, EmployeeModel.class);

        viewBinding.edtName.setText(String.format("%s", employeeModel.getName()));
        viewBinding.edtPhoneNumber.setText(String.format("%s", employeeModel.getPhone()));
        viewBinding.edtEmailId.setText(String.format("%s", employeeModel.getEmail()));

        getPositions();

        if (Functions.isConnected(this)) {

            // call all ws one by one
            fetchBranches();

        } else {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

        actionListener();
    }

    private void getPositions()
    {
        ArrayList<PositionModel> positionModelList = new ArrayList<>();

        if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS))
        {
            positionModelList.add(new PositionModel(null, AppConstants.MARKETER));
        }
        else if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.BM))
        {
            positionModelList.add(new PositionModel(null, AppConstants.MARKETER));
            positionModelList.add(new PositionModel(null, AppConstants.HOS));
        }
        else if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS))
        {
            positionModelList.add(new PositionModel(null, AppConstants.MARKETER));
            positionModelList.add(new PositionModel(null, AppConstants.HOS));
            positionModelList.add(new PositionModel(null, AppConstants.BM));
        }

        positionAdapter = new PositionAdapter(this, R.layout.item_adapter, positionModelList);
        viewBinding.spinnerPosition.setAdapter(positionAdapter);
        viewBinding.spinnerPosition.setOnItemSelectedListener(positionSelectListener);
    }

    private void actionListener() {
        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Functions.isConnected(EmployeeDetailActivity.this)) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtName))) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_employee_name), getString(R.string.fa_error));
                    return;
                }

                if (branchId == 0) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.select_branch), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPhoneNumber))) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_phone), getString(R.string.fa_error));
                    return;
                }

                if (Functions.toLength(viewBinding.edtPhoneNumber) < 10) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_valid_phone), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_email_id), getString(R.string.fa_error));
                    return;
                }

                if (!Functions.emailValidation(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_valid_email_id), getString(R.string.fa_error));
                    return;
                }

//                doAddAgent();

            }
        });

        positionSelectListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PositionModel positionModel = positionAdapter.getItem(position);
                positionName = positionModel.getPositionName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        branchSelectListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Branch branch = branchAdapter.getItem(position);
                branchId = Integer.parseInt(branch.getBranchId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        viewBinding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEditMode) {
                    disableFields();
                    isEditMode = !isEditMode;
                } else {

                    Functions.showPrompt(EmployeeDetailActivity.this, "Yes", "No", getString(R.string.employee_delete_dialog), new Functions.onPromptListener() {
                        @Override
                        public void onClickYes(MaterialDialog dialog) {

//                            deleteAgent();

                        }

                        @Override
                        public void onClickNo(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    });

                }

            }
        });

        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEditMode = !isEditMode;
                if (isEditMode) {
                    enableFields();

                } else {
//                    doUpdateProfile();

                }

            }
        });

    }

    private void enableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_cancel));
        viewBinding.btnAdd.setText(getString(R.string.btn_save));

        viewBinding.edtName.setFocusableInTouchMode(true);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(true);
        viewBinding.edtEmailId.setFocusableInTouchMode(true);

        branchAdapter.setCanOpen(true);
        positionAdapter.setCanOpen(true);
    }

    private void disableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_delete));
        viewBinding.btnAdd.setText(getString(R.string.btn_edit));

        viewBinding.edtName.setFocusableInTouchMode(false);
        viewBinding.spinnerBranch.setFocusableInTouchMode(false);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(false);
        viewBinding.edtEmailId.setFocusableInTouchMode(false);

        viewBinding.edtName.setFocusable(false);
        viewBinding.spinnerBranch.setFocusable(false);
        viewBinding.edtPhoneNumber.setFocusable(false);
        viewBinding.edtEmailId.setFocusable(false);

        branchAdapter.setCanOpen(false);
        positionAdapter.setCanOpen(false);
    }

    private void doAddAgent() {

        showProgress();

        JSONObject json = new JSONObject();
        try {
//            json.put("AgentName", Functions.toStr(viewBinding.edtAgentName));
//            json.put("AmgCode", Functions.toStr(viewBinding.edtAmgGeneral));
//            json.put("BranchId", branchId);
//            json.put("Description", Functions.toStr(viewBinding.edtDescription));
//            json.put("EmailId", Functions.toStr(viewBinding.edtEmailId));
//            json.put("KruniaCode", Functions.toStr(viewBinding.edtKruniaCode));
//            json.put("MobileNo", Functions.toStr(viewBinding.edtPhoneNumber));
//            json.put("TierId", tierId);
//            json.put("UserId", PrefUtils.getUserId(this));
//            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.AddAgent, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

//                AddAgentResponse addAgentResponse = MyApplication.getGson().fromJson(response, AddAgentResponse.class);
//
//                if (addAgentResponse != null) {
//                    Log.e("add_res", MyApplication.getGson().toJson(addAgentResponse));
//
//                    if (addAgentResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
//                        SimpleToast.ok(EmployeeDetailActivity.this, getString(R.string.add_agent_success));
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//
//                    } else {
//                        SimpleToast.error(EmployeeDetailActivity.this, addAgentResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
//                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, EmployeeDetailActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void fetchBranches() {
        showProgress();
        branchApi.getBranchList(new APIListener<BranchListResponse>() {
            @Override
            public void onResponse(Response<BranchListResponse> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    BranchListResponse branchListResponse = response.body();
                    if (branchListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        branchAdapter = new BranchAdapter(EmployeeDetailActivity.this, R.layout.item_adapter, branchListResponse.getData().getBranches());
                        viewBinding.spinnerBranch.setAdapter(branchAdapter);
                        viewBinding.spinnerBranch.setOnItemSelectedListener(branchSelectListener);

                        disableFields();

                    } else {
                        SimpleToast.error(EmployeeDetailActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.try_again), getString(R.string.fa_error));

                }
            }

            @Override
            public void onFailure(Call<BranchListResponse> call, Throwable t) {
                dismissProgress();
                if (t instanceof TimeoutException) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.time_out), getString(R.string.fa_error));
                } else if (t instanceof UnknownHostException) {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                } else {
                    SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }
        });
    }

    public void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, "Please wait..", android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewBinding.unbind();
    }
}
