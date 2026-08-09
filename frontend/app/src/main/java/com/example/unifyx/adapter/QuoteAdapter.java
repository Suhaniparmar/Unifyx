package com.example.unifyx.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.unifyx.R;
import com.example.unifyx.model.Hire;
import com.example.unifyx.model.Quote;
import com.example.unifyx.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
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
    private List<Hire> activeHires = new ArrayList<>();

    public QuoteAdapter(Context context, List<Quote> quotes, int postId) {
        this.context = context;
        this.quotes = quotes;
        this.postId = postId;
    }

    // Supplies the post's currently-ACTIVE hires (fetched separately from
    // quotes) so accepted quote cards can show Mark Complete / Rate & Review.
    // Note: the backend only exposes an ACTIVE-filtered endpoint for this, so
    // a hire that was completed in a previous session (and not yet rated)
    // will not be re-surfaced here after a fresh reload of this screen.
    public void setActiveHires(List<Hire> hires) {
        this.activeHires = hires != null ? hires : new ArrayList<>();
        notifyDataSetChanged();
    }

    private Hire findHireForQuote(Quote quote) {
        if (quote.getWorker() == null) {
            return null;
        }
        for (Hire hire : activeHires) {
            if (hire.getWorker() != null && hire.getWorker().getWorkerId() == quote.getWorker().getWorkerId()) {
                return hire;
            }
        }
        return null;
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
        private View hireSection;
        private TextView hireStatusText;
        private Button markCompleteBtn;
        private View ratingSection;
        private RatingBar ratingInput;
        private TextInputEditText reviewInput;
        private Button submitRatingBtn;

        private Hire currentHire;

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
            hireSection = itemView.findViewById(R.id.hireSection);
            hireStatusText = itemView.findViewById(R.id.hireStatusText);
            markCompleteBtn = itemView.findViewById(R.id.markCompleteBtn);
            ratingSection = itemView.findViewById(R.id.ratingSection);
            ratingInput = itemView.findViewById(R.id.ratingInput);
            reviewInput = itemView.findViewById(R.id.reviewInput);
            submitRatingBtn = itemView.findViewById(R.id.submitRatingBtn);
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

            currentHire = "ACCEPTED".equals(quote.getStatus()) ? findHireForQuote(quote) : null;
            updateHireSectionUi();
        }

        private void updateHireSectionUi() {
            if (currentHire == null) {
                hireSection.setVisibility(View.GONE);
                return;
            }

            hireSection.setVisibility(View.VISIBLE);
            String status = currentHire.getStatus() == null ? "ACTIVE" : currentHire.getStatus();

            if ("ACTIVE".equals(status)) {
                hireStatusText.setText("Hire: ACTIVE");
                markCompleteBtn.setVisibility(View.VISIBLE);
                markCompleteBtn.setEnabled(true);
                markCompleteBtn.setText("Mark Complete");
                ratingSection.setVisibility(View.GONE);
            } else if ("COMPLETED".equals(status) && currentHire.getOwnerRating() == null) {
                hireStatusText.setText("Hire: COMPLETED");
                markCompleteBtn.setVisibility(View.GONE);
                ratingSection.setVisibility(View.VISIBLE);
                ratingInput.setRating(0);
                submitRatingBtn.setEnabled(true);
                submitRatingBtn.setText("Submit Rating");
            } else if ("COMPLETED".equals(status)) {
                hireStatusText.setText("Hire: COMPLETED — Rated " + String.format("%.0f", currentHire.getOwnerRating()) + "/5");
                markCompleteBtn.setVisibility(View.GONE);
                ratingSection.setVisibility(View.GONE);
            } else {
                hireStatusText.setText("Hire: " + status);
                markCompleteBtn.setVisibility(View.GONE);
                ratingSection.setVisibility(View.GONE);
            }

            markCompleteBtn.setOnClickListener(v -> confirmMarkComplete(currentHire));
            submitRatingBtn.setOnClickListener(v -> submitRating(currentHire));
        }

        private void confirmMarkComplete(Hire hire) {
            new AlertDialog.Builder(context)
                .setTitle("Mark Complete")
                .setMessage("Mark this hire as complete? This confirms the work is finished.")
                .setPositiveButton("Mark Complete", (dialog, which) -> markHireComplete(hire))
                .setNegativeButton("Cancel", null)
                .show();
        }

        private void markHireComplete(Hire hire) {
            markCompleteBtn.setEnabled(false);
            markCompleteBtn.setText("Updating...");

            RetrofitClient.getInstance().getApiService()
                .completeHire(hire.getHireId())
                .enqueue(new Callback<Hire>() {
                    @Override
                    public void onResponse(Call<Hire> call, Response<Hire> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentHire = response.body();
                            Toast.makeText(context, "Hire marked complete.", Toast.LENGTH_SHORT).show();
                            updateHireSectionUi();
                        } else {
                            Toast.makeText(context, "Failed to mark hire complete.", Toast.LENGTH_SHORT).show();
                            markCompleteBtn.setEnabled(true);
                            markCompleteBtn.setText("Mark Complete");
                        }
                    }

                    @Override
                    public void onFailure(Call<Hire> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        markCompleteBtn.setEnabled(true);
                        markCompleteBtn.setText("Mark Complete");
                    }
                });
        }

        private void submitRating(Hire hire) {
            float rating = ratingInput.getRating();
            if (rating < 1f) {
                Toast.makeText(context, "Please select a rating (1-5 stars).", Toast.LENGTH_SHORT).show();
                return;
            }

            String review = reviewInput.getText() != null ? reviewInput.getText().toString().trim() : "";

            submitRatingBtn.setEnabled(false);
            submitRatingBtn.setText("Submitting...");

            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("rating", (double) rating);
            ratingData.put("review", review.isEmpty() ? null : review);

            RetrofitClient.getInstance().getApiService()
                .rateHire(hire.getHireId(), ratingData)
                .enqueue(new Callback<Hire>() {
                    @Override
                    public void onResponse(Call<Hire> call, Response<Hire> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentHire = response.body();
                            Toast.makeText(context, "Rating submitted. Thank you!", Toast.LENGTH_SHORT).show();
                            updateHireSectionUi();
                        } else {
                            Toast.makeText(context, "Failed to submit rating.", Toast.LENGTH_SHORT).show();
                            submitRatingBtn.setEnabled(true);
                            submitRatingBtn.setText("Submit Rating");
                        }
                    }

                    @Override
                    public void onFailure(Call<Hire> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        submitRatingBtn.setEnabled(true);
                        submitRatingBtn.setText("Submit Rating");
                    }
                });
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
                            quote.setStatus("ACCEPTED");
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                QuoteAdapter.this.notifyItemChanged(position);
                            }
                            createHireForQuote(quote);
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

        // Quote acceptance only flips the quote's status; a Hire record is a
        // separate entity that must be created explicitly once the quote is
        // accepted, using the quote's own postId/workerId/price.
        private void createHireForQuote(Quote quote) {
            if (quote.getPost() == null || quote.getWorker() == null) {
                // Hire currently only supports worker quotes (Hire has no
                // contractor field on the backend); contractor quotes can be
                // accepted but not hired through this endpoint yet.
                Toast.makeText(context, "Quote accepted, but hiring could not be completed.", Toast.LENGTH_LONG).show();
                acceptBtn.setText("Accept & Hire");
                acceptBtn.setEnabled(true);
                return;
            }

            Map<String, Object> hireData = new HashMap<>();
            hireData.put("postId", quote.getPost().getPostId());
            hireData.put("workerId", quote.getWorker().getWorkerId());
            hireData.put("agreedPrice", quote.getPrice());

            RetrofitClient.getInstance().getApiService()
                .createHire(hireData)
                .enqueue(new Callback<Hire>() {
                    @Override
                    public void onResponse(Call<Hire> call, Response<Hire> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Quote accepted and worker hired successfully.", Toast.LENGTH_SHORT).show();
                            acceptBtn.setText("Hired");
                            currentHire = response.body();
                            updateHireSectionUi();
                        } else {
                            Toast.makeText(context, "Quote accepted, but hiring could not be completed.", Toast.LENGTH_LONG).show();
                            acceptBtn.setText("Accept & Hire");
                            acceptBtn.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Hire> call, Throwable t) {
                        Toast.makeText(context, "Quote accepted, but hiring could not be completed.", Toast.LENGTH_LONG).show();
                        acceptBtn.setText("Accept & Hire");
                        acceptBtn.setEnabled(true);
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
