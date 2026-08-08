package com.example.unifyx.owner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
import androidx.cardview.widget.CardView;

import com.example.unifyx.login;
import com.example.unifyx.network.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.unifyx.model.OwnerNotification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class owner_home extends AppCompatActivity {

    private static final String SEEN_NOTIFICATION_IDS_KEY = "seenOwnerNotificationIds";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        ImageView menuIcon = findViewById(R.id.menu);
        ImageView profileLogo = findViewById(R.id.imageView2); // Profile logo ID
        ImageView searchLogo = findViewById(R.id.imageView3);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(owner_home.this, OwnerProfilePage.class));
            } else if (itemId == R.id.nav_my_projects) {
                Toast.makeText(owner_home.this, "My Projects coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_saved_contractors) {
                Toast.makeText(owner_home.this, "Saved Contractors coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(owner_home.this, OwnerNotificationsActivity.class));
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(owner_home.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                userPrefs.clear();
                userPrefs.apply();

                SharedPreferences.Editor appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
                appPrefs.clear();
                appPrefs.apply();

                Intent loginIntent = new Intent(owner_home.this, login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Handle profile logo click
        profileLogo.setOnClickListener(v -> {
            Intent intent = new Intent(owner_home.this, OwnerProfilePage.class);
            startActivity(intent);
        });

        searchLogo.setOnClickListener(v -> {
            Intent intent = new Intent(owner_home.this, OwnerSearch.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUnreadQuoteNotifications();
    }

    private void fetchUnreadQuoteNotifications() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String ownerUid = prefs.getString("uid", null);
        if (ownerUid == null || ownerUid.isEmpty()) {
            return;
        }

        RetrofitClient.getInstance().getApiService()
            .getOwnerNotifications(ownerUid)
            .enqueue(new Callback<List<OwnerNotification>>() {
                @Override
                public void onResponse(Call<List<OwnerNotification>> call, Response<List<OwnerNotification>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    SharedPreferences appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE);
                    Set<String> seenIds = new HashSet<>(appPrefs.getStringSet(SEEN_NOTIFICATION_IDS_KEY, new HashSet<>()));

                    int unseenUnreadCount = 0;
                    for (OwnerNotification notification : response.body()) {
                        String id = String.valueOf(notification.getNotificationId());
                        if (!notification.isRead() && !seenIds.contains(id)) {
                            unseenUnreadCount++;
                            seenIds.add(id);
                        }
                    }

                    if (unseenUnreadCount > 0) {
                        appPrefs.edit().putStringSet(SEEN_NOTIFICATION_IDS_KEY, seenIds).apply();
                        Toast.makeText(owner_home.this, "You have " + unseenUnreadCount + " new quote notification(s)", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OwnerNotification>> call, Throwable t) {
                    // Non-blocking; ignore network failure here.
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
