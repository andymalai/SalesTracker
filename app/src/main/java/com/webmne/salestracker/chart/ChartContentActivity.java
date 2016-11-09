package com.webmne.salestracker.chart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.call.GetBranchChart;
import com.webmne.salestracker.api.call.GetDepartmentChart;
import com.webmne.salestracker.api.call.GetDepartmentSlaChart;
import com.webmne.salestracker.api.call.GetPlanChart;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchChart;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.api.model.DepartmentChart;
import com.webmne.salestracker.api.model.PlanChart;
import com.webmne.salestracker.api.model.SlaChart;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivityChartContentBinding;
import com.webmne.salestracker.event.AddEventActivity;
import com.webmne.salestracker.event.model.Region;
import com.webmne.salestracker.event.model.RegionListResponse;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChartContentActivity extends AppCompatActivity {

    private ActivityChartContentBinding viewBinding;
    private int chartType;
    private Calendar calendar;
    private int year, monthInt, yearWhich = 0, monthWhich = 0, branchInt = 0, regionInt = 0;
    private int regionWhich = 0, branchWhich = 0;
    private String month;
    private ArrayList<Integer> yearArray;
    private ArrayList<String> monthArray;
    private JSONObject json;
    private LoadingIndicatorDialog dialog;
    private ArrayList<Region> regionArrayList;
    private String strRegion = "0";
    private ArrayList<Branch> branchArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_chart_content);
        init();

    }

    private void init() {

        chartType = getIntent().getIntExtra(AppConstants.CHART_TYPE, 0);

        initToolbar();

        branchInt = Integer.parseInt(PrefUtils.getUserProfile(this).getBranch());
        regionInt = Integer.parseInt(PrefUtils.getUserProfile(this).getRegionId());

        initYearMonth();

        callService();

        actionListener();

    }

    private void callService() {

        if (chartType == AppConstants.BRANCH_CHART) {
            getBranchChart();

        } else if (chartType == AppConstants.DEPARTMENT_CHART) {
            getDepartmentChart();

        } else if (chartType == AppConstants.DEPARTMENT_SLA_CHART) {
            getDepartmentSlaChart();

        } else {
            // call services by ladder
            getPlanChart();
        }
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
                        SimpleToast.error(ChartContentActivity.this, regionListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(ChartContentActivity.this, getString(R.string.try_again), getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ChartContentActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ChartContentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
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

    private void getPlanChart() {
        JSONObject json = new JSONObject();
        try {
            json.put("Month", monthInt);
            json.put("Year", year);
            json.put("Branch", branchInt);
            json.put("Region", regionInt);
            Log.e("plan_json", json.toString());
            new GetPlanChart(this, json, new GetPlanChart.OnGetChartListener() {
                @Override
                public void setPlanChart(ArrayList<PlanChart> planChartData) {
                    Log.e("size", planChartData.size() + " ####");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fetchRegion();
    }

    private void getDepartmentSlaChart() {
        json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("Month", monthInt);
            json.put("Year", year);
            Log.e("sla_json", json.toString());
            new GetDepartmentSlaChart(this, json, new GetDepartmentSlaChart.OnGetChartListener() {
                @Override
                public void setSlaChart(ArrayList<SlaChart> slaChartData) {
                    Log.e("size", slaChartData.size() + " ####");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getDepartmentChart() {
        json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("Month", monthInt);
            json.put("Year", year);
            Log.e("dept_json", json.toString());
            new GetDepartmentChart(this, json, new GetDepartmentChart.OnGetChartListener() {
                @Override
                public void setDeptChart(ArrayList<DepartmentChart> deptChartData) {
                    Log.e("size", deptChartData.size() + " ####");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBranchChart() {
        json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(this));
            json.put("Month", monthInt);
            json.put("Year", year);
            json.put("Position", PrefUtils.getUserProfile(this).getPos_name());
            Log.e("br_json", json.toString());
            new GetBranchChart(this, json, new GetBranchChart.OnGetChartListener() {
                @Override
                public void setBranchChart(ArrayList<BranchChart> branchChartData) {
                    Log.e("size", branchChartData.size() + " ####");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actionListener() {

        viewBinding.edtRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ChartContentActivity.this)
                        .title(getString(R.string.select_region))
                        .items(regionArrayList)
                        .typeface(Functions.getBoldFont(ChartContentActivity.this), Functions.getRegularFont(ChartContentActivity.this))
                        .itemsCallbackSingleChoice(regionWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                regionWhich = which;

                                viewBinding.edtRegion.setText(text.toString().replace("[", "").replace("]", ""));
                                strRegion = regionArrayList.get(which).getRegion();

                                regionInt = Integer.parseInt(regionArrayList.get(which).getRegion());

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
                new MaterialDialog.Builder(ChartContentActivity.this)
                        .title(getString(R.string.select_branch))
                        .items(branchArrayList)
                        .typeface(Functions.getBoldFont(ChartContentActivity.this), Functions.getRegularFont(ChartContentActivity.this))
                        .itemsCallbackSingleChoice(branchWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                branchWhich = which;

                                viewBinding.edtBranch.setText(text.toString());
                                branchInt = Integer.parseInt(branchArrayList.get(branchWhich).getBranchId());

                                getPlanChart();
                                return false;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .show();
            }
        });

        viewBinding.edtYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ChartContentActivity.this)
                        .title(getString(R.string.select_year))
                        .items(yearArray)
                        .typeface(Functions.getBoldFont(ChartContentActivity.this), Functions.getRegularFont(ChartContentActivity.this))
                        .itemsCallbackSingleChoice(yearWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                yearWhich = which;
                                viewBinding.edtYear.setText(text.toString());
                                year = Integer.parseInt(text.toString());
                                callService();
                                return true;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .negativeText(getString(R.string.btn_cancel))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        viewBinding.edtMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ChartContentActivity.this)
                        .title(getString(R.string.select_month))
                        .items(monthArray)
                        .typeface(Functions.getBoldFont(ChartContentActivity.this), Functions.getRegularFont(ChartContentActivity.this))
                        .itemsCallbackSingleChoice(monthWhich, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                monthWhich = which;
                                viewBinding.edtMonth.setText(text.toString());
                                monthInt = monthWhich + 1;
                                callService();
                                return true;
                            }
                        })
                        .positiveText(getString(R.string.btn_ok))
                        .negativeText(getString(R.string.btn_cancel))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
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
                        SimpleToast.error(ChartContentActivity.this, branchListResponse.getResponse().getResponseMsg(), getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }

            }

            @Override
            public void error(VolleyError error) {
                dismissProgress();
                VolleyErrorHelper.showErrorMsg(error, ChartContentActivity.this);
            }

            @Override
            public void noInternet() {
                dismissProgress();
                SimpleToast.error(ChartContentActivity.this, getString(R.string.no_internet_connection), getString(R.string.fa_error));
            }
        }.call();
    }

    private void initYearMonth() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        viewBinding.edtYear.setText(String.format(Locale.US, "%d", year));
        month = ConstantFormats.MMMFormat.format(calendar.getTime());
        monthInt = Integer.parseInt(ConstantFormats.MMFormat.format(calendar.getTime()));
        viewBinding.edtMonth.setText(month);

        yearArray = new ArrayList<>();
        monthArray = new ArrayList<>();

        for (int i = 2016; i < 2070; i++) {
            yearArray.add(i);
        }

        for (int i = 0; i < yearArray.size(); i++) {
            if (year == yearArray.get(i)) {
                yearWhich = i;
                break;
            }
        }

        monthArray.add("Jan");
        monthArray.add("Feb");
        monthArray.add("March");
        monthArray.add("April");
        monthArray.add("May");
        monthArray.add("June");
        monthArray.add("July");
        monthArray.add("Aug");
        monthArray.add("Sept");
        monthArray.add("Oct");
        monthArray.add("Nov");
        monthArray.add("Dec");

        for (int j = 0; j < monthArray.size(); j++) {
            if (month.equals(monthArray.get(j))) {
                monthWhich = j;
                break;
            }
        }

    }

    private void initToolbar() {
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
        if (chartType == AppConstants.BRANCH_CHART) {
            viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.branch_chart_title));
            viewBinding.regionLayout.setVisibility(View.GONE);

        } else if (chartType == AppConstants.DEPARTMENT_CHART) {
            viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.dept_chart_title));
            viewBinding.regionLayout.setVisibility(View.GONE);

        } else if (chartType == AppConstants.DEPARTMENT_SLA_CHART) {
            viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.sla_chart_title));
            viewBinding.regionLayout.setVisibility(View.GONE);

        } else {
            viewBinding.regionLayout.setVisibility(View.VISIBLE);
            viewBinding.toolbarLayout.txtCustomTitle.setText(getString(R.string.visit_plan_chart_title));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
