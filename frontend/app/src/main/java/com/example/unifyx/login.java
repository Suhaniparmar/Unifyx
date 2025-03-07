package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_home;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.owner.owner_home;
import com.example.unifyx.worker.worker_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private ProgressBar progressBar_login;
    private Button login_btn;
    private EditText email_et_login, password_et_login;
    private TextView signup_txt;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Auth before calling getCurrentUser
        mAuth = FirebaseAuth.getInstance();

        login_btn = findViewById(R.id.submit_button);
        email_et_login = findViewById(R.id.username_field);
        password_et_login = findViewById(R.id.password_field);
        signup_txt = findViewById(R.id.signup_text);
        progressBar_login = findViewById(R.id.progress_bar1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080") // Ensure this URL is correct
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        signup_txt.setOnClickListener(view -> startActivity(new Intent(login.this, signup.class)));

        login_btn.setOnClickListener(view -> {
            String email = email_et_login.getText().toString().trim();
            String password = password_et_login.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(login.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar_login.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar_login.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.getUid() != null) {
                                fetchUserRoleFromServer(user.getUid());
                            } else {
                                Log.e(TAG, "User is null after login.");
                                Toast.makeText(login.this, "Error: User data not available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getUid() != null) {
            fetchUserRoleFromServer(currentUser.getUid());
        } else {
            Log.e(TAG, "User is null in onStart()");
        }
    }

    private void fetchUserRoleFromServer(String uid) {
        if (uid == null) {
            Log.e(TAG, "fetchUserRoleFromServer: UID is null");
            return;
        }

        Log.d(TAG, "Fetching role for UID: " + uid);

        apiService.getUserRole(uid).enqueue(new Callback<Map<String, String>>() {  // Expecting JSON response
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> responseBody = response.body();
                    if (responseBody.containsKey("role")) {
                        String role = responseBody.get("role");
                        Log.d(TAG, "User role: " + role);
                        openRoleHome(role);
                    } else {
                        Log.e(TAG, "Role not found in response: " + responseBody.get("error"));
                        redirectToChooseRole();
                    }
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " " + response.message());
                    redirectToChooseRole();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "Error fetching role: " + t.getMessage());
                Toast.makeText(login.this, "Error fetching role: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                redirectToChooseRole();
            }
        });
    }

    // Helper method to redirect to choose_role
    private void redirectToChooseRole() {
        startActivity(new Intent(login.this, choose_role.class));
        finish();
    }


    private void openRoleHome(String role) {
        Intent intent;
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || user.getUid() == null || user.getEmail() == null) {
            Log.e(TAG, "User details missing in openRoleHome");
            return;
        }

        Log.d(TAG, "User Role: " + role + ", UID: " + user.getUid() + ", Email: " + user.getEmail());

        switch (role) {
            case "worker":
                intent = new Intent(login.this, worker_home.class);
                break;
            case "contractor":
                intent = new Intent(login.this, contractor_home.class);
                break;
            case "owner":
                intent = new Intent(login.this, owner_home.class);
                break;
            default:
                intent = new Intent(login.this, choose_role.class);
                intent.putExtra("uid", user.getUid());
                intent.putExtra("email", user.getEmail());
        }

        startActivity(intent);
        finish();
    }
}
