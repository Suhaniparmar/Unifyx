package com.example.unifyx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.contractor.*;
import com.example.unifyx.owner.*;
import com.example.unifyx.worker.*;

public class choose_role extends AppCompatActivity {

    Button owner,contractor,worker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        owner = (Button) findViewById(R.id.owner);
        contractor = findViewById(R.id.contractor);
        worker = findViewById(R.id.worker);
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(choose_role.this, owner_info.class);
                startActivity(intent);
            }
        });
        contractor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(choose_role.this, contractor_info.class);
                startActivity(intent);
            }
        });
        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(choose_role.this, worker_info.class);
                startActivity(intent);
            }
        });
    }
}