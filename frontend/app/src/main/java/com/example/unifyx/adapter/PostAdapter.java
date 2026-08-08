package com.example.unifyx.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.unifyx.R;
import com.example.unifyx.model.Post;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.owner.PostMatchesActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private ApiService apiService;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_owner, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        Log.d("PostAdapter", "Binding Post: " + post.toString());

        holder.title.setText(post.getWorkerCategory());
        holder.description.setText(post.getDescription());
        holder.location.setText("📍 " + post.getLocation());
        holder.duration.setText("⏳ Duration: " + post.getDuration());

        // Load image using Glide
        if (post.getPhoto() != null && !post.getPhoto().isEmpty()) {
            String imageUrl = post.getPhoto().get(0);
            if (!imageUrl.startsWith("https://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Image Load Failed: " + e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Glide", "Image Loaded Successfully");
                            return false;
                        }
                    })
                    .into(holder.postImage);
        } else {
            holder.postImage.setImageResource(R.drawable.placeholder);
        }

        // Delete Post button
        holder.actionButton.setText("Delete");
        holder.actionButton.setOnClickListener(v -> {
            deletePost(post.getPostId(), position);
        });

        // View Matches button (Smart Hiring System)
        holder.showBidsButton.setText("View Matches");
        holder.showBidsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostMatchesActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, location, duration;
        ImageView postImage;
        Button actionButton, showBidsButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            title = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            location = itemView.findViewById(R.id.postLocation);
            duration = itemView.findViewById(R.id.postDuration);
            actionButton = itemView.findViewById(R.id.actionButton);
            showBidsButton = itemView.findViewById(R.id.showBidsButton); // New reference
        }
    }

    private void deletePost(int postId, int position) {
        apiService = RetrofitClient.getInstance().getApiService();
        Call<Void> call = apiService.deletePost(postId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
