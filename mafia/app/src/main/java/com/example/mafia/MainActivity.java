package com.example.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText gameCodeEditText;
    private Button joinButton, disconnectButton;
    private GameClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameCodeEditText = findViewById(R.id.gameCodeEditText);
        joinButton = findViewById(R.id.joinButton);
        disconnectButton = findViewById(R.id.disconnectButton);
        disconnectButton.setEnabled(false);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameCode = gameCodeEditText.getText().toString();
                client = new GameClient(gameCode);
                client.connect();
                joinButton.setEnabled(false);
                disconnectButton.setEnabled(true);
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.disconnect();
                joinButton.setEnabled(true);
                disconnectButton.setEnabled(false);
            }
        });
    }
}