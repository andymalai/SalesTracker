package com.webmne.salestracker.chart;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.gun0912.tedpermission.PermissionListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

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

                    createPlanChart(planChartData);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fetchRegion();
    }

    private void createPlanChart(ArrayList<PlanChart> planChartData) {

        viewBinding.chartLayout.removeAllViews();

        final ComboLineColumnChartView chartView = new ComboLineColumnChartView(ChartContentActivity.this);
        viewBinding.chartLayout.addView(chartView);

        viewBinding.plannedVisitsLayout.setVisibility(View.VISIBLE);
        viewBinding.completedLayout.setVisibility(View.VISIBLE);
        viewBinding.tftCompleted.setText(getString(R.string.actual_visited));
        viewBinding.ongoingLayout.setVisibility(View.GONE);
        viewBinding.overdueLayout.setVisibility(View.GONE);
        viewBinding.noOfIssueLayout.setVisibility(View.VISIBLE);
        viewBinding.tftNoOfIssue.setText(getString(R.string.plan_attainment));

        ArrayList<Column> columns = new ArrayList<Column>();
        ArrayList<SubcolumnValue> values;
        ArrayList<AxisValue> xAxisValues = new ArrayList<>();

        for (int i = 0; i < planChartData.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            float average_sla = Float.parseFloat(planChartData.get(i).getPlannedVisits());
            values.add(new SubcolumnValue(average_sla, ContextCompat.getColor(this, R.color.planned_visited_color)));

            float no_of_issue = Float.parseFloat(planChartData.get(i).getActualVisited());
            values.add(new SubcolumnValue(no_of_issue, ContextCompat.getColor(this, R.color.completed_color)));

            xAxisValues.add(new AxisValue(i).setLabel(planChartData.get(i).getDate()));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData columnChartData = new ColumnChartData(columns);

        // Set stacked flag.
        columnChartData.setStacked(false);


        ArrayList<Line> lines = new ArrayList<Line>();
        ArrayList<PointValue> pointValues = new ArrayList<PointValue>();

        for (int j = 0; j < planChartData.size(); ++j) {
//                float point_y = Float.parseFloat(slaChartData.get(j).getAverageSLA()) + Float.parseFloat(slaChartData.get(j).getNoOfIssues());
            pointValues.add(new PointValue(j, Float.parseFloat(planChartData.get(j).getPlanAttainment().replace("%", ""))));
        }

        Line line = new Line(pointValues);
        line.setColor(Color.BLACK);
        line.setCubic(true);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
//        line.setFormatter(new );
        lines.add(line);

        LineChartData lineChartData = new LineChartData(lines);


        ComboLineColumnChartData data = new ComboLineColumnChartData(columnChartData, lineChartData);


        /*------------------set xAxis data--------------------*/
        Axis axisX = new Axis(xAxisValues);
        axisX.setTextSize(10);
        axisX.setTextColor(Color.BLACK);
//        axisX.setName("Name of department");
        axisX.setMaxLabelChars(10);
        axisX.setFormatter(new SimpleAxisValueFormatter());
        axisX.setHasTiltedLabels(true);

        data.setAxisXBottom(axisX);
        /*------------------------------------------------------*/

        /*------------------set yAxis data--------------------*/
        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setTextSize(12);
        axisY.setTextColor(Color.BLACK);
        axisY.setName("Plan Attainment (%)");

        data.setAxisYLeft(axisY);
        /*------------------------------------------------------*/

        chartView.setComboLineColumnChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setClickable(true);
        chartView.setValueTouchEnabled(false);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

        if (xAxisValues.size() > 6) {
            chartView.setZoomLevel(0, 0, 3.42f);
        }

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

                    createDepartmentSlaChart(slaChartData);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createDepartmentSlaChart(ArrayList<SlaChart> slaChartData) {

        viewBinding.chartLayout.removeAllViews();

        ComboLineColumnChartView chartView = new ComboLineColumnChartView(ChartContentActivity.this);
        viewBinding.chartLayout.addView(chartView);

        viewBinding.plannedVisitsLayout.setVisibility(View.GONE);
        viewBinding.completedLayout.setVisibility(View.VISIBLE);
        viewBinding.tftCompleted.setText(getString(R.string.average_sla));
        viewBinding.ongoingLayout.setVisibility(View.GONE);
        viewBinding.overdueLayout.setVisibility(View.GONE);
        viewBinding.noOfIssueLayout.setVisibility(View.VISIBLE);
        viewBinding.tftNoOfIssue.setText(getString(R.string.no_of_issue));

        ArrayList<Column> columns = new ArrayList<Column>();
        ArrayList<SubcolumnValue> values;
        ArrayList<AxisValue> xAxisValues = new ArrayList<>();

        for (int i = 0; i < slaChartData.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            float average_sla = Float.parseFloat(slaChartData.get(i).getAverageSLA());
            values.add(new SubcolumnValue(average_sla, ContextCompat.getColor(this, R.color.completed_color)));

            float no_of_issue = Float.parseFloat(slaChartData.get(i).getNoOfIssues());
            values.add(new SubcolumnValue(no_of_issue, ContextCompat.getColor(this, R.color.white)));

            xAxisValues.add(new AxisValue(i).setLabel(slaChartData.get(i).getDepartment()));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData columnChartData = new ColumnChartData(columns);

        // Set stacked flag.
        columnChartData.setStacked(true);


        ArrayList<Line> lines = new ArrayList<Line>();
//        for (int i = 0; i < 1; ++i) {

        ArrayList<PointValue> pointValues = new ArrayList<PointValue>();

        for (int j = 0; j < slaChartData.size(); ++j) {
//                float point_y = Float.parseFloat(slaChartData.get(j).getAverageSLA()) + Float.parseFloat(slaChartData.get(j).getNoOfIssues());
            pointValues.add(new PointValue(j, Float.parseFloat(slaChartData.get(j).getNoOfIssues())));
        }

        Line line = new Line(pointValues);
        line.setColor(Color.BLACK);
        line.setCubic(true);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);
//        }

        LineChartData lineChartData = new LineChartData(lines);


        ComboLineColumnChartData data = new ComboLineColumnChartData(columnChartData, lineChartData);


        /*------------------set xAxis data--------------------*/
        Axis axisX = new Axis(xAxisValues);
        axisX.setTextSize(10);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("Name of department");
        axisX.setMaxLabelChars(10);
        axisX.setFormatter(new SimpleAxisValueFormatter());
        axisX.setHasTiltedLabels(true);

        data.setAxisXBottom(axisX);
        /*------------------------------------------------------*/

        /*------------------set yAxis data--------------------*/
        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setTextSize(12);
        axisY.setTextColor(Color.BLACK);
//        axisY.setName("No of issue");

        data.setAxisYLeft(axisY);
        /*------------------------------------------------------*/

        chartView.setComboLineColumnChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setClickable(true);
        chartView.setValueTouchEnabled(false);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        if (xAxisValues.size() > 6) {
            chartView.setZoomLevel(0, 0, 2.0f);
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

                    createDepartmentChart(deptChartData);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createDepartmentChart(ArrayList<DepartmentChart> deptChartData) {

        viewBinding.chartLayout.removeAllViews();

        ColumnChartView chartView = new ColumnChartView(ChartContentActivity.this);
        viewBinding.chartLayout.addView(chartView);

        viewBinding.plannedVisitsLayout.setVisibility(View.GONE);
        viewBinding.completedLayout.setVisibility(View.VISIBLE);
        viewBinding.tftCompleted.setText(getString(R.string.completed));
        viewBinding.ongoingLayout.setVisibility(View.VISIBLE);
        viewBinding.overdueLayout.setVisibility(View.VISIBLE);
        viewBinding.noOfIssueLayout.setVisibility(View.GONE);

        ArrayList<Column> columns = new ArrayList<Column>();
        ArrayList<SubcolumnValue> values;
        ArrayList<AxisValue> xAxisValues = new ArrayList<>();

        for (int i = 0; i < deptChartData.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            float completed = Float.parseFloat(deptChartData.get(i).getCompleted());
            values.add(new SubcolumnValue(completed, ContextCompat.getColor(this, R.color.completed_color)));

            float ongoing = Float.parseFloat(deptChartData.get(i).getOnGoing());
            values.add(new SubcolumnValue(ongoing, ContextCompat.getColor(this, R.color.on_going_color)));

            float overdue = Float.parseFloat(deptChartData.get(i).getOverdue());
            values.add(new SubcolumnValue(overdue, ContextCompat.getColor(this, R.color.overdue_color)));

            xAxisValues.add(new AxisValue(i).setLabel(deptChartData.get(i).getDepartment()));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        // Set stacked flag.
        data.setStacked(true);

        /*------------------set xAxis data--------------------*/
        Axis axisX = new Axis(xAxisValues);
        axisX.setTextSize(10);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("Name of department");
        axisX.setMaxLabelChars(10);
        axisX.setFormatter(new SimpleAxisValueFormatter());
        axisX.setHasTiltedLabels(true);

        data.setAxisXBottom(axisX);
        /*------------------------------------------------------*/

        /*------------------set yAxis data--------------------*/
        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setTextSize(12);
        axisY.setTextColor(Color.BLACK);
        axisY.setName("No of issue");

        data.setAxisYLeft(axisY);
        /*------------------------------------------------------*/

        chartView.setColumnChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setClickable(true);
        chartView.setValueTouchEnabled(false);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        if (xAxisValues.size() > 6) {
            chartView.setZoomLevel(0, 0, 2.0f);
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
                    Log.e("tag", "Gson branchChartData:-" + MyApplication.getGson().toJson(branchChartData));

                    createBranchChart(branchChartData);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createBranchChart(ArrayList<BranchChart> branchChartData) {

        viewBinding.chartLayout.removeAllViews();

        ColumnChartView chartView = new ColumnChartView(ChartContentActivity.this);
        viewBinding.chartLayout.addView(chartView);

        viewBinding.plannedVisitsLayout.setVisibility(View.GONE);
        viewBinding.completedLayout.setVisibility(View.VISIBLE);
        viewBinding.tftCompleted.setText(getString(R.string.completed));
        viewBinding.ongoingLayout.setVisibility(View.VISIBLE);
        viewBinding.overdueLayout.setVisibility(View.VISIBLE);
        viewBinding.noOfIssueLayout.setVisibility(View.GONE);

        ArrayList<Column> columns = new ArrayList<Column>();
        ArrayList<SubcolumnValue> values;
        ArrayList<AxisValue> xAxisValues = new ArrayList<>();

        for (int i = 0; i < branchChartData.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            float completed = Float.parseFloat(branchChartData.get(i).getCompleted());
            values.add(new SubcolumnValue(completed, ContextCompat.getColor(this, R.color.completed_color)));

            float ongoing = Float.parseFloat(branchChartData.get(i).getOnGoing());
            values.add(new SubcolumnValue(ongoing, ContextCompat.getColor(this, R.color.on_going_color)));

            float overdue = Float.parseFloat(branchChartData.get(i).getOverdue());
            values.add(new SubcolumnValue(overdue, ContextCompat.getColor(this, R.color.overdue_color)));

            xAxisValues.add(new AxisValue(i).setLabel(branchChartData.get(i).getBranch()));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        // Set stacked flag.
        data.setStacked(true);

        /*------------------set xAxis data--------------------*/
        Axis axisX = new Axis(xAxisValues);
        axisX.setTextSize(12);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("Name of department");

        data.setAxisXBottom(axisX);
        /*------------------------------------------------------*/

        /*------------------set yAxis data--------------------*/
        Axis axisY = new Axis();
        axisY.setHasLines(false);
        axisY.setTextSize(12);
        axisY.setTextColor(Color.BLACK);
        axisY.setName("No of issue");

        data.setAxisYLeft(axisY);
        /*------------------------------------------------------*/

        chartView.setColumnChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setClickable(true);
        chartView.setValueTouchEnabled(false);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        if (xAxisValues.size() > 6) {
            chartView.setZoomLevel(0, 0, 2.0f);
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

        viewBinding.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.setPermission(ChartContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        Functions.showPrompt(ChartContentActivity.this, getString(R.string.yes), getString(R.string.no), getString(R.string.ask_chart_download), new Functions.onPromptListener() {
                            @Override
                            public void onClickYes(MaterialDialog dialog) {

                                saveChartSnapShot();

                            }

                            @Override
                            public void onClickNo(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        SimpleToast.error(ChartContentActivity.this, getString(R.string.permission_denied), getString(R.string.fa_error));
                    }
                });

            }
        });

    }

    private void saveChartSnapShot() {

        String strGenerateName = new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());
        String strChartTypeFolder;

        if (chartType == AppConstants.BRANCH_CHART) {
            strChartTypeFolder = "BranchChart";
        } else if (chartType == AppConstants.DEPARTMENT_CHART) {
            strChartTypeFolder = "DepartmentChart";
        } else if (chartType == AppConstants.DEPARTMENT_SLA_CHART) {
            strChartTypeFolder = "DepartmentSLAChart";
        } else {
            strChartTypeFolder = "VisitPlanChart";
        }

        File mainDerectoryFile = new File(Environment.getExternalStorageDirectory() + AppConstants.CHART_DIRECTORY + strChartTypeFolder + "/");

        if (mainDerectoryFile.exists()) {

            try {
                // create bitmap screen capture
                View v1 = viewBinding.fullChartLayout;
                v1.setBackgroundColor(Color.WHITE);
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                File imageFile = new File(mainDerectoryFile.getAbsoluteFile(), strGenerateName + ".jpg");

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                SimpleToast.ok(ChartContentActivity.this, getString(R.string.chart_download_successfully));

            } catch (Throwable e) {
                e.printStackTrace();
            }

        } else {
            mainDerectoryFile.mkdirs();
            saveChartSnapShot();
        }

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
