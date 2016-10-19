package com.webmne.salestracker.api.call;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.Branch;
import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
import com.webmne.salestracker.event.AddEventActivity;
import com.webmne.salestracker.helper.AppConstants;
import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.helper.volley.CallWebService;
import com.webmne.salestracker.helper.volley.VolleyErrorHelper;
import com.webmne.salestracker.visitplan.model.AgentListModel;
import com.webmne.salestracker.visitplan.model.VisitPlanAgentListResponse;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 18-10-2016.
 */

public class GetBranches {

    private Context context;
    private String regionId;
    private ArrayList<Branch> agentModelList = new ArrayList<>();
    private OnGetBranchListener OnGetBranchListener;
    private LoadingIndicatorDialog dialog;

    public GetBranches(Context context, String regionId, OnGetBranchListener OnGetBranchListener) {
        this.context = context;
        this.regionId = regionId;
        this.OnGetBranchListener = OnGetBranchListener;
        callWS();
    }

    private void callWS() {

        showProgress();

        Log.e("call", AppConstants.Branch + "&regionid=" + regionId);
        new CallWebService(context, AppConstants.Branch + "&regionid=" + regionId, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                BranchListResponse listResponse = MyApplication.getGson().fromJson(response, BranchListResponse.class);

                if (listResponse != null) {

                    if (listResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {

                        agentModelList = new ArrayList<>();
                        agentModelList = listResponse.getData().getBranches();
                        if (OnGetBranchListener != null) {
                            OnGetBranchListener.getBranches(agentModelList);
                        }

                    } else {
                        SimpleToast.error(context, listResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
                    }

                } else {
                    // SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                    if (OnGetBranchListener != null) {
                        OnGetBranchListener.getBranches(agentModelList);
                    }
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

    public interface OnGetBranchListener {
        public void getBranches(ArrayList<Branch> agentList);
    }
}
