package com.example.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);

        serverListView = findViewById(R.id.serverListView);
        refreshButton = findViewById(R.id.refreshButton);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        serverList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serverList);
        serverListView.setAdapter(adapter);

        try {
            socket = IO.socket("https://mafia-3zvq.onrender.com");
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on("roomList", onRoomList);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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
        builder.setTitle("Enter Nickname");

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

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            fetchServerList();
        }
    };

    private Emitter.Listener onRoomList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                serverList.clear();
                JSONObject rooms = (JSONObject) args[0];
                for (Iterator<String> it = rooms.keys(); it.hasNext(); ) {
                    String roomName = it.next();
                    serverList.add(roomName);
                }
                adapter.notifyDataSetChanged();
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off("roomList", onRoomList);
    }
}