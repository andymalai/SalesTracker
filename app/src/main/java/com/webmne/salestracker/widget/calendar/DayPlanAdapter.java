package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.databinding.ItemEventBinding;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.ConstantFormats;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.CustomDialogAddVisitPlan;
import com.webmne.salestracker.visitplan.CustomTimePickerCallBack;
import com.webmne.salestracker.visitplan.CustomTimePickerDialog;
import com.webmne.salestracker.widget.PlanItem;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sagartahelyani on 30-09-2016.
 */

class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.EventHolder> {

    private ArrayList<Plan> plans;
    private ArrayList<TimeLineHour> timeLineHours;
    private Context context;
    private Calendar currentDate;
    private Calendar todayCalendar;
    private LoadingIndicatorDialog dialog;

    int startHour, startminute, endHour, endminute;
    String strSelectedStartTime, strSelectedEndTime, strStartTime, strEndTime;

    DayPlanAdapter(Context context, ArrayList<Plan> plans, ArrayList<TimeLineHour> timeLineHours, Calendar currentDate) {
        this.plans = plans;
        this.timeLineHours = timeLineHours;
        this.context = context;
        this.currentDate = currentDate;
        todayCalendar = Calendar.getInstance();
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemEventBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event, parent, false);
        return new EventHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final EventHolder holder, int pos) {

        holder.newBinding.subView.removeAllViews();
        holder.newBinding.subView.invalidate();

        holder.newBinding.contentView.setVisibility(View.GONE);

        holder.newBinding.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDialogAddVisitPlan(currentDate, timeLineHours.get(holder.getAdapterPosition()).getTime(),
                        new MaterialDialog.Builder(context), context);

            }
        });

        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getPosition() == pos) {
                Plan plan = plans.get(i);
                holder.setEvent(plan);
            }
        }
    }

    @Override
    public int getItemCount() {
        return timeLineHours.size();
    }

    void setDayPlan(ArrayList<Plan> newPlans, Calendar currentDate) {
        this.plans = newPlans;
        this.currentDate = currentDate;
        notifyDataSetChanged();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        private ItemEventBinding newBinding;

        private EventHolder(View itemView) {
            super(itemView);
            this.newBinding = DataBindingUtil.bind(itemView);
        }

        void setEvent(final Plan plan) {

            newBinding.contentView.setVisibility(View.VISIBLE);

            PlanItem planItem = new PlanItem(context, plan, currentDate);
            planItem.setOnPlanChangeListener(new PlanItem.onPlanChangeListener() {
                @Override
                public void onChange(int type, Plan plan, String box) {
                    switch (type) {
                        case AppConstants.DELETE_PLAN:
                            doDelete(plan);
                            break;

                        case AppConstants.OPEN_REMARK:
                            openRemarkDialog(plan);
                            break;

                        case AppConstants.UPDATE_BOX:
                            updateBox(box, plan);
                            break;
                    }
                }
            });
            newBinding.subView.addView(planItem);
        }
    }

    private void openRemarkDialog(Plan plan) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.update_plan)
                .customView(R.layout.dialog_update_plan, true)
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .canceledOnTouchOutside(false)
                .show();
        initDialog(dialog, plan);
    }

    private void doDelete(final Plan plan) {

        new MaterialDialog.Builder(context)
                .title("Delete Plan")
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .positiveText("Yes")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deletePlan(plan);
                    }
                })
                .negativeText("No")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .content("Are you sure want to delete this plan?")
                .show();
    }

    private void deletePlan(final Plan plan) {
        showProgress(context.getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("PlanId", plan.getPlanId());
            json.put("Date", "");
            Log.e("delete_plan", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(context, AppConstants.DeletePlan, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(context, context.getString(R.string.update_status));
                        plans.remove(plan);
                        notifyDataSetChanged();

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

    private void initDialog(final MaterialDialog dialog, final Plan plan) {
        View view = dialog.getCustomView();
        if (view != null) {
            LinearLayout parentView = (LinearLayout) view.findViewById(R.id.parentView);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            final TfEditText editText = (TfEditText) view.findViewById(R.id.edtDesc);
            final EditText edtStartTime = (EditText) view.findViewById(R.id.edtStartTime);
            final EditText edtEndTime = (EditText) view.findViewById(R.id.edtEndTime);

            String[] start = plan.getStartTime().split(":");
            // edtStartTime.setText(start[0] + ":" + start[1]);
            strSelectedStartTime = start[0] + ":" + start[1];

            setFullDateTime("s", start[0], start[1], edtStartTime);

            String[] end = plan.getEndTime().split(":");
            //  edtEndTime.setText(end[0] + ":" + end[1]);
            strSelectedEndTime = end[0] + ":" + end[1];

            setFullDateTime("e", end[0], end[1], edtEndTime);

            edtStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showTimePickerDialog("s", edtStartTime);
                }
            });

            edtEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePickerDialog("e", edtEndTime);

                }
            });

            if (plan.getRemark().equals("0")) {
                editText.setText("");

            } else {
                editText.setText(plan.getRemark());
            }

            TfButton btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
            TfButton btnOk = (TfButton) view.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("strt", strStartTime);
                    Log.e("end", strEndTime);

                    plan.setStartTime(Functions.toStr(edtStartTime) + ":00");
                    plan.setEndTime(Functions.toStr(edtEndTime) + ":00");
                    notifyDataSetChanged();
                    plan.setRemark(Functions.toStr(editText));
                    doUpdatePlan(plan);
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }

    private void showTimePickerDialog(final String str_flag, final EditText edtEndTime) {
        new CustomTimePickerDialog(new MaterialDialog.Builder(context), context, str_flag, strSelectedStartTime, strSelectedEndTime, new CustomTimePickerCallBack() {
            @Override
            public void timePickerCallBack(String hour, String minute) {

                Log.e("tag", str_flag + "," + hour + "," + minute);
                setFullDateTime(str_flag, hour, minute, edtEndTime);

            }
        });
    }

    private void setFullDateTime(String str_flag, String hour, String minute, EditText edtEndTime) {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String timeZone = ConstantFormats.zoneFormat.format(calendar.getTime());
        boolean isContain = timeZone.contains(":");
        String newTimeZone;
        if (isContain) {
            newTimeZone = timeZone;
            Log.e("newTimeZone_1", newTimeZone);
        } else {
            newTimeZone = new StringBuilder(timeZone).insert(timeZone.length() - 2, ":").toString();
            Log.e("newTimeZone_2", newTimeZone);
        }

        String d = ConstantFormats.ymdFormat.format(currentDate.getTime());

        if (str_flag.equals("s")) {
            startHour = Integer.parseInt(hour);
            startminute = Integer.parseInt(minute);
            strSelectedStartTime = hour + ":" + minute;
            strStartTime = d + "T" + hour + ":" + minute + ":00" + newTimeZone;
            edtEndTime.setText(hour + ":" + minute);

        } else if (str_flag.equals("e")) {
            endHour = Integer.parseInt(hour);
            endminute = Integer.parseInt(minute);
            strSelectedEndTime = hour + ":" + minute;
            strEndTime = d + "T" + hour + ":" + minute + ":00" + newTimeZone;
            edtEndTime.setText(hour + ":" + minute);
        }
    }

    private void doUpdatePlan(Plan plan) {

        showProgress(context.getString(R.string.loading));
        JSONObject json = new JSONObject();
        try {
            json.put("PlanId", plan.getPlanId());
            json.put("StartTime", strStartTime);
            json.put("EndTime", strEndTime);
            json.put("Remark", plan.getRemark());

            Log.e("update_plan", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(context, AppConstants.UpdateRemarkPlan, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(context, context.getString(R.string.update_status));
                        notifyDataSetChanged();

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

    private void updateBox(final String b, final Plan plan) {

        showProgress(context.getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("PlanId", plan.getPlanId());
            json.put("Status", b);
            Log.e("update_box", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallWebService(context, AppConstants.BOXUpdate, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                com.webmne.salestracker.api.model.Response wsResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);
                if (wsResponse != null) {
                    if (wsResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        SimpleToast.ok(context, context.getString(R.string.update_status));
                        plan.setStatus(b);
                        notifyDataSetChanged();

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

    private void showProgress(String string) {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(context, string, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
