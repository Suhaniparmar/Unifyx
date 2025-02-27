package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unifyx.contractor.contractor_home;
import com.example.unifyx.owner.owner_home;
import com.example.unifyx.worker.worker_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                Log.d(TAG, "User logged in, fetching role from MySQL...");
                fetchUserRoleFromDatabase(currentUser.getUid());
            } else {
                Log.d(TAG, "No logged-in user, redirecting to login screen.");
                startActivity(new Intent(SplashActivity.this, login.class));
                finish();
            }
        }, 2000); // 2-second splash screen delay
    }

    private void fetchUserRoleFromDatabase(String uid) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Connecting to database...");
                Connection conn = DatabaseHelper.getConnection();

                if (conn == null) {
                    Log.e(TAG, "Database connection failed!");
                    runOnUiThread(() -> {
                        startActivity(new Intent(SplashActivity.this, login.class));
                        finish();
                    });
                    return;
                }

                PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE uid = ?");
                stmt.setString(1, uid);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    Log.d(TAG, "User role found: " + role);
                    runOnUiThread(() -> openRoleHome(role));
                } else {
                    Log.d(TAG, "User role not found, redirecting to login.");
                    runOnUiThread(() -> {
                        startActivity(new Intent(SplashActivity.this, login.class));
                        finish();
                    });
                }
                conn.close();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching role: ", e);
                runOnUiThread(() -> {
                    startActivity(new Intent(SplashActivity.this, login.class));
                    finish();
                });
            }
        }).start();
    }

    private void openRoleHome(String role) {
        Intent intent;
        switch (role) {
            case "worker":
                intent = new Intent(SplashActivity.this, worker_home.class);
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
}
