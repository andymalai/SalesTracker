package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.FetchMappingRequest;
import com.webmne.salestracker.api.model.FetchMappingResponse;
import com.webmne.salestracker.api.model.FetchRecruitmentData;
import com.webmne.salestracker.api.model.FetchRecruitmentRequest;
import com.webmne.salestracker.api.model.FetchRecruitmentResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 05-09-2016.
 */
public class FetchRecruitmentApi {

    private AppApi appApi;
    private APIListener apiListener;

    public FetchRecruitmentApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void fetchRecruitmentData(FetchRecruitmentRequest fetchRecruitmentRequest, final APIListener<FetchRecruitmentResponse> apiListener) {
        Call<FetchRecruitmentResponse> call = appApi.fetchRecruitmentData(fetchRecruitmentRequest);
        call.enqueue(new Callback<FetchRecruitmentResponse>() {
            @Override
            public void onResponse(Call<FetchRecruitmentResponse> call, Response<FetchRecruitmentResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<FetchRecruitmentResponse> call, Throwable t) {
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
