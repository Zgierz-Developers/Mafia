package com.example.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private Button serverListButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        serverListButton = findViewById(R.id.serverListButton);
        settingsButton = findViewById(R.id.settingsButton);

        serverListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ServerListActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }
}