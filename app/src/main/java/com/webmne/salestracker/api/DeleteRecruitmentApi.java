package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.DeleteRecruitmentRequest;
import com.webmne.salestracker.api.model.DeleteRecruitmentResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class DeleteRecruitmentApi {

    private AppApi appApi;
    private APIListener apiListener;

    public DeleteRecruitmentApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void deleteRecruitment(DeleteRecruitmentRequest deleteRecruitmentRequest, final APIListener<DeleteRecruitmentResponse> apiListener) {
        Call<DeleteRecruitmentResponse> call = appApi.deleteRecruitment(deleteRecruitmentRequest);
        call.enqueue(new Callback<DeleteRecruitmentResponse>() {
            @Override
            public void onResponse(Call<DeleteRecruitmentResponse> call, Response<DeleteRecruitmentResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<DeleteRecruitmentResponse> call, Throwable t) {
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
