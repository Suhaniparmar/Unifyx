package com.example.unifyx.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.unifyx.contractor.contractor_info;

public interface ApiService {
    @POST("api/contractors/add")
    Call<contractor_info> addContractor(@Body contractor_info contractorInfo);
}
