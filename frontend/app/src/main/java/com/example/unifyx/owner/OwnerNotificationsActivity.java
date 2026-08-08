package com.example.unifyx.owner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.adapter.OwnerNotificationAdapter;
import com.example.unifyx.model.OwnerNotification;
import com.example.unifyx.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerNotificationsActivity extends AppCompatActivity {

    private static final String SEEN_NOTIFICATION_IDS_KEY = "seenOwnerNotificationIds";

    private RecyclerView notificationsRecyclerView;
    private OwnerNotificationAdapter adapter;
    private final List<OwnerNotification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_notifications);

        ImageButton backButton = findViewById(R.id.backButton);
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OwnerNotificationAdapter(this, notifications);
        notificationsRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> onBackPressed());

        loadNotifications();
    }

    private void loadNotifications() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String ownerUid = prefs.getString("uid", null);
        if (ownerUid == null || ownerUid.isEmpty()) {
            Toast.makeText(this, "Owner session missing", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApiService()
            .getOwnerNotifications(ownerUid)
            .enqueue(new Callback<List<OwnerNotification>>() {
                @Override
                public void onResponse(Call<List<OwnerNotification>> call, Response<List<OwnerNotification>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(OwnerNotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    notifications.clear();
                    notifications.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    markNotificationsAsSeenAndRead(response.body());
                }

                @Override
                public void onFailure(Call<List<OwnerNotification>> call, Throwable t) {
                    Toast.makeText(OwnerNotificationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void markNotificationsAsSeenAndRead(List<OwnerNotification> notificationList) {
        SharedPreferences appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);
        Set<String> seenIds = new HashSet<>(appPrefs.getStringSet(SEEN_NOTIFICATION_IDS_KEY, new HashSet<>()));

        for (OwnerNotification notification : notificationList) {
            seenIds.add(String.valueOf(notification.getNotificationId()));

            if (!notification.isRead()) {
                RetrofitClient.getInstance().getApiService()
                    .markOwnerNotificationRead(notification.getNotificationId())
                    .enqueue(new Callback<OwnerNotification>() {
                        @Override
                        public void onResponse(Call<OwnerNotification> call, Response<OwnerNotification> response) {
                            // Fire-and-forget mark-as-read.
                        }

                        @Override
                        public void onFailure(Call<OwnerNotification> call, Throwable t) {
                            // Ignore non-blocking failure.
                        }
                    });
            }
        }

        appPrefs.edit().putStringSet(SEEN_NOTIFICATION_IDS_KEY, seenIds).apply();
    }
}

