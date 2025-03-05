package com.example.unifyx.owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
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
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String contact = contactEditText.getText().toString();
            String location = locationSpinner.getSelectedItem().toString();

            OwnerProfile owner = new OwnerProfile(name, email, contact, location);
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
}
