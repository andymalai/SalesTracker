package com.webmne.salestracker.visitplan;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.PlanDataResponse;
import com.webmne.salestracker.api.model.SalesPlanResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ActivitySalesVisitPlanBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.adapter.CustomDialogVisitPlanAgentListAdapter;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.calendar.CalendarView;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

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

        // call fetch plan WS with current month and year
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        fetchPlan(month, year);
    }

    private void fetchPlan(int month, int year) {

        showProgress(getString(R.string.loading));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserId", PrefUtils.getUserId(SalesVisitPlanActivity.this));
            jsonObject.put("Month", month);
            jsonObject.put("Year", year);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("fetch_plan_req", jsonObject.toString());

        new CallWebService(this, AppConstants.PlanList, CallWebService.TYPE_POST, jsonObject) {

            @Override
            public void response(String response) {
                dismissProgress();

                SalesPlanResponse planResponse = MyApplication.getGson().fromJson(response, SalesPlanResponse.class);
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
        binding.cv.setOnGridSelectListener(new CalendarView.onGridSelectListener() {
            @Override
            public void onGridSelect(Calendar c) {
                Toast.makeText(SalesVisitPlanActivity.this, "Select " + ConstantFormats.dateFormat.format(c.getTime()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void dayClicked(View view) {
        binding.cv.setMode(CalendarView.MODE.DAY);
    }

    public void weekClicked(View view) {
        binding.cv.setMode(CalendarView.MODE.WEEK);
    }

    public void monthClicked(View view) {
        binding.cv.setMode(CalendarView.MODE.MONTH);
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


}
