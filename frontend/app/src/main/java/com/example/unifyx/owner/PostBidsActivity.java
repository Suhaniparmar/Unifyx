package com.example.unifyx.owner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.BidAdapter;
import com.example.unifyx.model.BidRaise;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostBidsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BidAdapter bidAdapter;
    private ApiService apiService;
    private ProgressBar progressBar;
    private int postId;  // The Post ID to fetch bids for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldRedirectToMatches()) {
            Intent sourceIntent = getIntent();
            Intent matchesIntent = new Intent(this, PostMatchesActivity.class);
            matchesIntent.putExtra("postId", sourceIntent.getIntExtra("postId", -1));
            startActivity(matchesIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_post_bids);

        recyclerView = findViewById(R.id.recyclerViewBids);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btn_back).setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);

        if (postId == -1) {
            Toast.makeText(this, "Invalid Post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);
        fetchBids();
    }

    private boolean shouldRedirectToMatches() {
        return true;
    }

    private void navigateBack() {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        startActivity(new Intent(this, owner_home.class));
        finish();
    }

    private void fetchBids() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getBidsForPost(postId).enqueue(new Callback<List<BidRaise>>() {
            @Override
            public void onResponse(Call<List<BidRaise>> call, Response<List<BidRaise>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<BidRaise> bidList = response.body();
                    bidAdapter = new BidAdapter(PostBidsActivity.this, bidList);
                    recyclerView.setAdapter(bidAdapter);
                } else {
                    Toast.makeText(PostBidsActivity.this, "No bids found for this post!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BidRaise>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostBidsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PostBidsActivity", "Error fetching bids", t);
            }
        });
    }
}
