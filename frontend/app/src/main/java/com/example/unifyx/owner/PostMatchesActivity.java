package com.example.unifyx.owner;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.unifyx.R;
import com.example.unifyx.adapter.PostMatchesAdapter;
import com.example.unifyx.model.Recommendation;
import com.example.unifyx.model.Quote;
import com.example.unifyx.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Owner's screen to view recommended workers and quotes after posting a job
 */
public class PostMatchesActivity extends AppCompatActivity {
    
    private int postId;
    private int initialTab = 0;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private List<Recommendation> recommendations = new ArrayList<>();
    private List<Quote> otherQuotes = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_matches);
        
        // Get post ID from intent
        postId = getIntent().getIntExtra("postId", -1);
        initialTab = getIntent().getIntExtra("initialTab", 0);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        ImageButton backButton = findViewById(R.id.backButton);
        
        backButton.setOnClickListener(v -> onBackPressed());
        
        // Load recommendations and quotes
        loadRecommendations();
        loadQuotes();
    }
    
    private void loadRecommendations() {
        RetrofitClient.getInstance().getApiService()
            .getRecommendationsForPost(postId)
            .enqueue(new Callback<List<Recommendation>>() {
                @Override
                public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        recommendations = response.body();
                        setupAdapter();
                    }
                }
                
                @Override
                public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                    Toast.makeText(PostMatchesActivity.this, "Failed to load recommendations", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void loadQuotes() {
        RetrofitClient.getInstance().getApiService()
            .getQuotesForPost(postId)
            .enqueue(new Callback<List<Quote>>() {
                @Override
                public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        otherQuotes = response.body();
                        setupAdapter();
                    }
                }
                
                @Override
                public void onFailure(Call<List<Quote>> call, Throwable t) {
                    Toast.makeText(PostMatchesActivity.this, "Failed to load quotes", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void setupAdapter() {
        PostMatchesAdapter adapter = new PostMatchesAdapter(this, recommendations, otherQuotes, postId);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Recommended (" + recommendations.size() + ")");
            } else {
                tab.setText("Other Quotes (" + otherQuotes.size() + ")");
            }
        }).attach();

        viewPager.post(() -> {
            if (initialTab >= 0 && initialTab < adapter.getItemCount()) {
                viewPager.setCurrentItem(initialTab, false);
            }
        });
    }
}

