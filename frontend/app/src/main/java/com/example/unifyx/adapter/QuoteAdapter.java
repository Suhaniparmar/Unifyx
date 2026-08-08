package com.example.unifyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unifyx.R;
import com.example.unifyx.model.Quote;
import com.example.unifyx.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying quotes from workers
 */
public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {
    
    private Context context;
    private List<Quote> quotes;
    private int postId;
    
    public QuoteAdapter(Context context, List<Quote> quotes, int postId) {
        this.context = context;
        this.quotes = quotes;
        this.postId = postId;
    }
    
    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.bind(quote);
    }
    
    @Override
    public int getItemCount() {
        return quotes.size();
    }
    
    public class QuoteViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView workerAvatar;
        private TextView workerName;
        private TextView submitTime;
        private TextView statusBadge;
        private TextView quotePrice;
        private TextView estimatedTime;
        private TextView quoteMessage;
        private Button rejectBtn;
        private Button acceptBtn;
        
        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            workerAvatar = itemView.findViewById(R.id.workerAvatar);
            workerName = itemView.findViewById(R.id.workerName);
            submitTime = itemView.findViewById(R.id.submitTime);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            quotePrice = itemView.findViewById(R.id.quotePrice);
            estimatedTime = itemView.findViewById(R.id.estimatedTime);
            quoteMessage = itemView.findViewById(R.id.quoteMessage);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
        }
        
        public void bind(Quote quote) {
            if (quote.getWorker() != null && quote.getWorker().getName() != null) {
                workerName.setText(quote.getWorker().getName());
            } else if (quote.getContractor() != null && quote.getContractor().getName() != null) {
                workerName.setText(quote.getContractor().getName());
            } else {
                workerName.setText("Service Provider");
            }

            Double price = quote.getPrice() == null ? 0.0 : quote.getPrice();
            quotePrice.setText("₹" + String.format("%.0f", price));
            estimatedTime.setText(quote.getEstimatedTime() == null ? "Not provided" : quote.getEstimatedTime());
            quoteMessage.setText(quote.getMessage() == null ? "No message" : quote.getMessage());
            statusBadge.setText(quote.getStatus() == null ? "PENDING" : quote.getStatus());
            
            // Update status badge color
            if ("ACCEPTED".equals(quote.getStatus())) {
                statusBadge.setBackgroundResource(R.drawable.badge_accepted);
            } else if ("REJECTED".equals(quote.getStatus())) {
                statusBadge.setBackgroundResource(R.drawable.badge_rejected);
            }
            
            // Disable buttons if not pending
            if (!"PENDING".equals(quote.getStatus())) {
                rejectBtn.setEnabled(false);
                acceptBtn.setEnabled(false);
            }
            
            // Reject button
            rejectBtn.setOnClickListener(v -> rejectQuote(quote));
            
            // Accept button
            acceptBtn.setOnClickListener(v -> acceptQuote(quote));
        }
        
        private void acceptQuote(Quote quote) {
            acceptBtn.setEnabled(false);
            acceptBtn.setText("Accepting...");
            
            RetrofitClient.getInstance().getApiService()
                .acceptQuote(quote.getQuoteId())
                .enqueue(new Callback<Quote>() {
                    @Override
                    public void onResponse(Call<Quote> call, Response<Quote> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Quote accepted & worker hired!", Toast.LENGTH_SHORT).show();
                            quote.setStatus("ACCEPTED");
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                QuoteAdapter.this.notifyItemChanged(position);
                            }
                        } else {
                            Toast.makeText(context, "Failed to accept quote", Toast.LENGTH_SHORT).show();
                            acceptBtn.setEnabled(true);
                            acceptBtn.setText("Accept & Hire");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Quote> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        acceptBtn.setEnabled(true);
                        acceptBtn.setText("Accept & Hire");
                    }
                });
        }
        
        private void rejectQuote(Quote quote) {
            rejectBtn.setEnabled(false);
            rejectBtn.setText("Rejecting...");
            
            RetrofitClient.getInstance().getApiService()
                .rejectQuote(quote.getQuoteId())
                .enqueue(new Callback<Quote>() {
                    @Override
                    public void onResponse(Call<Quote> call, Response<Quote> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Quote rejected", Toast.LENGTH_SHORT).show();
                            quote.setStatus("REJECTED");
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                QuoteAdapter.this.notifyItemChanged(position);
                            }
                        } else {
                            Toast.makeText(context, "Failed to reject quote", Toast.LENGTH_SHORT).show();
                            rejectBtn.setEnabled(true);
                            rejectBtn.setText("Reject");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Quote> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        rejectBtn.setEnabled(true);
                        rejectBtn.setText("Reject");
                    }
                });
        }
    }
}

