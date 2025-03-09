package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_home;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.owner.owner_home;
import com.example.unifyx.worker.WorkerHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ApiService apiService;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Change to actual backend URL
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "User logged in, fetching role from API...");
                fetchUserRoleFromAPI(currentUser.getUid());
            } else {
                Log.d(TAG, "No logged-in user, redirecting to login.");
                startActivity(new Intent(SplashActivity.this, signup.class));
                finish();
            }
        }, 2000);
    }

    private void fetchUserRoleFromAPI(String uid) {
        apiService.getUserRole(uid).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String role = response.body().get("role"); // Extract "role" from JSON
                    if (role != null) {
                        Log.d(TAG, "User role: " + role);
                        openRoleHome(role);
                    } else {
                        Log.e(TAG, "Role not found in response");
                        redirectToLogin();
                    }
                } else {
                    Log.e(TAG, "API Error: " + response.code());
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "Error fetching role: " + t.getMessage());
                Toast.makeText(SplashActivity.this, "Error fetching role", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, login.class));
                finish();
            }
        });
    }

    private void openRoleHome(String role) {
        Intent intent;
        switch (role) {
            case "worker":
                intent = new Intent(SplashActivity.this, WorkerHome.class);
                break;
            case "contractor":
                intent = new Intent(SplashActivity.this, contractor_home.class);
                break;
            case "owner":
                intent = new Intent(SplashActivity.this, owner_home.class);
                break;
            default:
                intent = new Intent(SplashActivity.this, login.class);
        }
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        startActivity(new Intent(SplashActivity.this, login.class));
        finish();
    }
}
