package com.example.unifyx.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView; // ✅ CORRECT


import java.util.ArrayList;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.PostAdapterViewer;
import com.example.unifyx.model.Post;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.login;
import com.example.unifyx.owner.MyQuotesActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerHome extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapterViewer postAdapterViewer;
    private ApiService apiService;
    private SearchView searchView;
    private ImageView workerProfileIcon;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home); // Set the correct XML file

        workerProfileIcon = findViewById(R.id.imageView2);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        ImageView menuIcon = findViewById(R.id.menu);
        ImageView searchIcon = findViewById(R.id.imageView3);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        if (searchIcon != null) {
            searchIcon.setOnClickListener(v -> Toast.makeText(WorkerHome.this, "Search coming soon", Toast.LENGTH_SHORT).show());
        }

        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(WorkerHome.this, WorkerProfilePage.class));
            } else if (itemId == R.id.nav_my_projects) {
                startActivity(new Intent(WorkerHome.this, MyQuotesActivity.class));
            } else if (itemId == R.id.nav_saved_contractors) {
                Toast.makeText(WorkerHome.this, "Saved Contractors coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_notifications) {
                Toast.makeText(WorkerHome.this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(WorkerHome.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                userPrefs.clear();
                userPrefs.apply();

                SharedPreferences.Editor appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
                appPrefs.clear();
                appPrefs.apply();

                Intent loginIntent = new Intent(WorkerHome.this, login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        workerProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(WorkerHome.this, WorkerProfilePage.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapterViewer = new PostAdapterViewer(this, new ArrayList<>());
        recyclerView.setAdapter(postAdapterViewer);

        apiService = RetrofitClient.getInstance().getApiService();

        initializeWorkerAndFetchPosts();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!query.isEmpty()) {
                        Log.d("WorkerHome", "Searching for: " + query);
                        searchPostsByLocation(query);
                    } else {
                        initializeWorkerAndFetchPosts();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty()) {
                        initializeWorkerAndFetchPosts();
                    }
                    return true;
                }
            });
        }

    }

    private void initializeWorkerAndFetchPosts() {
        int workerId = getWorkerId();
        if (workerId != -1) {
            fetchPosts(workerId);
            return;
        }

        String email = getUserEmail();
        if (email == null || email.isEmpty()) {
            Log.e("WorkerHome", "Missing senderId and email. Skipping worker posts request.");
            postAdapterViewer.updateData(new ArrayList<>());
            return;
        }

        apiService.getWorker(email).enqueue(new Callback<WorkerProfile>() {
            @Override
            public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int resolvedWorkerId = response.body().getWorkerId();
                    if (resolvedWorkerId > 0) {
                        saveWorkerId(resolvedWorkerId);
                        fetchPosts(resolvedWorkerId);
                    } else {
                        Log.e("WorkerHome", "Invalid workerId from profile API: " + resolvedWorkerId);
                        postAdapterViewer.updateData(new ArrayList<>());
                    }
                } else {
                    Log.e("WorkerHome", "Unable to resolve workerId from profile API: " + response.code());
                    postAdapterViewer.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<WorkerProfile> call, Throwable t) {
                Log.e("WorkerHome", "Failed to resolve workerId", t);
                postAdapterViewer.updateData(new ArrayList<>());
            }
        });
    }

    private void fetchPosts(int workerId) {
        Log.d("WorkerInfo", "Worker ID from session/profile: " + workerId);
        Call<List<Post>> call = apiService.getWorkerPosts(workerId);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();
                    postAdapterViewer.updateData(postList);
                } else {
                    Log.e("WorkerHome", "Response Failed: " + response.message());
                    postAdapterViewer.updateData(new ArrayList<>()); // Show empty list
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("WorkerHome", "API Call Failed: " + t.getMessage());
            }
        });
    }


    private void searchPostsByLocation(String location) {
        Log.d("WorkerHome", "Calling API for location: " + location); // ✅ Debugging

        apiService.searchByLocation(location).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> filteredPosts = response.body();
                    Log.d("WorkerHome", "Results Found: " + filteredPosts.size()); // ✅ Debugging
                    postAdapterViewer.updateData(filteredPosts);
                } else {
                    Log.e("WorkerHome", "No results found for location: " + location);
                    postAdapterViewer.updateData(new ArrayList<>()); // ✅ Show empty list
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("WorkerHome", "Search API Call Failed: " + t.getMessage());
            }
        });
    }


    public void openBidPage(View view) {
        int workerId = getWorkerId();
        if (workerId <= 0) {
            Toast.makeText(this, "Worker profile not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fallback action: open quote screen only when a post context exists.
        Toast.makeText(this, "Open a job and tap Send Quote", Toast.LENGTH_SHORT).show();
    }

    private int getWorkerId() {
        SharedPreferences prefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);

        return prefs.getInt("senderId", -1); // Ensure correct key
    }

    private String getUserEmail() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("email", null);
    }

    private void saveWorkerId(int workerId) {
        SharedPreferences.Editor editor = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
        editor.putInt("senderId", workerId);
        editor.apply();
        Log.d("WorkerHome", "Recovered and saved senderId: " + workerId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }


}
