package com.example.unifyx.contractor;

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
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.OwnerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.owner.owner_info;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class contractor_info extends AppCompatActivity {
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

//        // Handle Spinner Selection
//        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedCity = parent.getItemAtPosition(position).toString();
//                Toast.makeText(owner_info.this, "Selected: " + selectedCity, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });

        RetrofitClient retrofitClient = new RetrofitClient();
        ApiService apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // Handle Submit Button Click
        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = (identityEmail != null && !identityEmail.trim().isEmpty())
                    ? identityEmail.trim()
                    : emailEditText.getText().toString().trim();
            String contact = contactEditText.getText().toString().trim();
            String location = locationSpinner.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
                Toast.makeText(contractor_info.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            ContractorProfile contractor = new ContractorProfile(name, email, contact, location, null);
            apiService.addContractor(contractor).enqueue(new Callback<ContractorProfile>() {

                @Override
                public void onResponse(Call<ContractorProfile> call, Response<ContractorProfile> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(contractor_info.this, "Data Submitted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(contractor_info.this, contractor_home.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(contractor_info.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContractorProfile> call, Throwable t) {
                    Toast.makeText(contractor_info.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
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
