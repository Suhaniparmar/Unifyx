package com.example.unifyx.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
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
import com.example.unifyx.worker.WorkerBid;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PostAdapterViewer extends RecyclerView.Adapter<PostAdapterViewer.PostViewHolder> {
    private List<Post> postList;
    private Context context;

    public PostAdapterViewer(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_viewer, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {


        Post post = postList.get(position);
        Log.d("PostAdapterViewer", "Binding Post: " + post.toString());
        Log.d("PostAdapterViewer", "Post Photo List: " + post.getPhoto());
        Log.d("PostAdapterViewer", "Binding Post ID: " + post.getPostId());

        holder.title.setText(post.getWorkerCategory());
        holder.description.setText(post.getDescription());
        holder.location.setText("📍 " + post.getLocation());
        holder.duration.setText("⏳ Duration: " + post.getDuration());

        // Load image from Cloudinary using Glide
        if (post.getPhoto() != null && !post.getPhoto().isEmpty()) {
            String imageUrl = post.getPhoto().get(0);
            Log.d("PostAdapterViewer", "Loading Image URL: " + imageUrl);

            if (imageUrl == null || imageUrl.isEmpty()) {
                Log.e("PostAdapterViewer", "Image URL is null or empty");
            } else {
                Log.d("PostAdapterViewer", "Final Image URL: " + imageUrl);
            }

            if (!imageUrl.startsWith("https://")) {
                imageUrl = imageUrl.replace("http://", "https://");
                Log.d("PostAdapterViewer", "Updated to HTTPS: " + imageUrl);
            }

            if (holder.postImage == null) {
                Log.e("GlideDebug", "ImageView is NULL!");
            } else {
                Log.d("GlideDebug", "ImageView is initialized properly.");
            }



            Glide.with(context)
                    .load(imageUrl) // Load the first image from the list
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {

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
            Log.e("PostAdapterViewer", "No Image Found in post.getPhoto()");
            holder.postImage.setImageResource(R.drawable.placeholder);

        }
        holder.itemView.setOnClickListener(v -> {
            Log.d("PostAdapterViewer", "Sending postId: " + post.getPostId());

            SharedPreferences sharedPreferences2 = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String uid = sharedPreferences2.getString("uid", null);
            Intent intent = new Intent(context, WorkerBid.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("senderId",uid);

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
        Button BidNow;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            title = itemView.findViewById(R.id.postTitle);
            description = itemView.findViewById(R.id.postDescription);
            location = itemView.findViewById(R.id.postLocation);
            duration = itemView.findViewById(R.id.postDuration);
            BidNow = itemView.findViewById(R.id.btnBidNow);
        }
    }
    public void updateData(List<Post> newPostList) {
        this.postList.clear();
        this.postList.addAll(newPostList);
        notifyDataSetChanged();
    }



}
