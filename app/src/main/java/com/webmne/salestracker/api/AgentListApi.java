package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.AgentListResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class AgentListApi {

    private AppApi appApi;
    private APIListener apiListener;

    public AgentListApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void getAgents(String userid, final APIListener<AgentListResponse> apiListener) {

        Call<AgentListResponse> call = appApi.getAgents(userid);
        call.enqueue(new Callback<AgentListResponse>() {
            @Override
            public void onResponse(Call<AgentListResponse> call, Response<AgentListResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<AgentListResponse> call, Throwable t) {
                apiListener.onFailure(call, t);
            }
        });
    }

    public void onDestroy() {
        if (appApi != null) {
            appApi = null;
        }
        if (apiListener != null) {
            apiListener = null;
        }
    }
}
