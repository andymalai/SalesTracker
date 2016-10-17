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
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddEmployeeBinding;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.employee.model.PositionModel;
import com.webmne.salestracker.event.model.Region;
import com.webmne.salestracker.event.model.RegionListResponse;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Response;

/**
 * Created by vatsaldesai on 27-09-2016.
 */

public class EmployeeDetailActivity extends AppCompatActivity {

    private boolean isEditMode = false;

    private ActivityAddEmployeeBinding viewBinding;

    private ArrayList<Branch> branchArrayList = new ArrayList<>();
    private Integer[] branchWhich;
    private StringBuilder stringBuilderBranch;

    private ArrayList<Region> regionArrayList;
    private int regionWhich = 0;

    private ArrayList<PositionModel> positionArrayList;

    //    private ArrayList<PositionModel> positionArrayList;
    private int positionWhich = 0;
//    private StringBuilder stringBuilderPosition;

    private String strRegion = "0";

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

        viewBinding.edtName.setText(String.format("%s", employeeModel.getName()));
        viewBinding.edtPhoneNumber.setText(String.format("%s", employeeModel.getPhone()));
        viewBinding.edtEmailId.setText(String.format("%s", employeeModel.getEmailId()));

        getPositions();

        if (Functions.isConnected(this)) {

            // call all ws one by one
            fetchRegion();

        } else {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
        }

        actionListener();
    }

    private void getPositions()
    {
        positionArrayList = new ArrayList<>();

        if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS))
        {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
        }
        else if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.BM))
        {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
            positionArrayList.add(new PositionModel(null, AppConstants.HOS));
        }
        else if(PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS))
        {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
            positionArrayList.add(new PositionModel(null, AppConstants.HOS));
            positionArrayList.add(new PositionModel(null, AppConstants.BM));
        }
    }

    private void actionListener() {

        viewBinding.edtRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(EmployeeDetailActivity.this)
                        .title(getString(R.string.select_region))
                        .items(regionArrayList)
                        .typeface(Functions.getBoldFont(EmployeeDetailActivity.this), Functions.getRegularFont(EmployeeDetailActivity.this))
                        .itemsCallbackSingleChoice(regionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                regionWhich = which;

                                viewBinding.edtRegion.setText(text.toString().replace("[", "").replace("]", ""));

                                strRegion = regionArrayList.get(which).getRegion();

                                viewBinding.edtBranch.setText("");
                                branchWhich = null;

                                fetchBranches();

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();

            }
        });

        viewBinding.edtBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(EmployeeDetailActivity.this)
                        .title(getString(R.string.select_branch))
                        .items(branchArrayList)
                        .typeface(Functions.getBoldFont(EmployeeDetailActivity.this), Functions.getRegularFont(EmployeeDetailActivity.this))
                        .itemsCallbackMultiChoice(branchWhich, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                                branchWhich = which;

                                stringBuilderBranch = new StringBuilder();

                                for (int i = 0; i < which.length; i++) {

                                    stringBuilderBranch.append(branchArrayList.get(which[i]).getBranchId());
                                    stringBuilderBranch.append(",");
                                }

                                viewBinding.edtBranch.setText(Arrays.toString(text).replace("[", "").replace("]", ""));

                                return true;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();

            }
        });

        viewBinding.edtPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(EmployeeDetailActivity.this)
                        .title(getString(R.string.select_position))
                        .items(positionArrayList)
                        .typeface(Functions.getBoldFont(EmployeeDetailActivity.this), Functions.getRegularFont(EmployeeDetailActivity.this))
                        .itemsCallbackSingleChoice(positionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                positionWhich = which;


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

                doUpdate();

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

//                            deleteEmployee();

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

        viewBinding.edtRegion.setClickable(true);
        viewBinding.edtBranch.setClickable(true);
        viewBinding.edtPosition.setClickable(true);
    }

    private void disableFields() {
        viewBinding.txtCancel.setText(getString(R.string.btn_delete));
        viewBinding.btnAdd.setText(getString(R.string.btn_edit));

        viewBinding.edtName.setFocusableInTouchMode(false);
        viewBinding.edtPhoneNumber.setFocusableInTouchMode(false);
        viewBinding.edtEmailId.setFocusableInTouchMode(false);

        viewBinding.edtName.setFocusable(false);
        viewBinding.edtPhoneNumber.setFocusable(false);
        viewBinding.edtEmailId.setFocusable(false);

        viewBinding.edtRegion.setClickable(false);
        viewBinding.edtBranch.setClickable(false);
        viewBinding.edtPosition.setClickable(false);
    }

    private void doUpdate() {

        if (!Functions.isConnected(EmployeeDetailActivity.this)) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtName))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.enter_employee_name), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtRegion))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.select_region), getString(R.string.fa_error));
            return;
        }

        if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtBranch))) {
            SimpleToast.error(EmployeeDetailActivity.this, getString(R.string.select_branch), getString(R.string.fa_error));
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

//        updateEvent();
    }

    private void updateEvent() {

        showProgress(getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            // json.put("Id", employeeModel.getEmpId());
            json.put("UserId", PrefUtils.getUserId(this));
//            json.put("Date", Functions.toStr(viewBinding.edtDate));
//            json.put("Title", Functions.toStr(viewBinding.edtEventName));
//            json.put("Description", Functions.toStr(viewBinding.edtDescription));
            json.put("RegionId", strRegion);
            json.put("BranchId", stringBuilderBranch.toString());
//            json.put("RoleId", stringBuilderPosition.toString());

            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.UpdateEmployee, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Response addEventResponse = MyApplication.getGson().fromJson(response, Response.class);

                if (addEventResponse != null) {

//                    if (addEventResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
//                        SimpleToast.ok(EmployeeDetailActivity.this, getString(R.string.update_event_success));
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                        Log.e("response", response);
//                    } else {
//                        SimpleToast.error(EmployeeDetailActivity.this, addEventResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
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

        showProgress(getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
          //  json.put("Id", employeeModel.getEmpId());

            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.DeleteEmployee, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Response deleteEventResponse = MyApplication.getGson().fromJson(response, Response.class);

                if (deleteEventResponse != null) {

//                    if (deleteEventResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
//                        SimpleToast.ok(EmployeeDetailActivity.this, getString(R.string.add_event_success));
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                        Log.e("response", response);
//                    } else {
//                        SimpleToast.error(EmployeeDetailActivity.this, deleteEventResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void fetchRegion() {

        showProgress(getString(R.string.loading));

        new CallWebService(this, AppConstants.Region, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                RegionListResponse regionListResponse = MyApplication.getGson().fromJson(response, RegionListResponse.class);

                if (regionListResponse != null) {

                    if (regionListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        regionArrayList = new ArrayList<>();
                        regionArrayList = regionListResponse.getData().getBranches();

                        fetchBranches();

                    } else {
                        SimpleToast.error(EmployeeDetailActivity.this, regionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
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

    }

    private void fetchBranches() {

        showProgress(getString(R.string.loading));

        Log.e("branch", AppConstants.Branch + "&regionid=" + strRegion);

        new CallWebService(this, AppConstants.Branch + "&regionid=" + strRegion, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                BranchListResponse branchListResponse = MyApplication.getGson().fromJson(response, BranchListResponse.class);

                if (branchListResponse != null) {

                    if (branchListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        branchArrayList = new ArrayList<>();
                        branchArrayList = branchListResponse.getData().getBranches();

                    } else {
                        SimpleToast.error(EmployeeDetailActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
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
