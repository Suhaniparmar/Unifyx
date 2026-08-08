package com.example.unifyx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_info;
import com.example.unifyx.common.NetworkUtils;
import com.example.unifyx.model.Users;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.owner.owner_info;
import com.example.unifyx.worker.worker_info;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class choose_role extends AppCompatActivity {

    LinearLayout owner, contractor, worker;
    String email, uid;
    ApiService apiService;
    boolean isSubmitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(email)) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            if (TextUtils.isEmpty(uid)) {
                uid = prefs.getString("uid", null);
            }
            if (TextUtils.isEmpty(email)) {
                email = prefs.getString("email", null);
            }
        }

        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "User data missing. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Change to actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        owner = findViewById(R.id.owner);
        contractor = findViewById(R.id.contractor);
        worker = findViewById(R.id.worker);

        owner.setOnClickListener(view -> insertUser("owner"));
        contractor.setOnClickListener(view -> insertUser("contractor"));
        worker.setOnClickListener(view -> insertUser("worker"));
    }

    private void insertUser(String role) {
        if (isSubmitting) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setRoleSelectionEnabled(false);
        isSubmitting = true;

        Users user = new Users(uid, email, role);
        Log.d("Request Body", "UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
        apiService.createUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isSubmitting = false;
                    setRoleSelectionEnabled(true);
                    Toast.makeText(choose_role.this, "Role saved", Toast.LENGTH_SHORT).show();
                    navigateToHome(role);
                } else if (response.code() == 409) {
                    isSubmitting = true;
                    setRoleSelectionEnabled(false);
                    Toast.makeText(choose_role.this, "Role already exists. Fetching your current role...", Toast.LENGTH_SHORT).show();
                    fetchActualRoleAndNavigate();
                } else if (response.code() >= 500) {
                    isSubmitting = false;
                    setRoleSelectionEnabled(true);
                    Toast.makeText(choose_role.this, "Server unavailable. Please try again shortly.", Toast.LENGTH_SHORT).show();
                } else {
                    isSubmitting = false;
                    setRoleSelectionEnabled(true);
                    Log.d("API Response", "Error: " + response.code() + " " + response.message());
                    Toast.makeText(choose_role.this, "Failed to save role. Please retry.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isSubmitting = false;
                setRoleSelectionEnabled(true);
                Toast.makeText(choose_role.this, "Could not connect to server. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRoleSelectionEnabled(boolean enabled) {
        owner.setEnabled(enabled);
        contractor.setEnabled(enabled);
        worker.setEnabled(enabled);
    }

    private void fetchActualRoleAndNavigate() {
        if (TextUtils.isEmpty(uid)) {
            isSubmitting = false;
            setRoleSelectionEnabled(true);
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService.getUserRole(uid).enqueue(new Callback<java.util.Map<String, String>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                isSubmitting = false;
                setRoleSelectionEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String actualRole = response.body().get("role");
                    if (!TextUtils.isEmpty(actualRole)) {
                        navigateToHome(actualRole.trim().toLowerCase());
                        return;
                    }
                }

                Toast.makeText(choose_role.this, "Could not resolve account role. Please login again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                isSubmitting = false;
                setRoleSelectionEnabled(true);
                Toast.makeText(choose_role.this, "Could not fetch current role. Please retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome(String role) {
        Intent intent;
        switch (role) {
            case "owner":
                intent = new Intent(choose_role.this, owner_info.class);
                break;
            case "contractor":
                intent = new Intent(choose_role.this, contractor_info.class);
                break;
            case "worker":
                intent = new Intent(choose_role.this, worker_info.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        finish();
    }
}
