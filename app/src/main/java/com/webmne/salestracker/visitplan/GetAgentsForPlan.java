package com.webmne.salestracker.visitplan;

import android.content.Context;

import com.android.volley.VolleyError;
import com.github.pierry.simpletoast.SimpleToast;
import com.webmne.salestracker.R;
import com.webmne.salestracker.custom.LoadingIndicatorDialog;
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

public class GetAgentsForPlan {

    private Context context;
    private String userId;
    private ArrayList<AgentListModel> agentModelList = new ArrayList<>();
    private OnGetAgentsListener OnGetAgentsListener;
    private LoadingIndicatorDialog dialog;

    public GetAgentsForPlan(Context context, String userId, OnGetAgentsListener OnGetAgentsListener) {
        this.context = context;
        this.userId = userId;
        this.OnGetAgentsListener = OnGetAgentsListener;
        callWS();
    }

    private void callWS() {

        showProgress();

        new CallWebService(context, AppConstants.AgentForPlan + userId, CallWebService.TYPE_GET) {

            @Override
            public void response(String response) {

                dismissProgress();

                VisitPlanAgentListResponse visitPlanAgentListResponse = MyApplication.getGson().fromJson(response, VisitPlanAgentListResponse.class);

                if (visitPlanAgentListResponse != null) {

                    if (visitPlanAgentListResponse.getResponse().getResponseCode().equals(AppConstants.SUCCESS)) {
                        PrefUtils.setPlanAgents(context, visitPlanAgentListResponse);
                        agentModelList = visitPlanAgentListResponse.getData();
                        if (OnGetAgentsListener != null) {
                            OnGetAgentsListener.getAgents(agentModelList);
                        }

                    } else {
                       // SimpleToast.error(context, visitPlanAgentListResponse.getResponse().getResponseMsg(), context.getString(R.string.fa_error));
                        if (OnGetAgentsListener != null) {
                            OnGetAgentsListener.getAgents(agentModelList);
                        }
                    }

                } else {
                   // SimpleToast.error(context, context.getString(R.string.try_again), context.getString(R.string.fa_error));
                    if (OnGetAgentsListener != null) {
                        OnGetAgentsListener.getAgents(agentModelList);
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

    public interface OnGetAgentsListener {
        public void getAgents(ArrayList<AgentListModel> agentList);
    }
}
