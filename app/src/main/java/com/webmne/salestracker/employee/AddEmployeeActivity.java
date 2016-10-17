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

/**
 * Created by vatsaldesai on 26-09-2016.
 */

public class AddEmployeeActivity extends AppCompatActivity {

    private ActivityAddEmployeeBinding viewBinding;
    private LoadingIndicatorDialog dialog;

    private ArrayList<Branch> branchArrayList = new ArrayList<>();
    private int branchWhich = 0;
    private StringBuilder stringBuilderBranch;

    private ArrayList<Region> regionArrayList;
    private int regionWhich = 0;

    private ArrayList<PositionModel> positionArrayList;

    //    private ArrayList<PositionModel> positionArrayList;
    private int positionWhich = 0;
//    private StringBuilder stringBuilderPosition;

    private String strRegion = "0";

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

        // call all ws one by one
        fetchRegion();

        actionListener();
    }

    private void getPositions() {
        positionArrayList = new ArrayList<>();

        if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS)) {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.BM)) {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
            positionArrayList.add(new PositionModel(null, AppConstants.HOS));
        } else if (PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.HOS)) {
            positionArrayList.add(new PositionModel(null, AppConstants.MARKETER));
            positionArrayList.add(new PositionModel(null, AppConstants.HOS));
            positionArrayList.add(new PositionModel(null, AppConstants.BM));
        }
    }

    private void actionListener() {

        viewBinding.edtRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(AddEmployeeActivity.this)
                        .title(getString(R.string.select_region))
                        .items(regionArrayList)
                        .typeface(Functions.getBoldFont(AddEmployeeActivity.this), Functions.getRegularFont(AddEmployeeActivity.this))
                        .itemsCallbackSingleChoice(regionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                regionWhich = which;

                                viewBinding.edtRegion.setText(text.toString().replace("[", "").replace("]", ""));

                                strRegion = regionArrayList.get(which).getRegion();

                                viewBinding.edtBranch.setText("");
                                branchWhich = 0;

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

                new MaterialDialog.Builder(AddEmployeeActivity.this)
                        .title(getString(R.string.select_branch))
                        .items(branchArrayList)
                        .typeface(Functions.getBoldFont(AddEmployeeActivity.this), Functions.getRegularFont(AddEmployeeActivity.this))
                        .itemsCallbackSingleChoice(regionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                branchWhich = which;
                                viewBinding.edtBranch.setText(text.toString().replace("[", "").replace("]", ""));

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();

            }
        });

        viewBinding.edtPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(AddEmployeeActivity.this)
                        .title(getString(R.string.select_position))
                        .items(positionArrayList)
                        .typeface(Functions.getBoldFont(AddEmployeeActivity.this), Functions.getRegularFont(AddEmployeeActivity.this))
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


//                                MaterialDialog.ListCallbackMultiChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
//
//                                positionWhich = which;
//
////                                stringBuilderPosition = new StringBuilder();
////
////                                for (int i = 0; i < which.length; i++) {
////
////                                    stringBuilderPosition.append(positionArrayList.get(which[i]).getPositionId());
////                                    stringBuilderPosition.append(",");
////                                }
//
//                                viewBinding.edtPosition.setText(Arrays.toString(text).replace("[", "").replace("]", ""));
//
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.btn_ok))
//                        .show();

            }
        });

        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Functions.isConnected(AddEmployeeActivity.this)) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtName))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.enter_employee_name), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtRegion))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.select_region), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtBranch))) {
                    SimpleToast.error(AddEmployeeActivity.this, getString(R.string.select_branch), getString(R.string.fa_error));
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

//                doAddAgent();

            }
        });

    }

    private void doAddAgent() {

        showProgress(getString(R.string.loading));

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
//                        SimpleToast.ok(AddEmployeeActivity.this, getString(R.string.add_agent_success));
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//
//                    } else {
//                        SimpleToast.error(AddEmployeeActivity.this, addAgentResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
//                }
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
                        SimpleToast.error(AddEmployeeActivity.this, regionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
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
                        SimpleToast.error(AddEmployeeActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
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
