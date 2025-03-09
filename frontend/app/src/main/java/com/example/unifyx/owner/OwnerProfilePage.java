package com.example.unifyx.owner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.PostAdapter;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.model.Post;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OwnerProfilePage extends AppCompatActivity {
    private TextView ownerName, ownerEmail, ownerPhone, ownerAddress;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ApiService apiService;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_page);

        // Initialize UI elements
        ownerName = findViewById(R.id.userName);
        ownerEmail = findViewById(R.id.userEmail);
        ownerPhone = findViewById(R.id.userPhone);
        ownerAddress = findViewById(R.id.userAddress);

        // ✅ Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        for (Post post : postList) {
            Log.d("OwnerProfile", "Fetched Post: " + post.toString());
        }

        postAdapter = new PostAdapter(this,postList);
        recyclerView.setAdapter(postAdapter);
//        // Dummy Post Data (Replace with API call later)
//        postList = new ArrayList<>();
//        postList.add(new Post( "Fixing water leakage", "Ahmedabad"));
//        postList.add(new Post("Wall painting required", "Gandhinagar"));
//
//        // Set Adapter
//        postAdapter = new PostAdapter(postList);
//        recyclerView.setAdapter(postAdapter);

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // ✅ Retrieve logged-in email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String ownerEmailStr = sharedPreferences.getString("email", null);

        SharedPreferences sharedPreferences2 = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        uid = sharedPreferences2.getString("uid", null);

        if (ownerEmailStr != null && uid != null) {
            Log.d("OwnerProfile", "Email from SharedPreferences: " + ownerEmailStr);
            fetchOwnerProfile(ownerEmailStr);
            fetchOwnerPosts(uid);
        } else {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOwnerProfile(String email) {

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        Call<OwnerProfile> call = apiService.getOwnerProfile(email);

        call.enqueue(new Callback<OwnerProfile>() {

            @Override
            public void onResponse(Call<OwnerProfile> call, Response<OwnerProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OwnerProfile owner = response.body();
                    ownerName.setText(owner.getName());
                    ownerEmail.setText(owner.getEmail());
                    ownerPhone.setText(owner.getPhoneNo());
                    ownerAddress.setText(owner.getAddress());
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("OwnerProfile", "Response unsuccessful: " + errorResponse);
                    } catch (Exception e) {
                        Log.e("OwnerProfile", "Error reading response", e);
                    }
                    Toast.makeText(OwnerProfilePage.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProfile> call, Throwable t) {
                Log.e("OwnerProfile", "API call failed: " + t.getMessage());
                Toast.makeText(OwnerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchOwnerPosts(String uid) {
        Log.d("OwnerProfile", "Fetching posts for UID: " + uid);
        Call<List<Post>> call = apiService.getUserPosts(uid);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                    Log.d("OwnerProfile", "Fetched " + postList.size() + " posts.");
                    for (Post post : postList) {
                        Log.d("OwnerProfile", "Post: " + post.toString());
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("OwnerProfile", "Error fetching posts: " + errorResponse);
                    } catch (Exception e) {
                        Log.e("OwnerProfile", "Error reading response", e);
                    }
                    Toast.makeText(OwnerProfilePage.this, "No posts found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(OwnerProfilePage.this, "Error fetching posts", Toast.LENGTH_SHORT).show();
                Log.e("OwnerProfile", "Error fetching posts: " + t.getMessage());
            }
        });
    }
}
