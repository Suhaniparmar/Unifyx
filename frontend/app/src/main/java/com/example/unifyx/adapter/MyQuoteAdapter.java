package com.example.unifyx.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.model.Quote;
import com.example.unifyx.owner.PostMatchesActivity;

import java.util.List;

public class MyQuoteAdapter extends RecyclerView.Adapter<MyQuoteAdapter.MyQuoteViewHolder> {

    private final Context context;
    private final List<Quote> quotes;

    public MyQuoteAdapter(Context context, List<Quote> quotes) {
        this.context = context;
        this.quotes = quotes;
    }

    @NonNull
    @Override
    public MyQuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_quote, parent, false);
        return new MyQuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        String title = quote.getPost() != null && quote.getPost().getWorkerCategory() != null
                ? quote.getPost().getWorkerCategory()
                : "Job #" + quote.getPostId();
        holder.jobTitle.setText(title);
        holder.jobDescription.setText(quote.getPost() != null && quote.getPost().getDescription() != null
                ? quote.getPost().getDescription()
                : "No description available");
        holder.jobLocation.setText("📍 " + (quote.getPost() != null && quote.getPost().getLocation() != null
                ? quote.getPost().getLocation()
                : "Location unavailable"));
        holder.quotePrice.setText("₹" + (quote.getPrice() == null ? "0" : String.valueOf(quote.getPrice().intValue())));
        holder.estimatedTime.setText(quote.getEstimatedTime() == null ? "Not provided" : quote.getEstimatedTime());
        holder.quoteMessage.setText(quote.getMessage() == null ? "" : quote.getMessage());
        holder.statusBadge.setText(quote.getStatus() == null ? "PENDING" : quote.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (quote.getPostId() > 0) {
                Intent intent = new Intent(context, PostMatchesActivity.class);
                intent.putExtra("postId", quote.getPostId());
                intent.putExtra("initialTab", 1);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Post details unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    static class MyQuoteViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobDescription, jobLocation, quotePrice, estimatedTime, statusBadge, quoteMessage;

        MyQuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            quotePrice = itemView.findViewById(R.id.quotePrice);
            estimatedTime = itemView.findViewById(R.id.estimatedTime);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            quoteMessage = itemView.findViewById(R.id.quoteMessage);
        }
    }
}

