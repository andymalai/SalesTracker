package com.webmne.salestracker.widget.calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Plan;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.CustomDialogAddVisitPlan;
import com.webmne.salestracker.visitplan.CustomDialogAddVisitPlanCallBack;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;
import com.webmne.salestracker.widget.TfTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    DayPlanAdapter(Context context, ArrayList<Plan> plans, ArrayList<TimeLineHour> timeLineHours, Calendar currentDate) {
        this.plans = plans;
        this.timeLineHours = timeLineHours;
        this.context = context;
        this.currentDate = currentDate;
        todayCalendar = Calendar.getInstance();
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int pos) {

        holder.subView.setVisibility(View.GONE);

        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getPosition() == pos) {
                holder.subView.setVisibility(View.VISIBLE);
                Plan plan = plans.get(i);
                holder.setEvent(plan);
                break;
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

        private TfTextView name, remark;
        private LinearLayout subView, actionLayout, planView;
        private RelativeLayout parentView;
        private ImageView imgDelete;
        private ImageButton bView, oView, xView;

        private EventHolder(View itemView) {
            super(itemView);
            name = (TfTextView) itemView.findViewById(R.id.name);
            remark = (TfTextView) itemView.findViewById(R.id.remark);
            subView = (LinearLayout) itemView.findViewById(R.id.subView);
            planView = (LinearLayout) itemView.findViewById(R.id.planView);
            parentView = (RelativeLayout) itemView.findViewById(R.id.parentView);
            actionLayout = (LinearLayout) itemView.findViewById(R.id.actionLayout);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);

            bView = (ImageButton) itemView.findViewById(R.id.bView);
            oView = (ImageButton) itemView.findViewById(R.id.oView);
            xView = (ImageButton) itemView.findViewById(R.id.xView);

            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parentView.getChildAt(0).getVisibility() == View.VISIBLE) {

                    } else {
                        new CustomDialogAddVisitPlan(currentDate, timeLineHours.get(getAdapterPosition()).getTime(),
                                new MaterialDialog.Builder(context), context);
                    }
                }
            });
        }

        void setEvent(final Plan plan) {

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title(context.getString(R.string.delete_plan))
                            .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                            .positiveText(context.getString(R.string.yes))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    deletePlan(plan);
                                }
                            })
                            .negativeText(context.getString(R.string.no))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .content(context.getString(R.string.ask_for_delete_plan))
                            .show();
                }
            });

            planView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog dialog = new MaterialDialog.Builder(context)
                            .title(R.string.dialog_title)
                            .customView(R.layout.custom_dialog_action_log_edit, true)
                            .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                            .canceledOnTouchOutside(false)
                            .show();
                    initDialog(dialog, plan);
                }
            });

            subView.setVisibility(View.VISIBLE);
            name.setText(plan.getAgentName());
            remark.setText(plan.getRemark());

            Date curDate = new Date();
            Date actualDate = new Date();

            curDate.setTime(todayCalendar.getTimeInMillis());
            actualDate.setTime(currentDate.getTimeInMillis());

            if (curDate.before(actualDate)) {
                actionLayout.setVisibility(View.GONE);
                imgDelete.setVisibility(View.VISIBLE);

            } else if (curDate.after(actualDate)) {
                actionLayout.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.GONE);

            } else {
                actionLayout.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.GONE);
            }

            if (plan.getStatus().equals(AppConstants.B)) {
                bView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
                oView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                xView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));

            } else if (plan.getStatus().equals(AppConstants.O)) {
                oView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
                oView.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

                bView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));

                xView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                xView.setColorFilter(ContextCompat.getColor(context, R.color.half_black), PorterDuff.Mode.SRC_ATOP);

            } else if (plan.getStatus().equals(AppConstants.X)) {
                xView.setBackgroundColor(ContextCompat.getColor(context, R.color.tile1));
                xView.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

                bView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                oView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                oView.setColorFilter(ContextCompat.getColor(context, R.color.half_black), PorterDuff.Mode.SRC_ATOP);
            }

            bView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(AppConstants.B, plan);
                }
            });

            oView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(AppConstants.O, plan);
                }
            });

            xView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(AppConstants.X, plan);
                }
            });
        }
    }

    private void deletePlan(final Plan plan) {

        showProgress(context.getString(R.string.delete));

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
                        SimpleToast.ok(context, context.getString(R.string.plan_deleted));
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
                    plan.setRemark(Functions.toStr(editText));
                    doUpdateRemark(plan);
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

    private void doUpdateRemark(Plan plan) {

        showProgress(context.getString(R.string.loading));
        JSONObject json = new JSONObject();
        try {
            json.put("PlanId", plan.getPlanId());
            json.put("Remark", plan.getRemark());
            Log.e("update_remark", json.toString());

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

    private void update(final String b, final Plan plan) {

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
