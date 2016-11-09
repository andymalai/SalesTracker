package com.webmne.salestracker.api.call;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.BranchChart;
import com.webmne.salestracker.api.model.BranchChartResponse;
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

public class GetBranchChart {

    private Context context;
    private JSONObject jsonObject;
    private LoadingIndicatorDialog dialog;
    private OnGetChartListener OnGetChartListener;
    private ArrayList<BranchChart> branchChartData;

    public GetBranchChart(Context context, JSONObject jsonObject, OnGetChartListener OnGetChartListener) {
        this.context = context;
        this.OnGetChartListener = OnGetChartListener;
        this.jsonObject = jsonObject;
        callWs();
    }

    private void callWs() {
        showProgress();

        new CallWebService(context, AppConstants.BranchChart, CallWebService.TYPE_POST, jsonObject) {
            @Override
            public void response(String response) {
                dismissProgress();

                BranchChartResponse branchChartResponse = MyApplication.getGson().fromJson(response, BranchChartResponse.class);
                if (branchChartResponse != null) {
                    if (branchChartResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        branchChartData = branchChartResponse.getData().getBranchData();
                        if (OnGetChartListener != null) {
                            OnGetChartListener.setBranchChart(branchChartData);
                        }
                    } else {
                        SimpleToast.error(context, branchChartResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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

    public interface OnGetChartListener {
        void setBranchChart(ArrayList<BranchChart> branchChartData);
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

}
