package com.webmne.salestracker.api;

import com.webmne.salestracker.helper.MyApplication;
import com.webmne.salestracker.ui.model.UserProfile;

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

    public void login(String json, String password, String username, String roleId, final APIListener<UserProfile> apiListener) {

        Call<UserProfile> profileCall = appApi.login(json, password, username, roleId);

        profileCall.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                apiListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
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

