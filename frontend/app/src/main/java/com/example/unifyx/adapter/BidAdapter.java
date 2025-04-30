package com.example.unifyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.model.BidRaise;

import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private Context context;
    private List<BidRaise> bidList;

    public BidAdapter(Context context, List<BidRaise> bidList) {
        this.context = context;
        this.bidList = bidList;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bid, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        BidRaise bid = bidList.get(position);

        holder.amountTextView.setText("Amount: ₹" + bid.getAmount());
        holder.durationTextView.setText("Duration: " + bid.getDuration());

        if (bid.getSender() != null) {
            holder.workerEmailTextView.setText("Email: " + bid.getSender().getEmail());
            holder.workerPhoneTextView.setText("Phone: " + bid.getSender().getPhoneNo());
        } else {
            holder.workerEmailTextView.setText("Email: N/A");
            holder.workerPhoneTextView.setText("Phone: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public static class BidViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, durationTextView, workerEmailTextView, workerPhoneTextView;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            workerEmailTextView = itemView.findViewById(R.id.workerEmailTextView);
            workerPhoneTextView = itemView.findViewById(R.id.workerPhoneTextView);
        }
    }
}
