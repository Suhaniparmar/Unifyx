package com.example.unifyx.network;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public RetrofitClient(){
        initializerRetrofit();
    }

    private void initializerRetrofit(){
        retrofit = new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/").addConverterFactory(GsonConverterFactory.create(new Gson()))
        .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }
}
