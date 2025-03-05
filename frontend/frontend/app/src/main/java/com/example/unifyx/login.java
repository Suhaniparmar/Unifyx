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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar_login;
    Button login_btn;
    EditText email_et_login,password_et_login;
    TextView signup_txt;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(login.this,choose_role.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_btn=(Button) findViewById(R.id.submit_button);
        email_et_login = (EditText) findViewById(R.id.username_field);
        password_et_login = (EditText) findViewById(R.id.password_field);
        signup_txt = (TextView) findViewById(R.id.signup_text);
        progressBar_login = (ProgressBar) findViewById(R.id.progress_bar1);

        mAuth = FirebaseAuth.getInstance();

        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar_login.setVisibility(View.VISIBLE);
                Intent intent = new Intent(login.this,signup.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(email_et_login.getText());
                password = String.valueOf(password_et_login.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(login.this,"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(login.this,"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar_login.setVisibility(view.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(login.this, "Authentication is done Successfuly",
                                            Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(login.this,choose_role.class);
                                startActivity(intent);
                                finish();
                                } else {

                                    Toast.makeText(login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }
}