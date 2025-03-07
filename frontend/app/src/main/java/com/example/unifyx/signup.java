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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button signup_btn;
    EditText email_et, password_et, cpassword_et;
    TextView login_txt;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(signup.this, choose_role.class);
            intent.putExtra("uid", currentUser.getUid()); // Pass UID
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        signup_btn = findViewById(R.id.btn_submit);
        email_et = findViewById(R.id.et_email);
        password_et = findViewById(R.id.et_password);
        cpassword_et = findViewById(R.id.et_confirm_password);
        login_txt = findViewById(R.id.tv_log_in);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        login_txt.setOnClickListener(view -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
            finish();
        });

        signup_btn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = email_et.getText().toString().trim();
            String password = password_et.getText().toString().trim();
            String confirmPassword = cpassword_et.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(signup.this, "Enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(signup.this, "Enter password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(signup.this, "Account created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(signup.this, choose_role.class);
                                intent.putExtra("uid", user.getUid());// Pass UID to choose_role
                                intent.putExtra("email",user.getEmail());
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(signup.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
