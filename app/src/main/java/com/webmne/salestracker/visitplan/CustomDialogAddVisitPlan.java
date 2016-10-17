package com.webmne.salestracker.visitplan;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.custom.LineDividerItemDecoration;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.adapter.CustomDialogVisitPlanAgentListAdapter;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.visitplan.model.VisitPlanAgentListResponse;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.familiarrecyclerview.FamiliarRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by vatsaldesai on 29-09-2016.
 */

public class CustomDialogAddVisitPlan extends MaterialDialog {

    private Context context;
    private MaterialDialog materialDialog;
    private LoadingIndicatorDialog dialog;

    private FamiliarRecyclerView familiarRecyclerView;
    private CustomDialogVisitPlanAgentListAdapter customDialogVisitPlanAgentListAdapter;
    private ArrayList<AgentListModel> agentModelList;
    private TfButton btnCancel, btnOk;
    private EditText edtStartTime, edtEndTime;
    private ProgressBar progressBar;
    private CustomTimePickerDialog customTimePickerDialog;

    private String strStartTime, strEndTime, strAgentId, time, strSelectedStartTime, strSelectedEndTime;
    private int startHour, startminute, endHour, endminute;

    private Calendar currentDate;

    public CustomDialogAddVisitPlan(Calendar currentDate, String time, Builder builder, Context context) {
        super(builder);
        this.currentDate = currentDate;
        this.time = time;
        this.context = context;

        init(builder);
    }

