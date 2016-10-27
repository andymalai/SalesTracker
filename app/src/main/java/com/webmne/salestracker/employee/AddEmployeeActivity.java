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
import com.webmne.salestracker.employee.model.PositionModel;
import com.webmne.salestracker.event.PositionListResponse;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 26-09-2016.
 */

public class AddEmployeeActivity extends AppCompatActivity {

    private ActivityAddEmployeeBinding viewBinding;
    private LoadingIndicatorDialog dialog;

    private ArrayList<PositionModel> positionList;
    private ArrayList<PositionModel> positionArrayList;
    private int positionWhich = 0;

    String selectedPositionID;

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

        getPositions();

        actionListener();
    }

    private void getPositions() {
        positionList = new ArrayList<>();

        if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS)) {
            positionList.add(new PositionModel(null, AppConstants.MARKETER));
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.BM)) {
            positionList.add(new PositionModel(null, AppConstants.MARKETER));
            positionList.add(new PositionModel(null, AppConstants.HOS));
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.RM)) {
            positionList.add(new PositionModel(null, AppConstants.MARKETER));
            positionList.add(new PositionModel(null, AppConstants.HOS));
            positionList.add(new PositionModel(null, AppConstants.BM));
        }

        if (Functions.isConnected(this)) {
            fetchPosition();
        } else {
            SimpleToast.error(AddEmployeeActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }
    }

    private void actionListener() {

        viewBinding.edtPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(AddEmployeeActivity.this)
                        .title(getString(R.string.select_position))
                        .items(positionList)
                        .typeface(Functions.getBoldFont(AddEmployeeActivity.this), Functions.getRegularFont(AddEmployeeActivity.this))
                        .itemsCallbackSingleChoice(positionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                positionWhich = which;

                                for(int i=0; i<positionArrayList.size(); i++)
                                {
                                    if(text.equals(positionArrayList.get(i).getPositionName()))
                                    {
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

        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Functions.isConnected(AddEmployeeActivity.this)) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEmpId))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_employee_id), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtName))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_employee_name), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPosition))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.select_position), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPhoneNumber))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_phone), getString(R.string.fa_error));
                    return;
                }

                if (Functions.toLength(viewBinding.edtPhoneNumber) < 10) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_valid_phone), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_email_id), getString(R.string.fa_error));
                    return;
                }

                if (!Functions.emailValidation(Functions.toStr(viewBinding.edtEmailId))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_valid_email_id), getString(R.string.fa_error));
                    return;
                }

                AddEmployee();

            }
        });

    }

    private void AddEmployee() {

        showProgress(getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("EmpId", Functions.toStr(viewBinding.edtEmpId));
            json.put("Position", selectedPositionID);
            json.put("Name", Functions.toStr(viewBinding.edtName));
            json.put("Phone", Functions.toStr(viewBinding.edtPhoneNumber));
            json.put("Email", Functions.toStr(viewBinding.edtEmailId));
            json.put("Region", PrefUtils.getUserProfile(this).getRegionId());
            json.put("Branch", PrefUtils.getUserProfile(this).getBranch());
            json.put("ParentId", PrefUtils.getUserId(this));
            json.put("Department", "0");
            json.put("Userpic", "0");
            json.put("DepartmentPIC", "0");
            json.put("RoleDescription", "0");
            json.put("PositionName", "0");
            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.AddEmployee, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Response addEmployeeResponse = MyApplication.getGson().fromJson(response, Response.class);

                if (addEmployeeResponse != null) {
                    Log.e("add_res", MyApplication.getGson().toJson(addEmployeeResponse));

                    if (addEmployeeResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(AddEmployeeActivity.this, getString(R.string.add_employee_success));
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    } else {
                        SimpleToast.error(AddEmployeeActivity.this, addEmployeeResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEmployeeActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEmployeeActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

                    } else {
                        SimpleToast.error(AddEmployeeActivity.this, positionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEmployeeActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEmployeeActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();

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
