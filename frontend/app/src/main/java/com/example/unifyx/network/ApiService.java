package com.example.unifyx.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.model.Post;
import com.example.unifyx.model.Users;
import com.example.unifyx.model.WorkerProfile;

import java.util.List;
import java.util.Map;

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

    @GET("/posts")
    Call<List<Post>> getPosts();

    // Cloudinary Image Upload
    @Multipart
    @POST("https://api.cloudinary.com/v1_1/dhm6tu4us/image/upload")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> options);

    // Create post using Cloudinary image URLs
    @Multipart
    @POST("/posts/upload")
    Call<ResponseBody> createPost(
            @Part("description") RequestBody description,
            @Part("worker_category") RequestBody workerCategory,
            @Part("site_address") RequestBody siteAddress,
            @Part("site_location") RequestBody siteLocation,
            @Part("duration") RequestBody duration,
            @Part List<MultipartBody.Part> images
    );
    // Sends Cloudinary image URLs only

    @POST("/users")
    Call<Void> createUser(@Body Users user);

    @GET("/users/role/{uid}")
    Call<Map<String, String>> getUserRole(@Path("uid") String uid);

    
    

}
