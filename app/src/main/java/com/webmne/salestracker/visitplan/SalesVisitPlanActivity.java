package com.webmne.salestracker.visitplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.DatePlan;
import com.webmne.salestracker.api.model.FetchMappingData;
import com.webmne.salestracker.api.model.FetchMappingResponse;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.api.model.FetchRecruitmentResponse;
import com.webmne.salestracker.api.model.PlanDataResponse;
import com.webmne.salestracker.api.model.SalesPlanResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivitySalesVisitPlanBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.adapter.CustomDialogVisitPlanAgentListAdapter;
import com.webmne.salestracker.visitplan.dialogs.MappingDialog;
import com.webmne.salestracker.visitplan.dialogs.RecruitmentDialog;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.calendar.CalendarView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class SalesVisitPlanActivity extends AppCompatActivity {

    private ActivitySalesVisitPlanBinding binding;
    private MenuItem addPlanItem;
    private LoadingIndicatorDialog dialog;

    private FamiliarRecyclerView familiarRecyclerView;
    private CustomDialogVisitPlanAgentListAdapter customDialogVisitPlanAgentListAdapter;
    private ArrayList<AgentListModel> agentModelList;
    private TfButton btnCancel, btnOk;
    private CustomDialogAddVisitPlan customDialogAddVisitPlan;
    private SalesPlanResponse planResponse;

    private CustomDialogActualProduction customDialogActualProduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_visit_plan);

        init();
    }

    private void init() {
        if (binding.toolbarLayout.toolbar != null) {
            binding.toolbarLayout.toolbar.setTitle("");
        }
        setSupportActionBar(binding.toolbarLayout.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        binding.toolbarLayout.txtCustomTitle.setText(getString(R.string.sales_title));

        initCalendarView();

        clickListener();
    }

    private void clickListener() {
        binding.progressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String progress = "";
                if (planResponse != null && planResponse.getData() != null) {
                    progress = planResponse.getData().getProgress();
                }
                customDialogActualProduction = new CustomDialogActualProduction(new MaterialDialog.Builder(SalesVisitPlanActivity.this), SalesVisitPlanActivity.this,
                        progress, binding.cv.getCurrentCalendar());

            }
        });

    }

    private void fetchPlan() {

        showProgress(getString(R.string.loading));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            jsonObject.put("Month", binding.cv.getCurrentCalendar().get(Calendar.MONTH) + 1);
            jsonObject.put("Year", binding.cv.getCurrentCalendar().get(Calendar.YEAR));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("fetch_plan_req", jsonObject.toString());

        new CallWebService(this, AppConstants.PlanList, CallWebService.TYPE_POST, jsonObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                planResponse = MyApplication.getGson().fromJson(response, SalesPlanResponse.class);
                if (planResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                    Log.e("plan_res", response);
                    setPlanDetails(planResponse.getData());
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.start();
    }

    private void setPlanDetails(PlanDataResponse data) {

        binding.txtProgress.setText(String.format("Actual Progress: %s/%s", data.getProgress(), data.getTarget()));

        float variance = (Float.parseFloat(data.getProgress()) * 100) / Float.parseFloat(data.getTarget());
        binding.txtVariance.setText(Html.fromHtml("<u>" + String.format(Locale.US, "(%.2f%s)", variance, "%") + "</u>"));

        binding.cv.setMonthPlans(data.getPlans());

    }


    private void initCalendarView() {
        HashSet<Date> events = new HashSet<>();

        // add events
        Calendar calendar = Calendar.getInstance();
        events.add(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        events.add(calendar.getTime());

        binding.cv.setMode(CalendarView.MODE.MONTH);
        binding.cv.updateCalendar(events);
        binding.cv.setOnCalendarChangeListener(new CalendarView.onCalendarChangeListener() {
            @Override
            public void onChange(int type) {
                if (type == AppConstants.DAY_VIEW) {
                    String d = ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime());
                    for (int i = 0; i < planResponse.getData().getPlans().size(); i++) {
                        DatePlan datePlan = planResponse.getData().getPlans().get(i);
                        if (datePlan != null)
                            if (datePlan.getDate().equals(d)) {
                                Log.e("select", datePlan.getDate() + " -- " + datePlan.getPlan().size());
                                binding.cv.setDayPlan(datePlan.getPlan());
                            }
                    }
                } else {
                    fetchPlan();
                }
            }
        });

        binding.cv.setOnViewChangeListener(new CalendarView.onViewChangeListener() {
            @Override
            public void onChange() {
                fetchPlan();
            }
        });

        /*binding.cv.setOnGridSelectListener(new CalendarView.onGridSelectListener() {
            @Override
            public void onGridSelect(Calendar c) {
                String d = ConstantFormats.ymdFormat.format(c.getTime());
                Toast.makeText(SalesVisitPlanActivity.this, "Select " + d, Toast.LENGTH_SHORT).show();

               *//* for (int i = 0; i < planResponse.getData().getPlans().size(); i++) {
                    if (d.equals(planResponse.getData().getPlans().get(i).getDate())) {
                        binding.cv.setDayPlan(planResponse.getData().getPlans().get(i));
                    }
                }*//*
            }
        });*/

        binding.cv.setOnCalendarActionClickListener(new CalendarView.OnCalendarActionClickListener() {
            @Override
            public void onCalendarActionCalled(int optionType) {
                if (optionType == CalendarView.CalendarOptions.MAPPPING.ordinal()) {

                    fetchMappingData();

                } else if (optionType == CalendarView.CalendarOptions.RECRUITMENT.ordinal()) {

                    fetchRecruitmentData();

                } else if (optionType == CalendarView.CalendarOptions.DELETEALL.ordinal()) {

                    new MaterialDialog.Builder(SalesVisitPlanActivity.this)
                            .title(getString(R.string.delete_all_plan))
                            .typeface(Functions.getBoldFont(SalesVisitPlanActivity.this), Functions.getRegularFont(SalesVisitPlanActivity.this))
                            .positiveText(getString(R.string.yes))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    deleteAllPlan();
                                }
                            })
                            .negativeText(getString(R.string.no))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .content(String.format("%s  %s",getString(R.string.ask_for_delete_all_plan), ConstantFormats.sdf_day.format(binding.cv.getCurrentCalendar().getTime())))
                            .show();

                }

            }
        });
    }

    private void deleteAllPlan() {

        showProgress(getString(R.string.delete_all));

        JSONObject json = new JSONObject();
        try {
            json.put("PlanId", "");
            json.put("Date", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            Log.e("delete_all_plan", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.DeletePlan, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(SalesVisitPlanActivity.this, getString(R.string.plan_deleted));
                        onResume();

                    } else {
                        SimpleToast.error(context, wsResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
                    }
                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, context);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(context, context.getString(R.string.no_internet_connection), context.getString(R.string.fa_error));
            }
        }.call();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visit_plan, menu);
        addPlanItem = menu.findItem(R.id.action_add_plan);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_plan:

