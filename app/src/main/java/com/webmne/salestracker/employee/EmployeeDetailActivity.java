package com.webmne.salestracker.employee;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Response;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddEmployeeBinding;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.employee.model.PositionModel;
import com.webmne.salestracker.event.PositionListResponse;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 27-09-2016.
 */

public class EmployeeDetailActivity extends AppCompatActivity {

    private boolean isEditMode = false;

    private ActivityAddEmployeeBinding viewBinding;

    private ArrayList<String> positionList;
    private ArrayList<PositionModel> positionArrayList;
    private int positionWhich = 0;

    private ArrayList<Integer> selectedEmplyeeId;

    String selectedPositionID;

    private LoadingIndicatorDialog dialog;
    private String employee;
    private EmployeeModel employeeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_employee);

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

        employee = getIntent().getStringExtra("employee");
        employeeModel = MyApplication.getGson().fromJson(employee, EmployeeModel.class);

        viewBinding.edtEmpId.setText(String.format("%s", employeeModel.getEmpId()));
        viewBinding.edtName.setText(String.format("%s", employeeModel.getName()));
        viewBinding.edtPhoneNumber.setText(String.format("%s", employeeModel.getPhone()));
        viewBinding.edtEmailId.setText(String.format("%s", employeeModel.getEmailId()));

        getPositions();

        actionListener();
    }

    private void getPositions() {
        positionList = new ArrayList<>();

        if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS)) {
            positionList.add(AppConstants.MARKETER);
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.BM)) {
            positionList.add(AppConstants.MARKETER);
            positionList.add(AppConstants.HOS);
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS)) {
            positionList.add(AppConstants.MARKETER);
            positionList.add(AppConstants.HOS);
            positionList.add(AppConstants.BM);
        }

        if (Functions.isConnected(this)) {
            fetchPosition();
        } else {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }
    }

    private void actionListener() {

        viewBinding.edtPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(EmployeeDetailActivity.this)
                        .title(getString(R.string.select_position))
                        .items(positionList)
                        .typeface(Functions.getBoldFont(EmployeeDetailActivity.this), Functions.getRegularFont(EmployeeDetailActivity.this))
                        .itemsCallbackSingleChoice(positionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                positionWhich = which;

                                for (int i = 0; i < positionArrayList.size(); i++) {
                                    if (text.equals(positionArrayList.get(i).getPositionName())) {
                                        selectedPositionID = positionArrayList.get(i).getPositionId();
                                        break;
                                    }
                                }

                                viewBinding.edtPosition.setText(text);

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();

            }
        });

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

                            deleteEmployee();

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
                    doUpdate();

                }

            }
        });

    }

    private void enableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_cancel));
        viewBinding.btnAdd.setText(getString(R.string.btn_save));

        viewBinding.edtEmpId.setFocusableInTouchMode(true);
        viewBinding.edtName.setFocusableInTouchMode(true);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(true);
        viewBinding.edtEmailId.setFocusableInTouchMode(true);

        viewBinding.edtPosition.setClickable(true);
    }

    private void disableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_delete));
        viewBinding.btnAdd.setText(getString(R.string.btn_edit));

        viewBinding.edtEmpId.setFocusableInTouchMode(false);
        viewBinding.edtName.setFocusableInTouchMode(false);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(false);
        viewBinding.edtEmailId.setFocusableInTouchMode(false);

        viewBinding.edtEmpId.setFocusable(false);
        viewBinding.edtName.setFocusable(false);
        viewBinding.edtPhoneNumber.setFocusable(false);
        viewBinding.edtEmailId.setFocusable(false);

        viewBinding.edtPosition.setClickable(false);
    }

    private void doUpdate() {

        if (!Functions.isConnected(EmployeeDetailActivity.this)) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEmpId))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_employee_id), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtName))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_employee_name), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPosition))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.select_position), getString(R.string.fa_error));
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

        updateEvent();
    }

    private void updateEvent() {

        showProgress(getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("UserId", employeeModel.getId());
            json.put("EmpId", Functions.toStr(viewBinding.edtEmpId));
            json.put("Position", selectedPositionID);
            json.put("Name", Functions.toStr(viewBinding.edtName));
            json.put("Phone", Functions.toStr(viewBinding.edtPhoneNumber));
            json.put("Email", Functions.toStr(viewBinding.edtEmailId));
            json.put("Region", PrefUtils.getUserProfile(this).getRegionId());
            json.put("Branch", PrefUtils.getUserProfile(this).getBranch());
            json.put("ParentId", PrefUtils.getUserId(this));

            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.UpdateEmployee, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Response updateEmployeeResponse = MyApplication.getGson().fromJson(response, Response.class);

                if (updateEmployeeResponse != null) {

                    Log.e("add_res", MyApplication.getGson().toJson(updateEmployeeResponse));

                    if (updateEmployeeResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(EmployeeDetailActivity.this, getString(R.string.update_event_success));
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        Log.e("response", response);
                    } else {
                        SimpleToast.error(EmployeeDetailActivity.this, updateEmployeeResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                }

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

    private void deleteEmployee() {

        showProgress(getString(R.string.delete_employee));

        selectedEmplyeeId = new ArrayList<>();

        selectedEmplyeeId.add(Integer.valueOf(employeeModel.getId()));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EmpId", new JSONArray(selectedEmplyeeId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("delete_req", jsonObject.toString());

        new CallWebService(this, AppConstants.DeleteEmployee, CallWebService.TYPE_POST, jsonObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response deleteResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                Log.e("delete_res", MyApplication.getGson().toJson(deleteResponse));

                if (deleteResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                    SimpleToast.ok(EmployeeDetailActivity.this, getString(R.string.delete_success));
                    finish();
                } else {
                    SimpleToast.error(EmployeeDetailActivity.this, deleteResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                }
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

    private void fetchPosition() {

        showProgress(getString(R.string.loading));

        new CallWebService(this, AppConstants.Position, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                PositionListResponse positionListResponse = MyApplication.getGson().fromJson(response, PositionListResponse.class);

                if (positionListResponse != null) {

                    if (positionListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        positionArrayList = new ArrayList<>();
                        positionArrayList = positionListResponse.getData().getPosition();

                        for (int i = 0; i < positionArrayList.size(); i++) {
                            if (employeeModel.getPosition().equals(positionArrayList.get(i).getPositionId())) {
                                viewBinding.edtPosition.setText(positionArrayList.get(i).getPositionName());

                                for (int j = 0; j < positionList.size(); j++) {
                                    if (positionArrayList.get(i).getPositionName().equals(positionList.get(j))) {
                                        positionWhich = j;
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    } else {
                        SimpleToast.error(EmployeeDetailActivity.this, positionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

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

        disableFields();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showProgress(String string) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, string, android.R.style.Theme_Translucent_NoTitleBar);
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
