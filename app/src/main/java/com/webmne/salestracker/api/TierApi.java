package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class TierApi {

    private AppApi appApi;
    private APIListener apiListener;

    public TierApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void getTierList(final APIListener<TierListResponse> apiListener) {
        Call<TierListResponse> call = appApi.getTier();
        call.enqueue(new Callback<TierListResponse>() {
            @Override
            public void onResponse(Call<TierListResponse> call, Response<TierListResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<TierListResponse> call, Throwable t) {
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
