package com.webmne.salestracker.api;

import com.webmne.salestracker.api.model.LoginResponse;
import com.webmne.salestracker.helper.AppConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public interface AppApi {

    @GET(AppConstants.LoginBase)
    Call<LoginResponse> login(@Query("format") String format, @Query("password") String password, @Query("username") String username);

}
