package com.example.unifyx.worker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.PostAdapter;
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
    private PostAdapter postAdapter;
    private ApiService apiService;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home); // Set the correct XML file

        ImageView workerProfileIcon = findViewById(R.id.imageView2);

        workerProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(WorkerHome.this, WorkerProfilePage.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.workerPostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchPosts();
    }

    private void fetchPosts() {

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        Call<List<Post>> call = apiService.getAllPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();
                    postAdapter = new PostAdapter(WorkerHome.this, postList);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Log.e("WorkerHome", "Response Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("WorkerHome", "API Call Failed: " + t.getMessage());
            }
        });
    }
}
