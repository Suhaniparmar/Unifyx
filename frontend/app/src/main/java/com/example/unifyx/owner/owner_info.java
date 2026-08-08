package com.example.unifyx.owner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
import com.example.unifyx.choose_role;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class owner_info extends AppCompatActivity {
    private EditText nameEditText, emailEditText, contactEditText;
    private Spinner locationSpinner;
    private Button submitButton;
    private String identityEmail;
    private String identityUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info);

        // Initialize Views
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        contactEditText = findViewById(R.id.contact);
        locationSpinner = findViewById(R.id.location_spinner);
        submitButton = findViewById(R.id.submit_button);
        findViewById(R.id.btn_back).setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        identityEmail = userPrefs.getString("email", null);
        identityUid = userPrefs.getString("uid", null);
        if (identityEmail != null && !identityEmail.trim().isEmpty()) {
            emailEditText.setText(identityEmail);
            emailEditText.setEnabled(false);
        }

        RetrofitClient retrofitClient = new RetrofitClient();
        ApiService apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // Handle Submit Button Click
        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String contact = contactEditText.getText().toString().trim();
            String location = locationSpinner.getSelectedItem().toString();
            String email = (identityEmail != null && !identityEmail.trim().isEmpty())
                    ? identityEmail.trim()
                    : emailEditText.getText().toString().trim();

            if (name.isEmpty() || contact.isEmpty() || email.isEmpty()) {
                Toast.makeText(owner_info.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            OwnerProfile owner = new OwnerProfile(name, email, contact, location);
            owner.setUid(identityUid);
            apiService.addOwner(owner).enqueue(new Callback<OwnerProfile>() {

                @Override
                public void onResponse(Call<OwnerProfile> call, Response<OwnerProfile> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(owner_info.this, "Data Submitted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(owner_info.this, owner_home.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(owner_info.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OwnerProfile> call, Throwable t) {
                    Toast.makeText(owner_info.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                    Logger.getLogger(owner_info.class.getName()).log(Level.SEVERE,"Error occured",t);
                }

            });
        });

//
    }

    private void navigateBack() {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        startActivity(new Intent(this, choose_role.class));
        finish();
    }

}
