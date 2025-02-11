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

public class SettingsActivity extends AppCompatActivity implements AvatarAdapter.OnAvatarClickListener, OnColorClickListener {

    private EditText nicknameEditText;
    private Button saveButton;
    private RecyclerView avatarsRecyclerView;
    private AvatarAdapter avatarAdapter;
    private List<Integer> avatarDrawables;
    private RecyclerView colorsRecyclerView;
    private ColorAdapter colorAdapter;
    private List<Integer> colorsDrawables;
    private int selectedAvatar = -1;
    private int selectedNickColor = -1;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        nicknameEditText = findViewById(R.id.nicknameEditText);
        saveButton = findViewById(R.id.saveButton);
        avatarsRecyclerView = findViewById(R.id.avatarsRecyclerView);
        colorsRecyclerView = findViewById(R.id.colorsRecyclerView);

        // Avatary
        avatarDrawables = new ArrayList<>();
        avatarDrawables.add(R.drawable.profile_logo_1);
        avatarDrawables.add(R.drawable.profile_logo_2);
        avatarDrawables.add(R.drawable.profile_logo_3);
        avatarDrawables.add(R.drawable.profile_logo_4);

        avatarAdapter = new AvatarAdapter(this, avatarDrawables, this);
        avatarsRecyclerView.setAdapter(avatarAdapter);
        avatarsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Kolory
        colorsDrawables = new ArrayList<>();
        colorsDrawables.add(R.color.dark_red);
        colorsDrawables.add(R.color.red);
        colorsDrawables.add(R.color.orange);
        colorsDrawables.add(R.color.yellow);
        colorsDrawables.add(R.color.dark_green);
        colorsDrawables.add(R.color.green);
        colorsDrawables.add(R.color.light_green);
        colorsDrawables.add(R.color.blue);
        colorsDrawables.add(R.color.dark_blue);
        colorsDrawables.add(R.color.light_blue);
        colorsDrawables.add(R.color.purple);
        colorsDrawables.add(R.color.pink);
        colorsDrawables.add(R.color.brown);
        colorsDrawables.add(R.color.black);
        colorsDrawables.add(R.color.gray);
        colorsDrawables.add(R.color.white);

        colorAdapter = new ColorAdapter(this, colorsDrawables, this);
        colorsRecyclerView.setAdapter(colorAdapter);
        colorsRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        // Wczytywanie danych
        loadSavedData();

        saveButton.setOnClickListener(v -> saveSettings());
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
        if (selectedNickColor == -1) {
            Toast.makeText(this, "Wybierz kolor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Zapisz nowy nick i wybraną ikonę do SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname", newNickname);
        editor.putInt("selectedAvatar", selectedAvatar);
        editor.putInt("selectedNickColor", selectedNickColor);
        editor.apply();

        Toast.makeText(this, "Ustawienia zapisane", Toast.LENGTH_SHORT).show();
        finish(); // Zamknij aktywność po zapisaniu
    }

    private void loadSavedData() {
        // Pobierz zapisany nick i ikonę z SharedPreferences
        String savedNickname = sharedPreferences.getString("nickname", "");
        selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);
        selectedNickColor = sharedPreferences.getInt("selectedNickColor", -1);

        nicknameEditText.setText(savedNickname);

        if (selectedAvatar != -1) {
            avatarAdapter.setSelectedAvatar(selectedAvatar);
        }
        if (selectedNickColor != -1) {
            colorAdapter.setSelectedColor(selectedNickColor);
        }
    }

    @Override
    public void onAvatarClick(int avatarDrawable, int position) {
        selectedAvatar = position;
    }

    @Override
    public void onColorClick(int colorDrawable, int position) {
        selectedNickColor = position;
        colorAdapter.setSelectedColor(position);
    }
    public int getSelectedAvatar(){
        return selectedAvatar;
    }
    public int getSelectedColor() { return selectedNickColor;
    }
}