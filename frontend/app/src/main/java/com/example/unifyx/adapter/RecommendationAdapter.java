package com.example.unifyx.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.unifyx.model.Recommendation;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying recommended workers
 */
public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {
    
    private Context context;
    private List<Recommendation> recommendations;
    private int postId;
    private OnHireClickListener hireListener;
    
    public interface OnHireClickListener {
        void onHireClicked(Recommendation recommendation);
    }
    
    public RecommendationAdapter(Context context, List<Recommendation> recommendations, int postId) {
        this.context = context;
        this.recommendations = recommendations;
        this.postId = postId;
    }
    
    public void setHireListener(OnHireClickListener listener) {
        this.hireListener = listener;
    }
    
    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_worker_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);
        holder.bind(recommendation);
    }
    
    @Override
    public int getItemCount() {
        return recommendations.size();
    }
    
    public class RecommendationViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView workerAvatar;
        private TextView workerName;
        private TextView trustScoreLabel;
        private TextView distanceBadge;
        private TextView quotePrice;
        private TextView estimatedTime;
        private TextView reasonTag;
        private Button viewProfileBtn;
        private Button hireBtn;
        
        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            workerAvatar = itemView.findViewById(R.id.workerAvatar);
            workerName = itemView.findViewById(R.id.workerName);
            trustScoreLabel = itemView.findViewById(R.id.trustScoreLabel);
            distanceBadge = itemView.findViewById(R.id.distanceBadge);
            quotePrice = itemView.findViewById(R.id.quotePrice);
            estimatedTime = itemView.findViewById(R.id.estimatedTime);
            reasonTag = itemView.findViewById(R.id.reasonTag);
            viewProfileBtn = itemView.findViewById(R.id.viewProfileBtn);
            hireBtn = itemView.findViewById(R.id.hireBtn);
        }
        
        public void bind(Recommendation recommendation) {
            WorkerProfile worker = recommendation.getWorker();
            
            if (worker != null) {
                workerName.setText(worker.getName());
                distanceBadge.setText(String.format("%.1f km", recommendation.getDistanceKm()));
                
                // Display recommendation reason
                String reason = String.format("✓ %d%% match • Rank #%d", 
                    recommendation.getScore().intValue(), 
                    recommendation.getRank());
                reasonTag.setText(reason);
            }
            
            // Use recommendation score as trust indicator fallback.
            Double trustScore = recommendation.getScore() == null ? 5.0 : recommendation.getScore();
            trustScoreLabel.setText(String.format("%.1f/10 Trust", trustScore));
            
            // View Profile button
            viewProfileBtn.setOnClickListener(v -> {
                if (worker != null) {
                    // Navigate to worker profile - implement based on your app structure
                    Toast.makeText(context, "View " + worker.getName() + "'s profile", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Hire button
            hireBtn.setOnClickListener(v -> {
                if (hireListener != null) {
                    hireListener.onHireClicked(recommendation);
                } else {
                    hireWorker(recommendation);
                }
            });
        }
        
        private void hireWorker(Recommendation recommendation) {
            Map<String, Object> hireData = new HashMap<>();
            hireData.put("postId", postId);
            hireData.put("workerId", recommendation.getWorkerId());
            hireData.put("agreedPrice", 0); // Price will be from quote if exists
            
            hireBtn.setEnabled(false);
            hireBtn.setText("Hiring...");
            
            RetrofitClient.getInstance().getApiService()
                .createHire(hireData)
                .enqueue(new Callback<com.example.unifyx.model.Hire>() {
                    @Override
                    public void onResponse(Call<com.example.unifyx.model.Hire> call, Response<com.example.unifyx.model.Hire> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Worker hired successfully!", Toast.LENGTH_SHORT).show();
                            hireBtn.setText("Hired ✓");
                        } else {
                            Toast.makeText(context, "Failed to hire worker", Toast.LENGTH_SHORT).show();
                            hireBtn.setEnabled(true);
                            hireBtn.setText("Hire Now");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<com.example.unifyx.model.Hire> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        hireBtn.setEnabled(true);
                        hireBtn.setText("Hire Now");
                    }
                });
        }
    }
}

