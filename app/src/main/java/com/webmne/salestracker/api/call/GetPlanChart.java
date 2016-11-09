package com.webmne.salestracker.api.call;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.PlanChart;
import com.webmne.salestracker.api.model.PlanChartResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class GetPlanChart {

    private Context context;
    private LoadingIndicatorDialog dialog;
    private JSONObject jsonObject;
    private ArrayList<PlanChart> planChartData;
    private OnGetChartListener OnGetChartListener;

    public GetPlanChart(Context context, JSONObject jsonObject, GetPlanChart.OnGetChartListener onGetChartListener) {
        this.context = context;
        this.jsonObject = jsonObject;
        OnGetChartListener = onGetChartListener;
        callWs();
    }

    private void callWs() {
        showProgress();

        new CallWebService(context, AppConstants.PlanChart, CallWebService.TYPE_POST, jsonObject) {
            @Override
            public void response(String response) {
                dismissProgress();

                PlanChartResponse planChartResponse = MyApplication.getGson().fromJson(response, PlanChartResponse.class);
                if (planChartResponse != null) {
                    if (planChartResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        planChartData = planChartResponse.getData().getDate();
                        if (OnGetChartListener != null) {
                            OnGetChartListener.setPlanChart(planChartData);
                        }
                    } else {
                        SimpleToast.error(context, planChartResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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

    private void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void showProgress() {
        if (dialog == null) {
            dialog = new LoadingIndicatorDialog(context, context.getString(R.string.loading), android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.show();
    }

    public interface OnGetChartListener {
        void setPlanChart(ArrayList<PlanChart> planChartData);
    }


}
