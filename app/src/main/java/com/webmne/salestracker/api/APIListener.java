package com.webmne.salestracker.api;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sagartahelyani on 26-08-2016.
 */
public interface APIListener<T> {

    void onResponse(Response<T> response);

    void onFailure(Call<T> call, Throwable t);
}
