package com.example.unifyx.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.owner.PostCreate;
import com.example.unifyx.R;
import androidx.cardview.widget.CardView;

public class owner_home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        ImageView menuIcon = findViewById(R.id.menu);
        ImageView profileLogo = findViewById(R.id.imageView2); // Profile logo ID
        CardView addPhotosCard = findViewById(R.id.cardView); // Add Photos CardView

        // Handle menu icon click
        menuIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(owner_home.this, menuIcon);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.home_menu, popupMenu.getMenu());
            popupMenu.show();
        });

        // Handle profile logo click
        profileLogo.setOnClickListener(v -> {
            Intent intent = new Intent(owner_home.this, OwnerProfilePage.class);
            startActivity(intent);
        });

        // Handle Add Photos CardView click
        addPhotosCard.setOnClickListener(v -> {
            Intent intent = new Intent(owner_home.this, PostCreate.class);
            startActivity(intent);
        });
    }
}
