package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_info;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080") // Change to actual backend URL
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
        Users user = new Users(uid, email, role);
        Log.d("Request Body", "UID: " + user.getUid() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
        apiService.createUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(choose_role.this, "Role saved", Toast.LENGTH_SHORT).show();
                    navigateToHome(role);
                } else {
                    Log.d("API Response", "Error: " + response.code() + " " + response.message());
                    Toast.makeText(choose_role.this, "Failed to save role: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(choose_role.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
