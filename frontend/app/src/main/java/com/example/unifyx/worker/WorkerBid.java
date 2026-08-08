package com.example.unifyx.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
import com.example.unifyx.model.Post;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.model.BidRaise;
import com.example.unifyx.owner.PostBidsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerBid extends AppCompatActivity {

    private TextInputEditText amountInput, durationInput;
    private MaterialButton submitBidButton;
    private ApiService apiService;

    private int postId ;
    private int senderId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldRedirectToQuote()) {
            // Legacy activity kept only for compatibility; route to quote flow.
            Intent legacyIntent = getIntent();
            Intent quoteIntent = new Intent(this, SubmitQuoteActivity.class);
            quoteIntent.putExtra("postId", legacyIntent.getIntExtra("postId", -1));
            int workerId = legacyIntent.getIntExtra("workerId", -1);
            if (workerId <= 0) {
                workerId = legacyIntent.getIntExtra("senderId", -1);
            }
            quoteIntent.putExtra("workerId", workerId);
            startActivity(quoteIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_bid_raise);

        // Initialize Views
        amountInput = findViewById(R.id.amountInput);
        durationInput = findViewById(R.id.durationInput);
        submitBidButton = findViewById(R.id.submitBidButton);
        findViewById(R.id.btn_back).setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        // Retrieve Intent Data
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", -1);
        senderId = intent.getIntExtra("senderId", -1);

        if (senderId == -1) {
            SharedPreferences prefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);
            senderId = prefs.getInt("senderId", -1); // Default to -1 if not found
        }


        Log.d("WorkerBid", "Received postId: " + postId + ", senderId: " + senderId);



        // Validate postId
        if (postId == -1) {
            Log.e("WorkerBid", "Invalid Post ID received");
            Toast.makeText(this, "Invalid Post ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no valid postId is received
            return;
        }

        if (senderId == -1) {
            Log.e("WorkerBid", "Sender ID is NULL!");
            Toast.makeText(this, "Session expired. Please open profile once and try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Keep session key fresh for later bid calls.
        SharedPreferences.Editor sessionEditor = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
        sessionEditor.putInt("senderId", senderId);
        sessionEditor.apply();


        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);



        submitBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBid();

            }
        });
    }

    private boolean shouldRedirectToQuote() {
        return true;
    }


    private void submitBid() {
        String amountText = amountInput.getText().toString().trim();
        String durationText = durationInput.getText().toString().trim();

        if (amountText.isEmpty() || durationText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid bid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Bid amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.raiseBid(senderId, postId, amount, durationText).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    SharedPreferences prefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("postId", postId);
                    editor.putInt("senderId", senderId);
                    editor.apply(); // Don't forget this

                    Toast.makeText(WorkerBid.this, "Bid raised successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkerBid.this, WorkerHome.class);
                    startActivity(intent);

                    finish();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("WorkerBid", "Failed: " + response.code() + " - " + errorBody);
                            Toast.makeText(WorkerBid.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(WorkerBid.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(WorkerBid.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        Log.e("WorkerBid", "Error parsing response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WorkerBid.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WorkerBid", "Network Error", t);
            }
        });
    }

    private void navigateBack() {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        startActivity(new Intent(this, WorkerHome.class));
        finish();
    }

}
