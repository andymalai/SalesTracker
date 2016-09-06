package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.BranchListResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class BranchApi {

    private AppApi appApi;
    private APIListener apiListener;

    public BranchApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void getBranchList(final APIListener<BranchListResponse> apiListener) {
        Call<BranchListResponse> call = appApi.getBranchList();
        call.enqueue(new Callback<BranchListResponse>() {
            @Override
            public void onResponse(Call<BranchListResponse> call, Response<BranchListResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<BranchListResponse> call, Throwable t) {
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
