package com.webmne.salestracker.visitplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.PlanDataResponse;
import com.webmne.salestracker.api.model.Response;
import com.webmne.salestracker.api.model.SalesPlanResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivitySalesVisitPlanBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.ui.profile.UserProfileActivity;
import com.webmne.salestracker.visitplan.adapter.CustomDialogVisitPlanAgentListAdapter;
import com.webmne.salestracker.visitplan.dialogs.MappingDialog;
import com.webmne.salestracker.visitplan.dialogs.RecruitmentDialog;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.visitplan.model.MappingDataModel;
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

        fetchPlan();
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

                    // TODO: 06-10-2016 fetch mapping data
                    fetchMappingData();

                    /*final MappingDialog mappingDialog = new MappingDialog(new MaterialDialog.Builder(SalesVisitPlanActivity.this), SalesVisitPlanActivity.this);
                    mappingDialog.show();
                    mappingDialog.setOnMappingDataSubmitListener(new MappingDialog.OnMappingDataSubmitListener() {
                        @Override
                        public void onSubmit(ArrayList<MappingDataModel> mappingDataModelList) {
                            Log.e("MAPPING_LIST", new Gson().toJson(mappingDataModelList).toString());
                            submitMappingData(mappingDialog, mappingDataModelList);
                        }
                    });*/
                } else if (optionType == CalendarView.CalendarOptions.RECRUITMENT.ordinal()) {
                    RecruitmentDialog recruitmentDialog = new RecruitmentDialog(new MaterialDialog.Builder(SalesVisitPlanActivity.this), SalesVisitPlanActivity.this);
                    recruitmentDialog.show();
//                    Toast.makeText(SalesVisitPlanActivity.this, "Recruitment", Toast.LENGTH_SHORT).show();
                } else if (optionType == CalendarView.CalendarOptions.DELETEALL.ordinal()) {
                    Toast.makeText(SalesVisitPlanActivity.this, "Delete All", Toast.LENGTH_SHORT).show();
                }

            }
        });
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

                new CustomDialogAddVisitPlan(new MaterialDialog.Builder(this), this, new CustomDialogAddVisitPlanCallBack() {
                    @Override
                    public void addCallBack(JSONObject json) {

                        Log.e("json", json.toString());

                        addSalesVisitData(json);

                    }
                });

//                initAddVisitPlanCustomDialog();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addSalesVisitData(JSONObject json) {

//        new CallWebService(this, AppConstants.AddAgent, CallWebService.TYPE_POST, json) {
//
//            @Override
//            public void response(String response) {
//                dismissProgress();
//
//                AddAgentResponse addAgentResponse = MyApplication.getGson().fromJson(response, AddAgentResponse.class);
//
//                if (addAgentResponse != null) {
//                    Log.e("add_res", MyApplication.getGson().toJson(addAgentResponse));
//
//                    if (addAgentResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
//                        SimpleToast.ok(AddAgentActivity.this, getString(R.string.add_agent_success));
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//
//                    } else {
//                        SimpleToast.error(AddAgentActivity.this, addAgentResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
//                    }
//                }
//            }
//
//            @Override
//            public void error(VolleyError error) {
//                dismissProgress();
//                VolleyErrorHelper.showErrorMsg(error, AddAgentActivity.this);
//            }
//
//            @Override
//            public void noInternet() {
//                dismissProgress();
//                SimpleToast.error(AddAgentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
//            }
//        }.call();

    }


//    private void initAddVisitPlanCustomDialog() {
//        final MaterialDialog dialog = new MaterialDialog.Builder(SalesVisitPlanActivity.this)
//                .title(R.string.add_agent_dialog_title)
//                .typeface(Functions.getBoldFont(this), Functions.getRegularFont(this))
//                .customView(R.layout.custom_dialog_add_visit_plan, false)
//                .canceledOnTouchOutside(false)
//                .show();
//
//        View view = dialog.getCustomView();
//
//        familiarRecyclerView = (FamiliarRecyclerView) view.findViewById(R.id.agentListRecyclerView);
//        btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
//        btnOk = (TfButton) view.findViewById(R.id.btnOk);
//
//        agentModelList = new ArrayList<>();
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        familiarRecyclerView.setLayoutManager(layoutManager);
//        familiarRecyclerView.addItemDecoration(new LineDividerItemDecoration(this));
//
//        customDialogVisitPlanAgentListAdapter = new CustomDialogVisitPlanAgentListAdapter(this, agentModelList);
//
//        familiarRecyclerView.setAdapter(customDialogVisitPlanAgentListAdapter);
//        familiarRecyclerView.setHasFixedSize(true);
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        getAgentList();
//
//    }
//
//    private void getAgentList() {
//
//        for (int i = 0; i < 10; i++) {
//            agentModelList.add(new AgentListModel("" + i, "Agent " + i, "Star", "1", "0", "0"));
//
//        }
//
//
//        customDialogVisitPlanAgentListAdapter.setAgentList(agentModelList);
//
//    }


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

        final JSONObject requestObject = new JSONObject();
        try {

            requestObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            requestObject.put("Date", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));

            Log.e("FETCH_MAPPING_REQ", new Gson().toJson(requestObject).toString());

            new CallWebService(this, AppConstants.FetchMapping, CallWebService.TYPE_POST, requestObject) {

                @Override
                public void response(String response) {
                    dismissProgress();
                    Log.e("FETCH_MAPPING_RESP", new Gson().toJson(response).toString());
                    Response wsResponse = MyApplication.getGson().fromJson(response, Response.class);
                    if (wsResponse != null) {
                        if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                           // SimpleToast.ok(SalesVisitPlanActivity.this, getString(R.string.mapping_submit_success));
                            // TODO: 06-10-2016 inflate dialog

                        } else {
                            SimpleToast.error(SalesVisitPlanActivity.this, wsResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                        }
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

    private void submitMappingData(final MappingDialog mappingDialog, ArrayList<MappingDataModel> mappingDataModelList) {
        mappingDialog.showProgress(getString(R.string.submitting));

        final JSONObject mainMappingRequestObject = new JSONObject();
        JSONObject mappingDataObject = new JSONObject();
        JSONObject mappingDataItem = new JSONObject();
        JSONArray mappingDataItemArray = new JSONArray();
        try {
            for (int i = 0; i < mappingDataModelList.size(); i++) {
                mappingDataItem.put("Mapping", mappingDataModelList.get(i).Mapping);
                mappingDataItem.put("MappingVisit", mappingDataModelList.get(i).MappingVisit);
                mappingDataItemArray.put(mappingDataItem);
            }

            mappingDataObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            mappingDataObject.put("CreatedDate", ConstantFormats.ymdFormat.format(binding.cv.getCurrentCalendar().getTime()));
            mappingDataObject.put("Data", mappingDataItemArray);

            mainMappingRequestObject.put("MappingData", mappingDataObject);

            Log.e("MAPPING_REQ", new Gson().toJson(mainMappingRequestObject).toString());

            new CallWebService(this, AppConstants.SubmitMapping, CallWebService.TYPE_POST, mainMappingRequestObject) {

                @Override
                public void response(String response) {
                    mappingDialog. dismissProgress();
                    Log.e("MAPPING_RESP", new Gson().toJson(response).toString());
                    com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                    if (wsResponse != null) {
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
        }
    }
}
