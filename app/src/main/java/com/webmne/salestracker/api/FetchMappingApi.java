package com.webmne.salestracker.api;

import com.webmne.salestracker.api.APIListener;
import com.webmne.salestracker.api.AppApi;
import com.webmne.salestracker.api.model.FetchMappingRequest;
import com.webmne.salestracker.api.model.FetchMappingResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class FetchMappingApi {

    private AppApi appApi;
    private APIListener apiListener;

    public FetchMappingApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void fetchMappingData(FetchMappingRequest fetchMappingRequest, final APIListener<FetchMappingResponse> apiListener) {
        Call<FetchMappingResponse> call = appApi.fetchMappingData(fetchMappingRequest);
        call.enqueue(new Callback<FetchMappingResponse>() {
            @Override
            public void onResponse(Call<FetchMappingResponse> call, Response<FetchMappingResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<FetchMappingResponse> call, Throwable t) {
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
