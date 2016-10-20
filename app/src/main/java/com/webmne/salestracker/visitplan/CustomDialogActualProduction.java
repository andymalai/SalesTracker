package com.webmne.salestracker.visitplan;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.Functions;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.widget.TfButton;
import com.webmne.salestracker.widget.TfEditText;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by vatsaldesai on 30-09-2016.
 */

public class CustomDialogActualProduction extends MaterialDialog {

    private Context context;
    private MaterialDialog materialDialog;
    private LoadingIndicatorDialog dialog;
    private Calendar currentCalendar;

    private TfEditText edtProgress;
    private TfButton btnCancel, btnOk;

    String strProgress;

    protected CustomDialogActualProduction(Builder builder, Context context, String strProgress, Calendar currentCalendar) {
        super(builder);

        this.context = context;
        this.strProgress = strProgress;
        this.currentCalendar = currentCalendar;

        init(builder);
    }

    private void init(Builder builder) {

        materialDialog = builder
                .title(R.string.actual_production_dialog_title)
                .typeface(Functions.getBoldFont(context), Functions.getRegularFont(context))
                .customView(R.layout.custom_dialog_actual_production, false)
                .canceledOnTouchOutside(false)
                .show();

        View view = materialDialog.getCustomView();

        edtProgress = (TfEditText) view.findViewById(R.id.edtProgress);
        btnCancel = (TfButton) view.findViewById(R.id.btnCancel);
        btnOk = (TfButton) view.findViewById(R.id.btnOk);

        edtProgress.setText(strProgress);

        actionListener();
    }

    private void actionListener() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.hideKeyPad(context, v);

                if (!TextUtils.isEmpty(Functions.toStr(edtProgress))) {

                    updateProduction();

                } else {
                    SimpleToast.error(context, context.getString(R.string.enter_progress), context.getString(R.string.fa_error));
                }
            }
        });

    }


    private void updateProduction() {

        showProgress(context.getString(R.string.loading));

        JSONObject json = new JSONObject();
        try {
            json.put("UserId", PrefUtils.getUserId(context));
            json.put("Month", currentCalendar.get(Calendar.MONTH)+1);
            json.put("Year", currentCalendar.get(Calendar.YEAR));
            json.put("Production", Functions.toStr(edtProgress));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("json", String.valueOf(json));

        new CallWebService(context, AppConstants.UpdateProduction, CallWebService.TYPE_POST, json) {

            @Override
            public void response(String response) {
                dismissProgress();

                Log.e("response", response);

                com.webmne.salestracker.api.model.Response addResponse = MyApplication.getGson().fromJson(response, com.webmne.salestracker.api.model.Response.class);

                if (addResponse != null) {
                    if (addResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        SimpleToast.ok(context, context.getString(R.string.progress_update_success));

                        dismissProgress();

                        materialDialog.dismiss();

                        ((SalesVisitPlanActivity)context).onResume();

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


    public void showProgress(String string) {
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
