package com.example.unifyx.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.unifyx.R;
import com.example.unifyx.login;
import com.example.unifyx.owner.MyQuotesActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class contractor_home extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_home);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        ImageView menuIcon = findViewById(R.id.menu);
        ImageView profileIcon = findViewById(R.id.imageView2);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        profileIcon.setOnClickListener(v -> Toast.makeText(this, "Contractor profile coming soon", Toast.LENGTH_SHORT).show());

        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                Toast.makeText(contractor_home.this, "Contractor profile coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_my_projects) {
                startActivity(new Intent(contractor_home.this, MyQuotesActivity.class));
            } else if (itemId == R.id.nav_saved_contractors) {
                Toast.makeText(contractor_home.this, "Saved Contractors coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_notifications) {
                Toast.makeText(contractor_home.this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(contractor_home.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                userPrefs.clear();
                userPrefs.apply();

                SharedPreferences.Editor appPrefs = getSharedPreferences("UnifyxPrefs", MODE_PRIVATE).edit();
                appPrefs.clear();
                appPrefs.apply();

                Intent loginIntent = new Intent(contractor_home.this, login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
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