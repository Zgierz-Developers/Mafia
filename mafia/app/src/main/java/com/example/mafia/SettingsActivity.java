package com.example.mafia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AvatarAdapter.OnAvatarClickListener {

    private EditText nicknameEditText;
    private Button saveButton;
    private RecyclerView avatarsRecyclerView;
    private AvatarAdapter avatarAdapter;
    private List<Integer> avatarDrawables;
    private int selectedAvatar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nicknameEditText = findViewById(R.id.nicknameEditText);
        saveButton = findViewById(R.id.saveButton);
        avatarsRecyclerView = findViewById(R.id.avatarsRecyclerView);

        // Inicjalizacja listy ikon
        avatarDrawables = new ArrayList<>();
        avatarDrawables.add(R.drawable.profile_logo_1);
        avatarDrawables.add(R.drawable.profile_logo_2);
        avatarDrawables.add(R.drawable.profile_logo_3);
        avatarDrawables.add(R.drawable.profile_logo_4);
        // Dodaj więcej ikon, jeśli masz

        // Ustawienie adaptera dla RecyclerView
        avatarAdapter = new AvatarAdapter(this, avatarDrawables, this);
        avatarsRecyclerView.setAdapter(avatarAdapter);
        avatarsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Pobierz zapisany nick i ikonę (jeśli istnieją)
        loadSavedData();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSavedData() {
        // Pobierz zapisany nick i ikonę z SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedNickname = sharedPreferences.getString("nickname", "");
        selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);

        nicknameEditText.setText(savedNickname);
        if (selectedAvatar != -1) {
            avatarAdapter.setSelectedAvatar(selectedAvatar);
        }
    }

    private void saveSettings() {
        String newNickname = nicknameEditText.getText().toString();

        if (newNickname.isEmpty()) {
            Toast.makeText(this, "Nick nie może być pusty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedAvatar == -1) {
            Toast.makeText(this, "Wybierz ikonę profilową", Toast.LENGTH_SHORT).show();
            return;
        }

        // Zapisz nowy nick i wybraną ikonę do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname", newNickname);
        editor.putInt("selectedAvatar", selectedAvatar);
        editor.apply();

        Toast.makeText(this, "Ustawienia zapisane", Toast.LENGTH_SHORT).show();
        finish(); // Zamknij aktywność po zapisaniu
    }

    @Override
    public void onAvatarClick(int avatarDrawable, int position) {
        selectedAvatar = position;
    }
    public int getSelectedAvatar(){
        return selectedAvatar;
    }
}