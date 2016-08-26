package com.webmne.salestracker.helper;

import android.app.Application;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * Created by sagartahelyani on 11-08-2016.
 */
public class MyApplication extends Application {

    private static Retrofit retrofit;
    private static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
        initGson();
    }

    private void initGson() {
        gson = new Gson();
    }

    public static Gson getGson() {
        return gson;
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
