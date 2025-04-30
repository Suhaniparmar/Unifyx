package com.example.unifyx.owner;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.ProfileAdapter;
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerSearch extends AppCompatActivity {

    private Spinner spinnerLocation;
    private Button btnApplyFilters;
    private RecyclerView recyclerViewProfiles;
    private ProfileAdapter profileAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_search);

        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles);

        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Retrofit API Service
        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        // Sample Locations (You can fetch this dynamically)
        String[] locations = {"Ahmedabad", "Surat", "Vadodara", "Rajkot", "Gandhinagar"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        // Apply Filters Button Click Listener
        btnApplyFilters.setOnClickListener(view -> {
            String selectedLocation = spinnerLocation.getSelectedItem().toString();
            fetchProfiles(selectedLocation);
        });
    }

    private void fetchProfiles(String location) {
        Call<List<Object>> call = apiService.ownersearchByLocation(location);
        call.enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Object> responseData = response.body();
                    List<Profile> profileList = new ArrayList<>();

                    Gson gson = new Gson(); // Standard Gson object

                    for (Object obj : responseData) {
                        JsonObject jsonObject = gson.toJsonTree(obj).getAsJsonObject();

                        if (jsonObject.has("workerId")) {
                            profileList.add(gson.fromJson(jsonObject, WorkerProfile.class));
                        } else if (jsonObject.has("contractorId")) {
                            profileList.add(gson.fromJson(jsonObject, ContractorProfile.class));
                        }
                    }

                    // Now use profileList for RecyclerView adapter
                    ProfileAdapter adapter = new ProfileAdapter(profileList);
                    recyclerViewProfiles.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch profiles", t);
            }
        });
    }

}
