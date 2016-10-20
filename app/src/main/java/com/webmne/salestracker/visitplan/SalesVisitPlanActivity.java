package com.webmne.salestracker.visitplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.call.GetBranches;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.DatePlan;
import com.webmne.salestracker.api.model.FetchMappingData;
import com.webmne.salestracker.api.model.FetchMappingResponse;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.api.model.FetchRecruitmentResponse;
import com.webmne.salestracker.api.model.PlanDataResponse;
import com.webmne.salestracker.api.model.SalesPlanResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivitySalesVisitPlanBinding;
import com.webmne.salestracker.employee.GetEmployees;
import com.webmne.salestracker.employee.model.EmployeeModel;
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
import com.webmne.salestracker.visitplan.model.SelectedUser;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.calendar.CalendarView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SalesVisitPlanActivity extends AppCompatActivity implements View.OnTouchListener {

    private ActivitySalesVisitPlanBinding binding;
    private MenuItem addPlanItem;
    private LoadingIndicatorDialog dialog;

    private FamiliarRecyclerView familiarRecyclerView;
    private CustomDialogVisitPlanAgentListAdapter customDialogVisitPlanAgentListAdapter;
    private TfButton btnCancel, btnOk;
    private CustomDialogAddVisitPlan customDialogAddVisitPlan;
    private SalesPlanResponse planResponse;

    private CustomDialogActualProduction customDialogActualProduction;
    private ArrayList<AgentListModel> agentModelList;

    private ArrayList<EmployeeModel> emptList;
    private ArrayList<Branch> branchList;
    private ArrayList<EmployeeModel> mktList;
    private ArrayList<EmployeeModel> hosList;
    private int mktWhich = 0;
    private int hosWhich = 0;
    private int branchWhich = 0;

    private SelectedUser selectedUser;

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

        setDefaultUser();

        initCalendarView();

        clickListener();

        // call ws
        if (!PrefUtils.getUserProfile(this).getPos_name().equals(AppConstants.MARKETER)) {
            callWs();
        }
    }

    private void setDefaultUser() {
        // default selectedUser
        selectedUser = new SelectedUser();
        selectedUser.setUserId(PrefUtils.getUserId(this));
        selectedUser.setUserName(PrefUtils.getUserProfile(this).getFirstName());
        if (binding.cv != null) {
            binding.cv.setUser(selectedUser);
        }
    }

    private void callWs() {

        branchList = new ArrayList<>();

        // get Employees
        new GetEmployees(this, PrefUtils.getUserId(this), new GetEmployees.OnGetEmpListener() {
            @Override
            public void getEmployees(ArrayList<EmployeeModel> agentList) {
                emptList = agentList;
                setUpChild(emptList);

                if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.RM)) {
                    // get Branches
                    new GetBranches(SalesVisitPlanActivity.this, PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getRegionId(), new GetBranches.OnGetBranchListener() {
                        @Override
                        public void getBranches(ArrayList<Branch> list) {
                            branchList = list;
                        }
                    });
                }
            }
        });
    }

    private void setUpChild(ArrayList<EmployeeModel> emptList) {
        mktList = new ArrayList<>();
        hosList = new ArrayList<>();

        for (EmployeeModel model : emptList) {
            if (model.getPosition().equals(AppConstants.MKT_POS + "")) {
                mktList.add(model);
            } else if (model.getPosition().equals(AppConstants.HOS_POS + "")) {
                hosList.add(model);
            }
        }
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

        binding.edtMarketer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SalesVisitPlanActivity.this)
                        .title(getString(R.string.select_marketer))
                        .items(mktList)
                        .typeface(Functions.getBoldFont(SalesVisitPlanActivity.this), Functions.getRegularFont(SalesVisitPlanActivity.this))
                        .itemsCallbackSingleChoice(mktWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                mktWhich = which;

                                EmployeeModel model = mktList.get(which);
                                Log.e("model", model.getId() + " @");

                                selectedUser.setUserId(model.getId());
                                selectedUser.setUserName(model.getName());
                                binding.cv.setUser(selectedUser);

                                fetchPlan();

                                new GetAgentsForPlan(SalesVisitPlanActivity.this, model.getId(), new GetAgentsForPlan.OnGetAgentsListener() {
                                    @Override
                                    public void getAgents(ArrayList<AgentListModel> agentList) {

                                    }
                                });

                                binding.edtMarketer.setText(text.toString().replace("[", "").replace("]", ""));
                                binding.edtHos.setText("");

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();
            }
        });

        binding.edtHos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SalesVisitPlanActivity.this)
                        .title(getString(R.string.select_hos))
                        .items(hosList)
                        .typeface(Functions.getBoldFont(SalesVisitPlanActivity.this), Functions.getRegularFont(SalesVisitPlanActivity.this))
                        .itemsCallbackSingleChoice(hosWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                hosWhich = which;

                                EmployeeModel model = hosList.get(which);
                                Log.e("model", model.getId() + " @");

                                selectedUser.setUserId(model.getId());
                                selectedUser.setUserName(model.getName());
                                binding.cv.setUser(selectedUser);

                                fetchPlan();

                                new GetAgentsForPlan(SalesVisitPlanActivity.this, model.getId(), new GetAgentsForPlan.OnGetAgentsListener() {
                                    @Override
                                    public void getAgents(ArrayList<AgentListModel> agentList) {

                                    }
                                });

                                binding.edtHos.setText(text.toString().replace("[", "").replace("]", ""));
                                binding.edtMarketer.setText("");

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();
            }
        });

        binding.edtBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SalesVisitPlanActivity.this)
                        .title(getString(R.string.select_branch))
                        .items(branchList)
                        .typeface(Functions.getBoldFont(SalesVisitPlanActivity.this), Functions.getRegularFont(SalesVisitPlanActivity.this))
                        .itemsCallbackSingleChoice(branchWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                branchWhich = which;

                                Branch branch = branchList.get(which);
                                binding.edtBranch.setText(branch.getBranchName());

                                mktList = new ArrayList<EmployeeModel>();
                                hosList = new ArrayList<EmployeeModel>();

                                for (EmployeeModel model : emptList) {
                                    if (model.getBranchId().equals(branch.getBranchId())) {
                                        if (model.getPosition().equals(AppConstants.MKT_POS + "")) {
                                            mktList.add(model);
                                        } else if (model.getPosition().equals(AppConstants.HOS_POS + "")) {
                                            hosList.add(model);
                                        }
                                    }
                                }

                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();
            }
        });

        binding.edtMarketer.setOnTouchListener(this);
        binding.edtBranch.setOnTouchListener(this);
        binding.edtHos.setOnTouchListener(this);

    }

    private void fetchPlan() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserId", selectedUser.getUserId());
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
                setPlanDetails(planResponse.getData());

                if (planResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                    Log.e("plan_res", response);
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

        if (data != null) {
            binding.txtProgress.setText(String.format("Actual Progress: %s/%s", data.getProgress(), data.getTarget()));

            float variance = (Float.parseFloat(data.getProgress()) * 100) / Float.parseFloat(data.getTarget());
            binding.txtVariance.setText(Html.fromHtml("<u>" + String.format(Locale.US, "(%.2f%s)", variance, "%") + "</u>"));

            binding.cv.setMonthPlans(data.getPlans());
            binding.cv.notifyAdapter();

        } else {
            binding.txtProgress.setText(String.format("Actual Progress: %s/%s", "0", "0"));

            binding.txtVariance.setText(Html.fromHtml("<u>" + "(0.0%)" + "</u>"));

            binding.cv.setMonthPlans(new ArrayList<DatePlan>());
        }
    }

    private void initCalendarView() {

        if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.MARKETER)) {
            binding.selectionLayout.setVisibility(View.GONE);

        } else {
            binding.selectionLayout.setVisibility(View.VISIBLE);
            if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.HOS)) {
                binding.edtBranch.setVisibility(View.GONE);
                binding.edtHos.setVisibility(View.GONE);
                binding.edtMarketer.setVisibility(View.VISIBLE);

            } else if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.BM)) {
                binding.edtBranch.setVisibility(View.GONE);
                binding.edtHos.setVisibility(View.VISIBLE);
                binding.edtMarketer.setVisibility(View.VISIBLE);

            } else if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.RM)) {
                binding.edtBranch.setVisibility(View.VISIBLE);
                binding.edtHos.setVisibility(View.VISIBLE);
                binding.edtMarketer.setVisibility(View.VISIBLE);
            }
        }

        binding.cv.setMode(CalendarView.MODE.MONTH);
        binding.cv.setUser(selectedUser);
        binding.cv.updateCalendar();

        binding.cv.setOnCalendarChangeListener(new CalendarView.onMonthChangeListener() {
            @Override
            public void onMonthChange() {
                fetchPlan();
            }
        });

        binding.cv.setOnModeChangeListener(new CalendarView.OnModeChangeListener() {
            @Override
            public void onModeChange(CalendarView.MODE modeType) {
                binding.cv.setUser(selectedUser);
                if (PrefUtils.getUserProfile(SalesVisitPlanActivity.this).getPos_name().equals(AppConstants.MARKETER)) {
                    binding.selectionLayout.setVisibility(View.GONE);

                } else {
                    if (modeType == CalendarView.MODE.MONTH) {
                        binding.selectionLayout.setVisibility(View.VISIBLE);

                    } else {
                        binding.selectionLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.cv.setOnViewChangeListener(new CalendarView.onViewChangeListener() {
            @Override
            public void onViewChange() {
                fetchPlan();
            }
        });

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
                            .content(String.format("%s  %s", getString(R.string.ask_for_delete_all_plan), ConstantFormats.sdf_day.format(binding.cv.getCurrentCalendar().getTime())))
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
                new CustomDialogAddVisitPlan(selectedUser, binding.cv.getCurrentCalendar(), "00:00",
                        new MaterialDialog.Builder(this), SalesVisitPlanActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get agents and plan one by one
        fetchPlan();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        EditText editText = (EditText) v;
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                // your action here
                editText.setText("");

                if (editText.getId() == R.id.edtMarketer) {
                    mktWhich = 0;

                } else if (editText.getId() == R.id.edtHos) {
                    hosWhich = 0;

                } else {
                    branchWhich = 0;
                    setUpChild(emptList);
                    binding.edtMarketer.setText("");
                    binding.edtHos.setText("");
                }
                setDefaultUser();
                fetchPlan();

                return true;
            }
        }
        return false;
    }
}
