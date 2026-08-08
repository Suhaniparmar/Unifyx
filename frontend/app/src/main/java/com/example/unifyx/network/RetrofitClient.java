package com.example.unifyx.network;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private static Retrofit retrofit;

    public RetrofitClient(){
        initializerRetrofit();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    private void initializerRetrofit(){
        retrofit = new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/").addConverterFactory(GsonConverterFactory.create(new Gson()))
        .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
