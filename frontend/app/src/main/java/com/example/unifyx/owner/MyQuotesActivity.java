package com.example.unifyx.owner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.MyQuoteAdapter;
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.Hire;
import com.example.unifyx.model.Quote;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuotesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyQuoteAdapter adapter;
    private final List<Quote> quoteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quotes);

        recyclerView = findViewById(R.id.myQuotesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyQuoteAdapter(this, quoteList);
        recyclerView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        loadMyQuotes();
    }

    private void loadMyQuotes() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String uid = prefs.getString("uid", null);
        String email = prefs.getString("email", null);
        if (uid == null || email == null) {
            Toast.makeText(this, "Session missing", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApiService().getUserRole(uid).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MyQuotesActivity.this, "Failed to resolve role", Toast.LENGTH_SHORT).show();
                    return;
                }
                String role = response.body().get("role");
                if ("contractor".equalsIgnoreCase(role)) {
                    loadContractorQuotes(email);
                } else {
                    loadWorkerQuotes(email);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(MyQuotesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWorkerQuotes(String email) {
        RetrofitClient.getInstance().getApiService().getWorker(email).enqueue(new Callback<WorkerProfile>() {
            @Override
            public void onResponse(Call<WorkerProfile> call, Response<WorkerProfile> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MyQuotesActivity.this, "Worker profile not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                int workerId = response.body().getWorkerId();
                RetrofitClient.getInstance().getApiService().getAllQuotesForWorker(workerId).enqueue(new QuoteListCallback());
                loadWorkerHires(workerId);
            }

            @Override
            public void onFailure(Call<WorkerProfile> call, Throwable t) {
                Toast.makeText(MyQuotesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContractorQuotes(String email) {
        RetrofitClient.getInstance().getApiService().getContractorProfile(email).enqueue(new Callback<ContractorProfile>() {
            @Override
            public void onResponse(Call<ContractorProfile> call, Response<ContractorProfile> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MyQuotesActivity.this, "Contractor profile not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                int contractorId = response.body().getContractorId();
                RetrofitClient.getInstance().getApiService().getAllQuotesForContractor(contractorId).enqueue(new QuoteListCallback());
            }

            @Override
            public void onFailure(Call<ContractorProfile> call, Throwable t) {
                Toast.makeText(MyQuotesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Read-only Hire status for the worker's own quotes. Not called for the
    // contractor path: Hire currently has no contractor relation at all
    // (backend Hire entity only supports WorkerProfile).
    private void loadWorkerHires(int workerId) {
        RetrofitClient.getInstance().getApiService().getHiresForWorker(workerId).enqueue(new Callback<List<Hire>>() {
            @Override
            public void onResponse(Call<List<Hire>> call, Response<List<Hire>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setHires(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Hire>> call, Throwable t) {
                // Non-blocking; quote list still works without hire state.
            }
        });
    }

    private class QuoteListCallback implements Callback<List<Quote>> {
        @Override
        public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
            if (!response.isSuccessful() || response.body() == null) {
                Toast.makeText(MyQuotesActivity.this, "Failed to load quotes", Toast.LENGTH_SHORT).show();
                return;
            }
            quoteList.clear();
            quoteList.addAll(response.body());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<List<Quote>> call, Throwable t) {
            Toast.makeText(MyQuotesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

