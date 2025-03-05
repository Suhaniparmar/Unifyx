package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.contractor_info;
import com.example.unifyx.owner.owner_info;
import com.example.unifyx.worker.worker_info;

public class choose_role extends AppCompatActivity {

    LinearLayout owner, contractor, worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        owner = findViewById(R.id.owner);
        contractor = findViewById(R.id.contractor);
        worker = findViewById(R.id.worker);

        owner.setOnClickListener(view -> {
            Intent intent = new Intent(choose_role.this, owner_info.class);
            startActivity(intent);
        });

        contractor.setOnClickListener(view -> {
            Intent intent = new Intent(choose_role.this, contractor_info.class);
            startActivity(intent);
        });

        worker.setOnClickListener(view -> {
            Intent intent = new Intent(choose_role.this, worker_info.class);
            startActivity(intent);
        });
    }
}
