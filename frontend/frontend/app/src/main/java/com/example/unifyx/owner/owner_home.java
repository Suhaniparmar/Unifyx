package com.example.unifyx.owner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.unifyx.R;

public class owner_home extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        ImageView menuIcon = findViewById(R.id.menu);

        // Set the click listener for the menu icon
        menuIcon.setOnClickListener(v -> {
            // Create a PopupMenu
            PopupMenu popupMenu = new PopupMenu(owner_home.this, menuIcon);

            // Inflate the menu from the XML resource
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.home_menu, popupMenu.getMenu());

            // Set the menu item click listener
//            popupMenu.setOnMenuItemClickListener(item -> {
//                // Handle the menu item clicks
//                switch (item.getItemId()) {
//                    case R.id.option1:
//                        Toast.makeText(owner_home.this, "Option 1 selected", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.option2:
//                        Toast.makeText(owner_home.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.option3:
//                        Toast.makeText(owner_home.this, "Option 3 selected", Toast.LENGTH_SHORT).show();
//                        return true;
//                    default:
//                        return false;
//                }
//            });

            // Show the popup menu
            popupMenu.show();
        });
    }
}