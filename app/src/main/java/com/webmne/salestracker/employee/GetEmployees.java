package com.webmne.salestracker.employee;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.EmployeeListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.employee.model.EmployeeModel;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 18-10-2016.
 */

public class GetEmployees {

    private Context context;
    private String userId;
    private ArrayList<EmployeeModel> agentModelList = new ArrayList<>();
    private OnGetEmpListener OnGetEmpListener;
    private LoadingIndicatorDialog dialog;

    public GetEmployees(Context context, String userId, OnGetEmpListener OnGetEmpListener) {
        this.context = context;
        this.userId = userId;
        this.OnGetEmpListener = OnGetEmpListener;
        callWS();
    }

    private void callWS() {

        showProgress();

        new CallWebService(context, AppConstants.EmployeeList + userId, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                EmployeeListResponse listResponse = MyApplication.getGson().fromJson(response, EmployeeListResponse.class);

                if (listResponse != null) {

                    if (listResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        agentModelList = listResponse.getData().getEmployee();
                        if (OnGetEmpListener != null) {
                            OnGetEmpListener.getEmployees(agentModelList);
                        }
                    } else {
                        SimpleToast.error(context, listResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
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

    public interface OnGetEmpListener {
        void getEmployees(ArrayList<EmployeeModel> agentList);
    }
}
