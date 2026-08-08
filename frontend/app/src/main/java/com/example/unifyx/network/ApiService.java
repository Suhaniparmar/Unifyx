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
import retrofit2.http.PUT;
import retrofit2.http.Query;

import com.example.unifyx.model.BidRaise;
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.model.Post;
import com.example.unifyx.model.Users;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.model.Quote;
import com.example.unifyx.model.Hire;
import com.example.unifyx.model.OwnerNotification;
import com.example.unifyx.model.Recommendation;
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

    @GET("/contractor/profile")
    Call<ContractorProfile> getContractorProfile(@Query("email") String email);

    @GET("/worker/profile")
    Call<WorkerProfile> getWorker(@Query("email") String emai);

    @GET("/worker/profile")
    Call<WorkerProfile> getWorkerByUid(@Query("uid") String uid);

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

    @GET("/owner/profile")
    Call<OwnerProfile> getOwnerProfileByUid(@Query("uid") String uid);

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

    // ==================== SMART HIRING SYSTEM ENDPOINTS ====================
    
    /**
     * Get recommended workers for a post
     * GET /recommendations/post/{postId}
     */
    @GET("/recommendations/post/{postId}")
    Call<List<Recommendation>> getRecommendationsForPost(@Path("postId") int postId);
    
    /**
     * Submit a quote as a worker
     * POST /quotes
     */
    @POST("/quotes")
    Call<Quote> submitQuote(@Body Map<String, Object> quoteData);
    
    /**
     * Get all quotes for a post (for owner review)
     * GET /quotes/post/{postId}
     */
    @GET("/quotes/post/{postId}")
    Call<List<Quote>> getQuotesForPost(@Path("postId") int postId);
    
    /**
     * Accept a quote (owner hires worker)
     * PUT /quotes/{quoteId}/accept
     */
    @PUT("/quotes/{quoteId}/accept")
    Call<Quote> acceptQuote(@Path("quoteId") int quoteId);
    
    /**
     * Reject a quote
     * PUT /quotes/{quoteId}/reject
     */
    @PUT("/quotes/{quoteId}/reject")
    Call<Quote> rejectQuote(@Path("quoteId") int quoteId);
    
    /**
     * Get pending quotes for a worker
     * GET /quotes/worker/{workerId}/pending
     */
    @GET("/quotes/worker/{workerId}/pending")
    Call<List<Quote>> getPendingQuotesForWorker(@Path("workerId") int workerId);

    @GET("/quotes/worker/{workerId}/all")
    Call<List<Quote>> getAllQuotesForWorker(@Path("workerId") int workerId);

    @GET("/quotes/contractor/{contractorId}/all")
    Call<List<Quote>> getAllQuotesForContractor(@Path("contractorId") int contractorId);
    
    /**
     * Create a hire agreement
     * POST /hires
     */
    @POST("/hires")
    Call<Hire> createHire(@Body Map<String, Object> hireData);
    
    /**
     * Get hires for a worker
     * GET /hires/worker/{workerId}
     */
    @GET("/hires/worker/{workerId}")
    Call<List<Hire>> getHiresForWorker(@Path("workerId") int workerId);
    
    /**
     * Complete a hire
     * PUT /hires/{hireId}/complete
     */
    @PUT("/hires/{hireId}/complete")
    Call<Hire> completeHire(@Path("hireId") int hireId);
    
    /**
     * Rate and review a hire
     * PUT /hires/{hireId}/rate
     */
    @PUT("/hires/{hireId}/rate")
    Call<Hire> rateHire(@Path("hireId") int hireId, @Body Map<String, Object> ratingData);

    @GET("/notifications/owners/{ownerUid}")
    Call<List<OwnerNotification>> getOwnerNotifications(@Path("ownerUid") String ownerUid);

    @GET("/notifications/owners/{ownerUid}/unread-count")
    Call<Map<String, Long>> getOwnerNotificationUnreadCount(@Path("ownerUid") String ownerUid);

    @PUT("/notifications/{notificationId}/read")
    Call<OwnerNotification> markOwnerNotificationRead(@Path("notificationId") int notificationId);

}
