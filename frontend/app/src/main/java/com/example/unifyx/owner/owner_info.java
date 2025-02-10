package com.example.unifyx.owner;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;

public class owner_info extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info);

        // Get reference to Spinner
        Spinner locationSpinner = findViewById(R.id.location_spinner);

        // Handle selection
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                Toast.makeText(owner_info.this, "Selected: " + selectedCity, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case where nothing is selected
            }
        });
    }
}
