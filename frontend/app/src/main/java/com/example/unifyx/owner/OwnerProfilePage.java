package com.example.unifyx.owner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
        findViewById(R.id.btn_back).setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });
        clearProfileFields();

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
        uid = sharedPreferences.getString("uid", null);

        if (!TextUtils.isEmpty(uid) || !TextUtils.isEmpty(ownerEmailStr)) {
            Log.d("OwnerProfile", "Identity from SharedPreferences: " + ownerEmailStr + ", uid=" + uid);
            fetchOwnerProfile(uid, ownerEmailStr);
            if (!TextUtils.isEmpty(uid)) {
                fetchOwnerPosts(uid);
            }
        } else {
            Toast.makeText(this, "User identity missing. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchOwnerProfile(String uid, String email) {
        if (!TextUtils.isEmpty(uid)) {
            apiService.getOwnerProfileByUid(uid).enqueue(new Callback<OwnerProfile>() {
                @Override
                public void onResponse(Call<OwnerProfile> call, Response<OwnerProfile> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        bindOwnerProfile(response.body());
                    } else if (!TextUtils.isEmpty(email)) {
                        fetchOwnerProfileByEmail(email);
                    } else {
                        clearProfileFields();
                        Toast.makeText(OwnerProfilePage.this, "Failed to fetch profile (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OwnerProfile> call, Throwable t) {
                    if (!TextUtils.isEmpty(email)) {
                        fetchOwnerProfileByEmail(email);
                    } else {
                        clearProfileFields();
                        Toast.makeText(OwnerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }

        fetchOwnerProfileByEmail(email);
    }

    private void fetchOwnerProfileByEmail(String email) {
        Call<OwnerProfile> call = apiService.getOwnerProfile(email);

        call.enqueue(new Callback<OwnerProfile>() {

            @Override
            public void onResponse(Call<OwnerProfile> call, Response<OwnerProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindOwnerProfile(response.body());
                } else {
                    clearProfileFields();
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("OwnerProfile", "Response unsuccessful: " + response.code() + " " + errorResponse);
                    } catch (Exception e) {
                        Log.e("OwnerProfile", "Error reading response", e);
                    }
                    Toast.makeText(OwnerProfilePage.this, "Failed to fetch profile (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProfile> call, Throwable t) {
                clearProfileFields();
                Log.e("OwnerProfile", "API call failed: " + t.getMessage());
                Toast.makeText(OwnerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindOwnerProfile(OwnerProfile owner) {
        ownerName.setText(owner.getName());
        ownerEmail.setText(owner.getEmail());
        ownerPhone.setText(owner.getPhoneNo());
        ownerAddress.setText(owner.getAddress());
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
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("OwnerProfile", "Error fetching posts: " + response.code() + " " + errorResponse);
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

    private void clearProfileFields() {
        ownerName.setText("-");
        ownerEmail.setText("-");
        ownerPhone.setText("-");
        ownerAddress.setText("-");
    }

    private void navigateBack() {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        startActivity(new Intent(this, owner_home.class));
        finish();
    }

}
