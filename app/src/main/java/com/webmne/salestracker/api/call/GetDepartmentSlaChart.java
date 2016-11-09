package com.webmne.salestracker.api.call;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.SlaChart;
import com.webmne.salestracker.api.model.SlaChartDataResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 08-11-2016.
 */

public class GetDepartmentSlaChart {

    private Context context;
    private JSONObject jsonObject;
    private LoadingIndicatorDialog dialog;
    private OnGetChartListener OnGetChartListener;
    private ArrayList<SlaChart> slaChartData;

    public GetDepartmentSlaChart(Context context, JSONObject jsonObject, GetDepartmentSlaChart.OnGetChartListener onGetChartListener) {
        this.context = context;
        OnGetChartListener = onGetChartListener;
        this.jsonObject = jsonObject;
        callWs();
    }

    private void callWs() {
        showProgress();

        new CallWebService(context, AppConstants.DeptSlaChart, CallWebService.TYPE_POST, jsonObject) {
            @Override
            public void response(String response) {
                dismissProgress();

                SlaChartDataResponse slaChartResponse = MyApplication.getGson().fromJson(response, SlaChartDataResponse.class);
                if (slaChartResponse != null) {
                    if (slaChartResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        slaChartData = slaChartResponse.getData().getSlaData();
                        if (OnGetChartListener != null) {
                            OnGetChartListener.setSlaChart(slaChartData);
                        }
                    } else {
                        SimpleToast.error(context, slaChartResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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
        void setSlaChart(ArrayList<SlaChart> slaChart);
    }
}
