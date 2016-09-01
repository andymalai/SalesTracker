package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.LoginResponse;
import com.webmne.salestracker.helper.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public class LoginApi {

    private AppApi appApi;
    private APIListener apiListener;

    public LoginApi() {
        appApi = MyApplication.getRetrofit().create(AppApi.class);
    }

    public void login(String json, String password, String username, final APIListener<LoginResponse> apiListener) {

        Call<LoginResponse> profileCall = appApi.login(json, password, username);

        profileCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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

