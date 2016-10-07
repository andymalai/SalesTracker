package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.DeleteMappingRequest;
import com.webmne.salestracker.api.model.DeleteMappingResponse;
import com.webmne.salestracker.api.model.TierListResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 02-09-2016.
 */
public class DeleteMappingApi {

    private AppApi appApi;
    private APIListener apiListener;

    public DeleteMappingApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void deleteMapping(DeleteMappingRequest deleteMappingRequest, final APIListener<DeleteMappingResponse> apiListener) {
        Call<DeleteMappingResponse> call = appApi.deleteMapping(deleteMappingRequest);
        call.enqueue(new Callback<DeleteMappingResponse>() {
            @Override
            public void onResponse(Call<DeleteMappingResponse> call, Response<DeleteMappingResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<DeleteMappingResponse> call, Throwable t) {
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
