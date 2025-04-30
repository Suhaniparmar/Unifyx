package com.example.unifyx.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.unifyx.model.BidRaise;
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.model.Post;
import com.example.unifyx.model.Users;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.owner.Profile;

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

    @GET("/worker/profile")
    Call<WorkerProfile> getWorker(@Query("email") String emai);

    @POST("/worker") // Make sure the endpoint matches EXACTLY
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
            @Part("uid") RequestBody uid, // Replace with actual owner UID"
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

    @GET("/owner/profile")
    Call<OwnerProfile> getOwnerProfile(@Query("email") String email);

    @GET("/posts/owner/{uid}")
    Call<List<Post>> getUserPosts(@Path("uid") String uid);

    @GET("/posts/all")
    Call<List<Post>> getAllPosts();

    @DELETE("posts/{id}") // Adjust URL as per your API
    Call<Void> deletePost(@Path("id") int postId);


    @POST("/bids/raise") // Adjust this based on your backend API endpoint
    Call<ResponseBody> raiseBid(
            @Query("senderId") int senderId,
            @Query("postId") int postId,
            @Query("amount") double amount,
            @Query("duration") String duration
    );

    @GET("/posts/{id}")
    Call<Post> getPostById(@Path("id") int postId);

    @GET("/bids/post/{postId}")
    Call<List<BidRaise>> getBidsByPost(@Path("postId") int postId);

    @DELETE("/bids/{bidRaiseId}")
    Call<Void> deleteBid(@Path("bidRaiseId") int bidRaiseId);

    @GET("search/by-location")
    Call<List<Object>> ownersearchByLocation(@Query("location") String location);

    @GET("search/posts/by-location")
    Call<List<Post>> searchByLocation(@Query("location") String location);

    @GET("/worker/home/{workerId}/posts")
    Call<List<Post>> getWorkerPosts(@Path("workerId") int workerId);

    @GET("bids/post/{postId}")
    Call<List<BidRaise>> getBidsForPost(@Path("postId") int postId);

}
