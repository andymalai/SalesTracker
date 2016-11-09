package com.webmne.salestracker.api.call;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.DepartmentChart;
import com.webmne.salestracker.api.model.DepartmentChartResponse;
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

public class GetDepartmentChart {

    private Context context;
    private JSONObject jsonObject;
    private LoadingIndicatorDialog dialog;
    private ArrayList<DepartmentChart> deptChartData;
    private OnGetChartListener OnGetChartListener;

    public GetDepartmentChart(Context context, JSONObject jsonObject, OnGetChartListener OnGetChartListener) {
        this.context = context;
        this.OnGetChartListener = OnGetChartListener;
        this.jsonObject = jsonObject;
        callWs();
    }

    private void callWs() {
        showProgress();

        new CallWebService(context, AppConstants.DeptChart, CallWebService.TYPE_POST, jsonObject) {
            @Override
            public void response(String response) {
                dismissProgress();

                DepartmentChartResponse deptChartResponse = MyApplication.getGson().fromJson(response, DepartmentChartResponse.class);
                if (deptChartResponse != null) {
                    if (deptChartResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        deptChartData = deptChartResponse.getData().getDepartmentData();
                        if (OnGetChartListener != null) {
                            OnGetChartListener.setDeptChart(deptChartData);
                        }
                    } else {
                        SimpleToast.error(context, deptChartResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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
        void setDeptChart(ArrayList<DepartmentChart> deptChartData);
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