//                sampleMarquee();
                new CustomDialogAddVisitPlan(binding.cv.getCurrentCalendar(), "00:00",
                        new MaterialDialog.Builder(this), SalesVisitPlanActivity.this);

               /* new CustomDialogAddVisitPlan(currentDate, timeLineHours.get(getAdapterPosition()).getTime(), new MaterialDialog.Builder(this), this, new CustomDialogAddVisitPlanCallBack() {
                    @Override
                    public void addCallBack(JSONObject json) {

                        Log.e("json", json.toString());

                        addSalesVisitData(json);

                    }
                });*/

//                initAddVisitPlanCustomDialog();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "Call");
        fetchPlan();
        binding.cv.setMode(CalendarView.MODE.MONTH);
    }

    public void showProgress(String str) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(this, str, android.R.style.Theme_Translucent_NoTitleBar);
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
        binding.unbind();
    }

    private void fetchMappingData() {

        showProgress(getString(R.string.fetching_data));

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("UserId", PrefUtils.getUserId(this));
            requestObject.put("Date", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            Log.e("map_req", requestObject.toString());

            new CallWebService(this, AppConstants.FetchMapping, CallWebService.TYPE_POST, requestObject) {

                @Override
                public void response(String response) {
                    dismissProgress();

                    final MappingDialog mappingDialog;

                    FetchMappingResponse wsResponse = MyApplication.getGson().fromJson(response, FetchMappingResponse.class);

                    if (wsResponse != null) {

                        if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                            mappingDialog = new MappingDialog(context, wsResponse.data);
                        } else {
                            mappingDialog = new MappingDialog(context, null);
                        }

                        mappingDialog.setOnMappingDataSubmitListener(new MappingDialog.OnMappingDataSubmitListener() {
                            @Override
                            public void onSubmit(ArrayList<FetchMappingData> newMapping) {
                                submitMappingData(mappingDialog, newMapping);
                            }
                        });

                    } else {
                        SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                    }
                }

                @Override
                public void error(VolleyError error) {
                    dismissProgress();
                    VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
                }

                @Override
                public void noInternet() {
                    dismissProgress();
                    SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                }
            }.call();

        } catch (JSONException e) {
            Log.e("MAPPING_SUBMIT_EXP", e.getMessage());
        }
    }

    private void submitMappingData(final MappingDialog mappingDialog, ArrayList<FetchMappingData> mappingDataModelList) {

        mappingDialog.showProgress(getString(R.string.submitting));

        final JSONObject mainMappingRequestObject = new JSONObject();
        JSONObject mappingDataObject = new JSONObject();

        JSONArray mappingDataItemArray = new JSONArray();

        try {
            for (int i = 0; i < mappingDataModelList.size(); i++) {
                JSONObject mappingDataItem = new JSONObject();
                mappingDataItem.put("Mapping", mappingDataModelList.get(i).Mapping);
                mappingDataItem.put("MappingVisit", mappingDataModelList.get(i).MappingVisit);
                if (mappingDataModelList.get(i).MappingId != null) {
                    mappingDataItem.put("Id", mappingDataModelList.get(i).MappingId);
                }
                mappingDataItemArray.put(mappingDataItem);
            }

            mappingDataObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            mappingDataObject.put("CreatedDate", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            mappingDataObject.put("Data", mappingDataItemArray);

            mainMappingRequestObject.put("MappingData", mappingDataObject);

            Log.e("main_mapping_req", MyApplication.getGson().toJson(mainMappingRequestObject));

            new CallWebService(this, AppConstants.SubmitMapping, CallWebService.TYPE_POST, mainMappingRequestObject) {

                @Override
                public void response(String response) {
                    mappingDialog.dismissProgress();

                    com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                    if (wsResponse != null) {

                        Log.e("main_mapping_res", MyApplication.getGson().toJson(wsResponse));

                        if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                            SimpleToast.ok(SalesVisitPlanActivity.this, getString(R.string.mapping_submit_success));
                        } else {
                            SimpleToast.error(SalesVisitPlanActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                        }
                    }
                }

                @Override
                public void error(VolleyError error) {
                    mappingDialog.dismissProgress();
                    VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
                }

                @Override
                public void noInternet() {
                    mappingDialog.dismissProgress();
                    SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                }
            }.call();

        } catch (JSONException e) {
            Log.e("MAPPING_SUBMIT_EXP", e.getMessage());
            Log.e("UPDATE_MAPPING_EXP", e.getMessage());
        }
    }

    private void fetchRecruitmentData() {

        showProgress(getString(R.string.fetching_data));

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("UserId", PrefUtils.getUserId(this));
            requestObject.put("Date", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            Log.e("recruit_req", requestObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(this, AppConstants.FetchRecruitment, CallWebService.TYPE_POST, requestObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                final RecruitmentDialog recruitmentDialog;

                FetchRecruitmentResponse wsResponse = MyApplication.getGson().fromJson(response, FetchRecruitmentResponse.class);

                if (wsResponse != null) {

                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        recruitmentDialog = new RecruitmentDialog(context, wsResponse.data);
                    } else {
                        recruitmentDialog = new RecruitmentDialog(context, null);
                    }

                    recruitmentDialog.setOnRecruitmentDataSubmitListener(new RecruitmentDialog.OnRecruitmentDataSubmitListener() {
                        @Override
                        public void onSubmit(ArrayList<FetchRecruitmentData> recruitmentDataModelList) {
                            submitRecruitmentData(recruitmentDialog, recruitmentDataModelList);
                        }
                    });

                } else {
                    SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();

    }

    private void submitRecruitmentData(final RecruitmentDialog recruitmentDialog, ArrayList<FetchRecruitmentData> recruitmentDataArrayList) {

        recruitmentDialog.showProgress(getString(R.string.submitting));

        final JSONObject mainRecruitmentRequestObject = new JSONObject();
        JSONObject recruitmentDataObject = new JSONObject();

        JSONArray recruitmentDataItemArray = new JSONArray();
        try {
            Log.e("TAG", new Gson().toJson(recruitmentDataArrayList));
            for (int i = 0; i < recruitmentDataArrayList.size(); i++) {
                JSONObject recruitmentDataItem = new JSONObject();
                recruitmentDataItem.put("ExistingName", recruitmentDataArrayList.get(i).Existing);
                recruitmentDataItem.put("ExistingLevel", recruitmentDataArrayList.get(i).ExistingVisit);
                recruitmentDataItem.put("TimeVisit", recruitmentDataArrayList.get(i).TimeVisit);
                if (recruitmentDataArrayList.get(i).RecId != null) {
                    recruitmentDataItem.put("Id", recruitmentDataArrayList.get(i).RecId);
                }
                recruitmentDataItemArray.put(recruitmentDataItem);
            }

            recruitmentDataObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            recruitmentDataObject.put("CreatedDate", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            recruitmentDataObject.put("Data", recruitmentDataItemArray);

            mainRecruitmentRequestObject.put("Recruitment", recruitmentDataObject);

            Log.e("main_recruit_req", MyApplication.getGson().toJson(mainRecruitmentRequestObject));

            new CallWebService(this, AppConstants.SubmitRecruitment, CallWebService.TYPE_POST, mainRecruitmentRequestObject) {

                @Override
                public void response(String response) {
                    recruitmentDialog.dismissProgress();

                    com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                    if (wsResponse != null) {

                        Log.e("main_recruit_res", MyApplication.getGson().toJson(wsResponse));

                        if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                            SimpleToast.ok(SalesVisitPlanActivity.this, getString(R.string.submit_success));

                        } else {
                            SimpleToast.error(SalesVisitPlanActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                        }
                    }
                }


                @Override
                public void error(VolleyError error) {
                    recruitmentDialog.dismissProgress();
                    VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
                }

                @Override
                public void noInternet() {
                    recruitmentDialog.dismissProgress();
                    SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                }
            }.call();

        } catch (JSONException e) {
            Log.e("RECRUITMENT_SUBMIT_EXP", e.getMessage());
        }
    }

    private void updateRecruitmentData(final RecruitmentDialog recruitmentDialog, ArrayList<FetchRecruitmentData> recruitmentDataArrayList) {
        recruitmentDialog.showProgress(getString(R.string.updating));

        final JSONObject mainRecruitmentRequestObject = new JSONObject();
        JSONObject recruitmentDataObject = new JSONObject();

        JSONArray recruitmentDataItemArray = new JSONArray();
        try {
            for (int i = 0; i < recruitmentDataArrayList.size(); i++) {
                JSONObject recruitmentDataItem = new JSONObject();
                recruitmentDataItem.put("Id", recruitmentDataArrayList.get(i).RecId);
                recruitmentDataItem.put("Existing", recruitmentDataArrayList.get(i).Existing);
                recruitmentDataItem.put("ExistingVisit", recruitmentDataArrayList.get(i).ExistingVisit);
                recruitmentDataItem.put("TimeVisit", recruitmentDataArrayList.get(i).TimeVisit);
                recruitmentDataItemArray.put(recruitmentDataItem);
            }

            recruitmentDataObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            recruitmentDataObject.put("CreatedDate", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            recruitmentDataObject.put("Data", recruitmentDataItemArray);

            mainRecruitmentRequestObject.put("RecruitmentData", recruitmentDataObject);

            Log.e("UPDATE_RECRUITMENT_REQ", new Gson().toJson(mainRecruitmentRequestObject).toString());

            new CallWebService(this, AppConstants.UpdateMapping, CallWebService.TYPE_POST, mainRecruitmentRequestObject) {

                @Override
                public void response(String response) {
                    recruitmentDialog.dismissProgress();
                    Log.e("UPDATE_RECRUITMENT_RESP", new Gson().toJson(response).toString());
                    com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                    if (wsResponse != null) {
                        if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                            SimpleToast.ok(SalesVisitPlanActivity.this, getString(R.string.update_success));
                        } else {
                            SimpleToast.error(SalesVisitPlanActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                        }
                    }
                }

                @Override
                public void error(VolleyError error) {
                    recruitmentDialog.dismissProgress();
                    VolleyErrorHelper.showErrorMsg(error, SalesVisitPlanActivity.this);
                }

                @Override
                public void noInternet() {
                    recruitmentDialog.dismissProgress();
                    SimpleToast.error(SalesVisitPlanActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
                }
            }.call();

        } catch (JSONException e) {
            Log.e("UPDATE_RECRUITMENT_EXP", e.getMessage());
        }
    }
}
