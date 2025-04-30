package com.example.unifyx.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class worker_info extends AppCompatActivity {
    private EditText nameEditText, emailEditText, contactEditText;
    private Spinner locationSpinner;
    private Button submitButton;
    private LinearLayout categoryCheckboxGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_info);

        // Initialize Views
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        contactEditText = findViewById(R.id.contact);
        locationSpinner = findViewById(R.id.location_spinner);
        submitButton = findViewById(R.id.submit_button);
        categoryCheckboxGroup = findViewById(R.id.category_checkbox_group);

        // Populate Worker Categories with Checkboxes
        addWorkerCategories();

        RetrofitClient retrofitClient = new RetrofitClient();
        ApiService apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // Handle Submit Button Click
        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String contact = contactEditText.getText().toString().trim();
            String location = locationSpinner.getSelectedItem().toString();
            List<String> selectedCategories = getSelectedCategories();

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
                Toast.makeText(worker_info.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCategories.isEmpty()) {
                Toast.makeText(worker_info.this, "Please select at least one category", Toast.LENGTH_SHORT).show();
                return;
            }

            WorkerProfile worker = new WorkerProfile(name, email, contact, location, selectedCategories);
            Log.d("WorkerInfo", "Sending Data: " + worker.toString()); // ✅ Debugging log

            apiService.addWorker(worker).enqueue(new Callback<WorkerProfile>() {
                @Override
                public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("WorkerInfo", "API Response: " + new Gson().toJson(response.body()));


//                        WorkerProfile savedWorker = response.body();
//                        Log.d("WorkerInfo", "API Response: " + new Gson().toJson(worker)); // Log full JSON
                        SharedPreferences.Editor editor = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
                        int workerId = response.body().getWorkerId(); // Ensure this method exists
                        Log.d("WorkerInfo", "Worker ID from API: " + workerId);

                        // Save worker_id in SharedPreferences
                        editor.putInt("senderId", workerId);
                        editor.apply();
                        Log.d("WorkerInfo", "Saved senderId to SharedPreferences: " + workerId);
                        Toast.makeText(worker_info.this, "Worker added successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(worker_info.this, WorkerHome.class));
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(worker_info.this, "Error: " + response.code() + "\n" + errorBody, Toast.LENGTH_LONG).show();
                            Log.e("WorkerHome", "Response Failed: " + errorBody);
                        } catch (Exception e) {
                            Log.e("WorkerHome", "Error parsing error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<WorkerProfile> call, Throwable t) {
                    Toast.makeText(worker_info.this, "API Call Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("WorkerHome", "API Call Failed", t);
                }
            });
        });
    }

    private void addWorkerCategories() {
        String[] categories = {"Painter", "Electrician", "Plaster Worker", "Tile Maker", "Carpenter", "Mason", "Welder", "Roofer", "Steel Fixer", "Concrete Laborer", "Pipefitter", "Scaffolder", "Glazier", "Insulation Worker", "Landscaper", "Paver", "Demolition Worker", "Crane Operator", "Heavy Equipment Operator", "Surveyor"};
        for (String category : categories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(category);
            checkBox.setTextColor(getResources().getColor(R.color.black));
            checkBox.setPadding(20, 10, 20, 10);

            categoryCheckboxGroup.addView(checkBox);
        }
    }

    private List<String> getSelectedCategories() {
        List<String> selectedCategories = new ArrayList<>();
        for (int i = 0; i < categoryCheckboxGroup.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) categoryCheckboxGroup.getChildAt(i);
            if (checkBox.isChecked()) {
                selectedCategories.add(checkBox.getText().toString());
            }
        }
        return selectedCategories;
    }
}
