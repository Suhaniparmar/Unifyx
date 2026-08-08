package com.example.unifyx.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.model.OwnerNotification;
import com.example.unifyx.network.RetrofitClient;
import com.example.unifyx.owner.PostMatchesActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerNotificationAdapter extends RecyclerView.Adapter<OwnerNotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<OwnerNotification> notifications;

    public OwnerNotificationAdapter(Context context, List<OwnerNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        OwnerNotification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.time.setText(notification.getCreatedAt());

        holder.itemView.setAlpha(notification.isRead() ? 0.7f : 1.0f);
        holder.itemView.setOnClickListener(v -> {
            markAsRead(notification);

            Intent intent = new Intent(context, PostMatchesActivity.class);
            intent.putExtra("postId", notification.getPostId());
            intent.putExtra("initialTab", 1);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void markAsRead(OwnerNotification notification) {
        RetrofitClient.getInstance().getApiService()
            .markOwnerNotificationRead(notification.getNotificationId())
            .enqueue(new Callback<OwnerNotification>() {
                @Override
                public void onResponse(Call<OwnerNotification> call, Response<OwnerNotification> response) {
                    // Fire-and-forget update.
                }

                @Override
                public void onFailure(Call<OwnerNotification> call, Throwable t) {
                    // Ignore non-blocking mark-read failure.
                }
            });
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;
        TextView time;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            time = itemView.findViewById(R.id.notificationTime);
        }
    }
}

