package com.example.unifyx.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.unifyx.R;
import com.example.unifyx.login;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerProfilePage extends AppCompatActivity {
    private TextView workerName, workerEmail, workerPhone, workerAddress;
    private ApiService apiService;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile_page);

        // Initialize UI elements
        workerName = findViewById(R.id.userName);
        workerEmail = findViewById(R.id.userEmail);
        workerPhone = findViewById(R.id.userPhone);
        workerAddress = findViewById(R.id.userAddress);
        clearProfileFields();
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        ImageView menuIcon = findViewById(R.id.menu);
        ImageView backButton = findViewById(R.id.btn_back);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        backButton.setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (itemId == R.id.nav_my_projects) {
                Toast.makeText(WorkerProfilePage.this, "My Projects coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_saved_contractors) {
                Toast.makeText(WorkerProfilePage.this, "Saved Contractors coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_notifications) {
                Toast.makeText(WorkerProfilePage.this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(WorkerProfilePage.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                userPrefs.clear();
                userPrefs.apply();

                SharedPreferences.Editor appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
                appPrefs.clear();
                appPrefs.apply();

                Intent loginIntent = new Intent(WorkerProfilePage.this, login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // Retrieve authenticated identity from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);
        String workerEmailStr = sharedPreferences.getString("email", null);

        if (!TextUtils.isEmpty(uid) || !TextUtils.isEmpty(workerEmailStr)) {
            Log.d("WorkerProfile", "Fetching profile for identity uid=" + uid + ", email=" + workerEmailStr);
            fetchWorkerProfile(uid, workerEmailStr);
        } else {
            Toast.makeText(this, "User identity missing. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchWorkerProfile(String uid, String email) {
        if (!TextUtils.isEmpty(uid)) {
            apiService.getWorkerByUid(uid).enqueue(new Callback<WorkerProfile>() {
                @Override
                public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        bindWorkerProfile(response.body());
                    } else if (!TextUtils.isEmpty(email)) {
                        fetchWorkerProfileByEmail(email);
                    } else {
                        clearProfileFields();
                        Toast.makeText(WorkerProfilePage.this, "Failed to fetch profile (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WorkerProfile> call, Throwable t) {
                    if (!TextUtils.isEmpty(email)) {
                        fetchWorkerProfileByEmail(email);
                    } else {
                        clearProfileFields();
                        Toast.makeText(WorkerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }

        fetchWorkerProfileByEmail(email);
    }

    private void fetchWorkerProfileByEmail(String email) {
        Call<WorkerProfile> call = apiService.getWorker(email);

        call.enqueue(new Callback<WorkerProfile>() {
            @Override
            public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindWorkerProfile(response.body());
                } else {
                    clearProfileFields();
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("WorkerProfile", "Response unsuccessful: " + response.code() + " " + errorResponse);
                    } catch (Exception e) {
                        Log.e("WorkerProfile", "Error reading response", e);
                    }
                    Toast.makeText(WorkerProfilePage.this, "Failed to fetch profile (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkerProfile> call, Throwable t) {
                clearProfileFields();
                Log.e("WorkerProfile", "API call failed", t);
                Toast.makeText(WorkerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void bindWorkerProfile(WorkerProfile worker) {
        workerName.setText(worker.getName());
        workerEmail.setText(worker.getEmail());
        workerPhone.setText(worker.getPhoneNo());
        workerAddress.setText(worker.getAddress());
    }

    private void navigateBack() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (!isTaskRoot()) {
            finish();
            return;
        }
        startActivity(new Intent(this, WorkerHome.class));
        finish();
    }

    private void clearProfileFields() {
        workerName.setText("-");
        workerEmail.setText("-");
        workerPhone.setText("-");
        workerAddress.setText("-");
    }

}
