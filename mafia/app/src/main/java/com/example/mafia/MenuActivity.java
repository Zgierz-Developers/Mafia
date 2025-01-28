package com.example.mafia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String IS_FIRST_RUN = "isFirstRun";

    private Button serverListButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean(IS_FIRST_RUN, true);

        if (isFirstRun) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_FIRST_RUN, false);
            editor.apply();
        } else {
            setContentView(R.layout.activity_menu);
        }

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