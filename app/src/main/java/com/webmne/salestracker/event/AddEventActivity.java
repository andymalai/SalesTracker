package com.webmne.salestracker.event;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.Response;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityAddEventBinding;
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
import java.util.Calendar;

/**
 * Created by vatsaldesai on 26-09-2016.
 */

public class AddEventActivity extends AppCompatActivity {

    private ActivityAddEventBinding viewBinding;
    private LoadingIndicatorDialog dialog;
    private DatePickerDialog datePickerDialog;

    private ArrayList<Branch> branchArrayList = new ArrayList<>();
    private Integer[] branchWhich;
    private StringBuilder stringBuilderBranch;

    private ArrayList<Region> regionArrayList;
    private int regionWhich = 0;

    private ArrayList<PositionModel> positionArrayList;
    private Integer[] positionWhich;
    private StringBuilder stringBuilderPosition;

    private String strRegion = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_event);

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

        viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.add_event_title));

//        if (Functions.isConnected(this)) {
//
//            // call all ws one by one
//            fetchRegion();
//
//        } else {
//            SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
//        }

        actionListener();
    }

    private void actionListener() {

        viewBinding.edtRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(AddEventActivity.this)
                        .title(getString(R.string.select_region))
                        .items(regionArrayList)
                        .typeface(Functions.getBoldFont(AddEventActivity.this), Functions.getRegularFont(AddEventActivity.this))
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

                new MaterialDialog.Builder(AddEventActivity.this)
                        .title(getString(R.string.select_branch))
                        .items(branchArrayList)
                        .typeface(Functions.getBoldFont(AddEventActivity.this), Functions.getRegularFont(AddEventActivity.this))
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

                new MaterialDialog.Builder(AddEventActivity.this)
                        .title(getString(R.string.select_position))
                        .items(positionArrayList)
                        .typeface(Functions.getBoldFont(AddEventActivity.this), Functions.getRegularFont(AddEventActivity.this))
                        .itemsCallbackMultiChoice(positionWhich, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                                positionWhich = which;

                                stringBuilderPosition = new StringBuilder();

                                for (int i = 0; i < which.length; i++) {

                                    stringBuilderPosition.append(positionArrayList.get(which[i]).getPositionId());
                                    stringBuilderPosition.append(",");
                                }

                                viewBinding.edtPosition.setText(Arrays.toString(text).replace("[", "").replace("]", ""));

                                return true;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();

            }
        });

        viewBinding.edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        viewBinding.edtDate.setText(String.format("%d-%d-%d", year, monthOfYear, dayOfMonth));

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        viewBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Functions.isConnected(AddEventActivity.this)) {
                    SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtEventName))) {
                    SimpleToast.error(AddEventActivity.this, getString(R.string.enter_event_name), getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtDate))) {
                    SimpleToast.error(AddEventActivity.this, getString(R.string.enter_date), getString(R.string.fa_error));
                    return;
                }

//                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtRegion))) {
//                    SimpleToast.error(AddEventActivity.this, getString(R.string.select_region), getString(R.string.fa_error));
//                    return;
//                }
//
//                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtBranch))) {
//                    SimpleToast.error(AddEventActivity.this, getString(R.string.select_branch), getString(R.string.fa_error));
//                    return;
//                }
//
//                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtPosition))) {
//                    SimpleToast.error(AddEventActivity.this, getString(R.string.select_position), getString(R.string.fa_error));
//                    return;
//                }

                if (TextUtils.isEmpty(Functions.toStr(viewBinding.edtDescription))) {
                    SimpleToast.error(AddEventActivity.this, getString(R.string.enter_description), getString(R.string.fa_error));
                    return;
                }

                addEvent();

            }
        });

    }

    private void addEvent() {

        showProgress(getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("Date", Functions.toStr(viewBinding.edtDate));
            json.put("Title", Functions.toStr(viewBinding.edtEventName));
            json.put("Description", Functions.toStr(viewBinding.edtDescription));
            json.put("RegionId", strRegion);
            json.put("BranchId", stringBuilderBranch.toString());
            json.put("RoleId", stringBuilderPosition.toString());

            Log.e("add_req", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.AddEvent, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Response addEventResponse = MyApplication.getGson().fromJson(response, Response.class);

                if (addEventResponse != null) {

                    if (addEventResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(AddEventActivity.this, getString(R.string.add_event_success));
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        Log.e("response", response);
                    } else {
                        SimpleToast.error(AddEventActivity.this, addEventResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEventActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

                        fetchPosition();

                    } else {
                        SimpleToast.error(AddEventActivity.this, regionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEventActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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
                        SimpleToast.error(AddEventActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEventActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

                        fetchBranches();

                    } else {
                        SimpleToast.error(AddEventActivity.this, positionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, AddEventActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(AddEventActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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
