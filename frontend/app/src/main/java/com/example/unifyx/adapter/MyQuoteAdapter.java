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
import com.example.unifyx.model.Hire;
import com.example.unifyx.model.Quote;
import com.example.unifyx.owner.PostMatchesActivity;

import java.util.ArrayList;
import java.util.List;

public class MyQuoteAdapter extends RecyclerView.Adapter<MyQuoteAdapter.MyQuoteViewHolder> {

    private final Context context;
    private final List<Quote> quotes;
    private List<Hire> hires = new ArrayList<>();

    public MyQuoteAdapter(Context context, List<Quote> quotes) {
        this.context = context;
        this.quotes = quotes;
    }

    // Read-only Hire status for this worker's own quotes. Rating/completion
    // actions live on the owner's side (PostMatchesActivity/QuoteAdapter) —
    // this only displays current state, matching the backend's ownerRating/
    // ownerReview fields, which are not something the worker submits.
    public void setHires(List<Hire> hires) {
        this.hires = hires != null ? hires : new ArrayList<>();
        notifyDataSetChanged();
    }

    private Hire findHireForQuote(Quote quote) {
        if (quote.getPost() == null) {
            return null;
        }
        for (Hire hire : hires) {
            if (hire.getPost() != null && hire.getPost().getPostId() == quote.getPost().getPostId()) {
                return hire;
            }
        }
        return null;
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

        Hire hire = findHireForQuote(quote);
        if (hire != null) {
            String status = hire.getStatus() == null ? "ACTIVE" : hire.getStatus();
            String text = "COMPLETED".equals(status) && hire.getOwnerRating() != null
                    ? "Hire: COMPLETED — Rated " + String.format("%.0f", hire.getOwnerRating()) + "/5"
                    : "Hire: " + status;
            holder.hireStatusText.setText(text);
            holder.hireStatusText.setVisibility(View.VISIBLE);
        } else {
            holder.hireStatusText.setVisibility(View.GONE);
        }

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
        TextView jobTitle, jobDescription, jobLocation, quotePrice, estimatedTime, statusBadge, quoteMessage, hireStatusText;

        MyQuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            quotePrice = itemView.findViewById(R.id.quotePrice);
            estimatedTime = itemView.findViewById(R.id.estimatedTime);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            quoteMessage = itemView.findViewById(R.id.quoteMessage);
            hireStatusText = itemView.findViewById(R.id.hireStatusText);
        }
    }
}