    private void init(Builder builder) {

        materialDialog = builder
//                .title(R.string.add_agent_dialog_title)
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .customView(R.layout.custom_dialog_add_visit_plan, false)
                .canceledOnTouchOutside(false)
                .show();

        View view = materialDialog.getCustomView();

        familiarRecyclerView = (FamiliarRecyclerView) view.findViewById(R.id.agentListRecyclerView);
        btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
        btnOk = (TfButton) view.findViewById(R.id.btnOk);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        edtStartTime = (EditText) view.findViewById(R.id.edtStartTime);
        edtStartTime.setTypeface(Functions.getRegularFont(context));
        edtEndTime = (EditText) view.findViewById(R.id.edtEndTime);
        edtEndTime.setTypeface(Functions.getRegularFont(context));

        if (time.equals("All\nDay")) {
            time = "00:00:00";
        }

        String[] newTime = time.split(":");
        edtStartTime.setText(String.format("%s:%s", newTime[0], newTime[1]));
        strSelectedStartTime = newTime[0] + ":" + newTime[1];
        strSelectedEndTime = "08:00";
        setFullDateTime("s", newTime[0], newTime[1]);

        agentModelList = new ArrayList<>();

        if (PrefUtils.getPlanAgents(context) != null) {
            VisitPlanAgentListResponse prefData = PrefUtils.getPlanAgents(context);
            agentModelList = prefData.getData();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        familiarRecyclerView.setLayoutManager(layoutManager);
        familiarRecyclerView.addItemDecoration(new LineDividerItemDecoration(context));

        customDialogVisitPlanAgentListAdapter = new CustomDialogVisitPlanAgentListAdapter(context, agentModelList);

        familiarRecyclerView.setAdapter(customDialogVisitPlanAgentListAdapter);
        familiarRecyclerView.setHasFixedSize(true);

        familiarRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {

                for (int i = 0; i < agentModelList.size(); i++) {
                    agentModelList.get(i).setChecked(false);
                }

                agentModelList.get(position).setChecked(true);

                customDialogVisitPlanAgentListAdapter.notifyDataSetChanged();

                strAgentId = agentModelList.get(position).getAgentId();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(Functions.toStr(edtStartTime))) {
                    SimpleToast.error(context, context.getString(R.string.pls_select_start_time), context.getString(R.string.fa_error));
                    return;
                }

                if (TextUtils.isEmpty(Functions.toStr(edtEndTime))) {
                    SimpleToast.error(context, context.getString(R.string.pls_select_end_time), context.getString(R.string.fa_error));
                    return;
                }

                if (startHour > endHour) {
                    SimpleToast.error(context, context.getString(R.string.select_between_time), context.getString(R.string.fa_error));
                    return;
                } else if (startHour == endHour) {
                    if (startminute == endminute) {
                        SimpleToast.error(context, context.getString(R.string.select_between_time), context.getString(R.string.fa_error));
                        return;
                    }
                }

                if (TextUtils.isEmpty(strAgentId)) {
                    SimpleToast.error(context, context.getString(R.string.pls_select_agent), context.getString(R.string.fa_error));
                    return;
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("UserId", PrefUtils.getUserId(context));
                    json.put("AgentId", strAgentId);
                    json.put("StartTime", strStartTime);
                    json.put("EndTime", strEndTime);
                    json.put("Position", PrefUtils.getUserProfile(context).getPos_name());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addPlan(json);
            }
        });

        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog("s");
            }
        });

        edtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog("e");

            }
        });

        getAgentList();

    }

    private void addPlan(JSONObject json) {

        showProgress(context.getString(R.string.loading));

        Log.e("json", String.valueOf(json));

        new CallWebService(context, AppConstants.AddPlan, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Log.e("response", response);

                com.webmne.salestracker.api.model.Response addResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);

                if (addResponse != null) {
                    if (addResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        SimpleToast.ok(context, context.getString(R.string.add_visit_plan_success));

                        dismissDialog();

                        ((SalesVisitPlanActivity) context).onResume();

                    } else {
                        SimpleToast.error(context, addResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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

    private void showTimePickerDialog(final String str_flag) {

        customTimePickerDialog = new CustomTimePickerDialog(new MaterialDialog.Builder(context), context, str_flag, strSelectedStartTime, strSelectedEndTime, new CustomTimePickerCallBack() {
            @Override
            public void timePickerCallBack(String hour, String minute) {

                Log.e("tag", str_flag + "," + hour + "," + minute);
                setFullDateTime(str_flag, hour, minute);

            }
        });


//        final Calendar calendar = Calendar.getInstance();


//        CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
////                if(strType == "p")
////                {
////                    et_pick_time.setText(String.format("%02d", hourOfDay) + ":" +String.format("%02d", minute));
////                    strPickTime = String.format("%02d", hourOfDay);
////                }
////                else if(strType == "r")
////                {
////                    et_return_time.setText(String.format("%02d", hourOfDay) + ":" +String.format("%02d", minute));
////                    strReturnTime = String.format("%02d", hourOfDay);
////                }
//
//            }
//        };


//        CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//                Log.e("tag1", calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL);
//
//                Log.e("tag2", hourOfDay + ":" + minute);
//
//            }
//        };
//
//
//        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR), CustomTimePickerDialog.getRoundedMinute(calendar.get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL), true);
//
//        timePickerDialog.setTitle("Select Time");
//        timePickerDialog.show();


//        final int TIME_PICKER_INTERVAL = 15;
//        final boolean[] mIgnoreEvent = {false};
//
//        TimePicker.OnTimeChangedListener mTimePickerListener = new TimePicker.OnTimeChangedListener() {
//            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
//                if (mIgnoreEvent[0])
//                    return;
//                if (minute % TIME_PICKER_INTERVAL != 0) {
//                    int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
//                    minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
//                    if (minute == 60)
//                        minute = 0;
//                    mIgnoreEvent[0] = true;
//                    timePicker.setCurrentMinute(minute);
//                    mIgnoreEvent[0] = false;
//                }
//
//            }
//        };
//
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(context, mTimePickerListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
//
//        timePickerDialog.show();


//        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//
//                Log.e("tag1", calendar.get(Calendar.HOUR)+":"+(calendar.get(Calendar.MINUTE)/10));
//
//                Log.e("tag2", hourOfDay+":"+(minute*10));
//
//                if (str_flag.equals("s")) {
//                    strStartTime = hourOfDay + ":" + minute;
//                    edtStartTime.setText(strStartTime);
//                } else if (str_flag.equals("e")) {
//                    strEndTime = hourOfDay + ":" + minute;
//                    edtEndTime.setText(strEndTime);
//                }
//
//
//            }
//        }, calendar.get(Calendar.HOUR), ((calendar.get(Calendar.MINUTE)/10)), true);
//
//        timePickerDialog.show();

    }

    private void setFullDateTime(String str_flag, String hour, String minute) {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String timeZone = ConstantFormats.zoneFormat.format(calendar.getTime());
        boolean isContain = timeZone.contains(":");
        String newTimeZone;
        if (isContain) {
            newTimeZone = timeZone;
        } else {
            newTimeZone = new StringBuilder(timeZone).insert(timeZone.length() - 2, ":").toString();
        }

        String d = ConstantFormats.ymdFormat.format(currentDate.getTime());

        if (str_flag.equals("s")) {
            startHour = Integer.parseInt(hour);
            startminute = Integer.parseInt(minute);
            strSelectedStartTime = hour + ":" + minute;
            strStartTime = d + "T" + hour + ":" + minute + ":00" + newTimeZone;
            edtStartTime.setText(hour + ":" + minute);

        } else if (str_flag.equals("e")) {
            endHour = Integer.parseInt(hour);
            endminute = Integer.parseInt(minute);
            strSelectedEndTime = hour + ":" + minute;
            strEndTime = d + "T" + hour + ":" + minute + ":00" + newTimeZone;
            edtEndTime.setText(hour + ":" + minute);
        }

    }

    private void getAgentList() {

        progressBar.setVisibility(View.VISIBLE);

        agentModelList = new ArrayList<>();

        new CallWebService(context, AppConstants.AgentForPlan + PrefUtils.getUserId(context), CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                progressBar.setVisibility(View.GONE);

                VisitPlanAgentListResponse visitPlanAgentListResponse = MyApplication.getGson().fromJson(response, VisitPlanAgentListResponse.class);

                if (visitPlanAgentListResponse != null) {

                    if (visitPlanAgentListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        PrefUtils.setPlanAgents(context, visitPlanAgentListResponse);
                        agentModelList = visitPlanAgentListResponse.getData();
                        customDialogVisitPlanAgentListAdapter.setAgentList(agentModelList);

                    } else {
                        SimpleToast.error(context, visitPlanAgentListResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
                    }

                } else {
                    SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                }
            }

            @Override
            public void error(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                VolleyErrorHelper.showErrorMsg(error, context);
            }

            @Override
            public void noInternet() {
                progressBar.setVisibility(View.GONE);
                SimpleToast.error(context, context.getString(R.string.no_internet_connection), context.getString(R.string.fa_error));
            }
        }.call();

    }

    public void dismissDialog() {
        if (materialDialog != null) {
            if (materialDialog.isShowing()) {
                materialDialog.dismiss();
            }
        }
    }

    public void showProgress(String str) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(context, str, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


}
