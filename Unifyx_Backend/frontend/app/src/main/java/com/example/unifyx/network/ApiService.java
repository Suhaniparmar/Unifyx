package com.example.unifyx.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.model.WorkerProfile;


public interface ApiService {

    @GET("/owner")
    Call<OwnerProfile> getOwners();

    @POST("/owner")
    Call<OwnerProfile> addOwner(@Body OwnerProfile owner);

    @GET("/contractor")
    Call<ContractorProfile> getContractors();

    @POST("/contractor")
    Call<ContractorProfile> addContractor(@Body ContractorProfile contractor);

    @GET("/worker")
    Call<WorkerProfile> getWorkers();

    @POST("/worker")
    Call<WorkerProfile> addWorker(@Body WorkerProfile worker);
}

