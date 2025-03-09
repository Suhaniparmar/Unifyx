package com.example.unifyx.worker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.PostAdapter;
import com.example.unifyx.model.Post;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.owner.OwnerProfilePage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerProfilePage extends AppCompatActivity {
    private TextView workerName, workerEmail, workerPhone, workerAddress;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ApiService apiService;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile_page);

        // Initialize UI elements
        workerName = findViewById(R.id.userName);
        workerEmail = findViewById(R.id.userEmail);
        workerPhone = findViewById(R.id.userPhone);
        workerAddress = findViewById(R.id.userAddress);

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // ✅ Retrieve logged-in email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String workerEmailStr = sharedPreferences.getString("email", null);

        if(workerEmailStr != null) {
            Log.d("OwnerProfile", "Email from SharedPreferences: " + workerEmailStr);
            fetchOwnerProfile(workerEmailStr);
        }else {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOwnerProfile(String email) {

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        Call<WorkerProfile> call = apiService.getWorker(email);

        call.enqueue(new Callback<WorkerProfile>() {
            @Override
            public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkerProfile worker = response.body();
                    workerName.setText(worker.getName());
                    workerEmail.setText(worker.getEmail());
                    workerPhone.setText(worker.getPhoneNo());
                    workerAddress.setText(worker.getAddress());
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("WorknerProfile", "Response unsuccessful: " + errorResponse);
                    } catch (Exception e) {
                        Log.e("WorkerProfile", "Error reading response", e);
                    }
                    Toast.makeText(WorkerProfilePage.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkerProfile> call, Throwable t) {
                Log.e("OwnerProfile", "API call failed: " + t.getMessage());
                Toast.makeText(WorkerProfilePage.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
