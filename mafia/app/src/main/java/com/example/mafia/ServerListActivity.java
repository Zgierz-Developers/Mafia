package com.example.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerListActivity extends AppCompatActivity {

    private ListView serverListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> serverList;
    private Button refreshButton, backToMenuButton;
    private Button createRoomButton;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);

        serverListView = findViewById(R.id.serverListView);
        refreshButton = findViewById(R.id.refreshButton);
        createRoomButton = findViewById(R.id.createRoomButton);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        serverList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serverList);
        serverListView.setAdapter(adapter);


        socket = SocketManager.getSocket();
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on("roomList", onRoomList);

        createRoomButton.setOnClickListener(v -> {
            createRoom();
        });

        refreshButton.setOnClickListener(v -> fetchServerList());

        backToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(ServerListActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String roomName = serverList.get(position);
                showNicknameDialog(roomName);
            }
        });
    }

    private void fetchServerList() {
        socket.emit("listRooms");
    }

    private void showNicknameDialog(String roomName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wpisz nazwę gracza");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Join", (dialog, which) -> {
            String nickname = input.getText().toString();
            if (!nickname.isEmpty()) {
                joinRoom(roomName, nickname);
            } else {
                Toast.makeText(ServerListActivity.this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Utwórz pokój");

        // Utwórz layout dla dialogu
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText roomNameInput = new EditText(this);
        roomNameInput.setHint("Nazwa pokoju");
        layout.addView(roomNameInput);

        final EditText nickNameInput = new EditText(this); // Dodaj definicję nickNameInput
        nickNameInput.setHint("Nazwa gracza");
        layout.addView(nickNameInput); // Dodaj nickNameInput do layoutu

        builder.setView(layout); // Ustaw layout jako widok dialogu

        builder.setPositiveButton("Utwórz", (dialog, which) -> {
            String roomName = roomNameInput.getText().toString();
            String nickName = nickNameInput.getText().toString();
            if (!roomName.isEmpty() && !nickName.isEmpty()) { // Sprawdź, czy oba pola są wypełnione
                try {
                    JSONObject data = new JSONObject();
                    data.put("roomName", roomName);
                    data.put("ownerName", nickName);
                    socket.emit("createRoom", data);

                    // Przejdź do CurrentRoomActivity z nazwą pokoju i nickiem
                    Intent intent = new Intent(ServerListActivity.this, CurrentRoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("nickname", nickName);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ServerListActivity.this, "Nazwa pokoju i nazwa gracza nie mogą być puste", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void joinRoom(String roomName, String nickname) {
        try {
            JSONObject data = new JSONObject();
            data.put("roomName", roomName);
            data.put("playerName", nickname);
            socket.emit("joinRoom", data);
            Intent intent = new Intent(ServerListActivity.this, CurrentRoomActivity.class);
            intent.putExtra("roomName", roomName);
            intent.putExtra("nickname", nickname);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onConnect = args -> fetchServerList();

    private final Emitter.Listener onRoomList = args -> runOnUiThread(() -> {
        serverList.clear();
        JSONObject rooms = (JSONObject) args[0];
        for (Iterator<String> it = rooms.keys(); it.hasNext(); ) {
            String roomName = it.next();
            serverList.add(roomName);
        }
        adapter.notifyDataSetChanged();
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off("roomList", onRoomList);
    }
}