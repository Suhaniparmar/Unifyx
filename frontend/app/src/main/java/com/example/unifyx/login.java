package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_home;
import com.example.unifyx.owner.owner_home;
import com.example.unifyx.worker.worker_home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class login extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar_login;
    Button login_btn;
    EditText email_et_login, password_et_login;
    TextView signup_txt;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUserRoleFromDatabase(currentUser.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_btn = findViewById(R.id.submit_button);
        email_et_login = findViewById(R.id.username_field);
        password_et_login = findViewById(R.id.password_field);
        signup_txt = findViewById(R.id.signup_text);
        progressBar_login = findViewById(R.id.progress_bar1);

        mAuth = FirebaseAuth.getInstance();

        signup_txt.setOnClickListener(view -> {
            progressBar_login.setVisibility(View.VISIBLE);
            startActivity(new Intent(login.this, signup.class));
        });

        login_btn.setOnClickListener(view -> {
            String email = email_et_login.getText().toString().trim();
            String password = password_et_login.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(login.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar_login.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar_login.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                fetchUserRoleFromDatabase(user.getUid());
                            }
                        } else {
                            Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void fetchUserRoleFromDatabase(String uid) {
        new Thread(() -> {
            try {
                Connection conn = DatabaseHelper.getConnection();
                if (conn == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(login.this, "Database connection failed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, choose_role.class));
                        finish();
                    });
                    return;
                }

                PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE uid = ?");
                stmt.setString(1, uid);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    runOnUiThread(() -> openRoleHome(role));
                } else {
                    runOnUiThread(() -> {
                        startActivity(new Intent(login.this, choose_role.class));
                        finish();
                    });
                }

                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(login.this, "Error fetching role!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login.this, choose_role.class));
                    finish();
                });
            }
        }).start();
    }

    private void openRoleHome(String role) {
        Intent intent;
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
        }
        startActivity(intent);
        finish();
    }
}
