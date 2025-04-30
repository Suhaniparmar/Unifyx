package com.example.unifyx.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView; // ✅ CORRECT


import java.util.ArrayList;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.PostAdapter;
import com.example.unifyx.adapter.PostAdapterViewer;
import com.example.unifyx.model.Post;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home); // Set the correct XML file

        workerProfileIcon = findViewById(R.id.imageView2);
        searchView = findViewById(R.id.searchView);

        workerProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(WorkerHome.this, WorkerProfilePage.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.workerPostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapterViewer = new PostAdapterViewer(this, new ArrayList<>());
        recyclerView.setAdapter(postAdapterViewer);

        fetchPosts();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    Log.d("WorkerHome", "Searching for: " + query); // ✅ Debugging
                    searchPostsByLocation(query);
                } else {
                    fetchPosts(); // ✅ Show all posts if search is empty
                }
                return true; // ✅ Prevents duplicate API calls
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchPosts(); // ✅ Reset list when search text is cleared
                }
                return true; // ✅ Prevents unnecessary calls
            }
        });

    }

    private void fetchPosts() {
        int workerId = getWorkerId(); // Get worker's ID from shared preferences or session
        Log.d("WorkerInfo", "Worker ID from API: " + workerId);

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

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
        Intent intent = new Intent(this, WorkerBid.class);
        startActivity(intent);
    }

    private int getWorkerId() {
        SharedPreferences prefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);

        return prefs.getInt("senderId", -1); // Ensure correct key
    }


}
