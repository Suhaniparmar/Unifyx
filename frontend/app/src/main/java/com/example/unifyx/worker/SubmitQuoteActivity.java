package com.example.unifyx.worker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unifyx.R;
import com.example.unifyx.model.Post;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.model.Quote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Worker's screen to submit a quote for a job post
 */
public class SubmitQuoteActivity extends AppCompatActivity {
    
    private int postId;
    private int workerId;
    private int contractorId;
    
    private EditText priceInput;
    private EditText timeInput;
    private EditText messageInput;
    private Button submitQuoteBtn;
    private TextView jobTitle;
    private TextView jobCategory;
    private TextView jobLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_quote);
        
        // Get data from intent
        postId = getIntent().getIntExtra("postId", -1);
        workerId = getIntent().getIntExtra("workerId", -1);
        contractorId = getIntent().getIntExtra("contractorId", -1);
        
        if (postId == -1 || (workerId == -1 && contractorId == -1)) {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        priceInput = findViewById(R.id.priceInput);
        timeInput = findViewById(R.id.timeInput);
        messageInput = findViewById(R.id.messageInput);
        submitQuoteBtn = findViewById(R.id.submitQuoteBtn);
        jobTitle = findViewById(R.id.jobTitle);
        jobCategory = findViewById(R.id.jobCategory);
        jobLocation = findViewById(R.id.jobLocation);

        loadJobDetails();
        
        backButton.setOnClickListener(v -> onBackPressed());
        
        submitQuoteBtn.setOnClickListener(v -> submitQuote());
    }

    private void loadJobDetails() {
        RetrofitClient.getInstance().getApiService()
            .getPostById(postId)
            .enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    Post post = response.body();
                    String category = post.getWorkerCategory() == null ? "Job" : post.getWorkerCategory();
                    String location = post.getLocation() == null || post.getLocation().isEmpty()
                        ? "Location not available"
                        : post.getLocation();
                    String title = post.getDescription() == null || post.getDescription().isEmpty()
                        ? category
                        : post.getDescription();

                    jobTitle.setText(title);
                    jobCategory.setText(category);
                    jobLocation.setText("Location: " + location);
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    // Keep layout defaults if post details fail to load.
                }
            });
    }
    
    private void submitQuote() {
        String price = priceInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();
        
        // Validation
        if (price.isEmpty()) {
            priceInput.setError("Price is required");
            return;
        }
        
        if (time.isEmpty()) {
            timeInput.setError("Estimated time is required");
            return;
        }
        
        if (message.isEmpty()) {
            messageInput.setError("Message is required");
            return;
        }
        
        // Disable button during submission
        submitQuoteBtn.setEnabled(false);
        submitQuoteBtn.setText("Submitting...");
        
        // Create request body
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("postId", postId);
        if (workerId > 0) {
            quoteData.put("workerId", workerId);
        } else if (contractorId > 0) {
            quoteData.put("contractorId", contractorId);
        }
        quoteData.put("price", Double.parseDouble(price));
        quoteData.put("estimatedTime", time);
        quoteData.put("message", message);
        
        // Submit quote
        RetrofitClient.getInstance().getApiService()
            .submitQuote(quoteData)
            .enqueue(new Callback<Quote>() {
                @Override
                public void onResponse(Call<Quote> call, Response<Quote> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SubmitQuoteActivity.this, "Quote submitted successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(SubmitQuoteActivity.this, "Failed to submit quote", Toast.LENGTH_SHORT).show();
                        submitQuoteBtn.setEnabled(true);
                        submitQuoteBtn.setText("Submit Quote");
                    }
                }
                
                @Override
                public void onFailure(Call<Quote> call, Throwable t) {
                    Toast.makeText(SubmitQuoteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    submitQuoteBtn.setEnabled(true);
                    submitQuoteBtn.setText("Submit Quote");
                }
            });
    }
}

