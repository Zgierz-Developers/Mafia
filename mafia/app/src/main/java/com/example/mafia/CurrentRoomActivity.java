package com.example.mafia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrentRoomActivity extends AppCompatActivity {

    private TextView roomDetailsTextView;
    private EditText messageEditText;
    private Button sendButton;
    private Button leaveButton;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String roomName;
    private String nickname;
    private Socket socket;
    private Integer clientProfileLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_room);



        roomDetailsTextView = findViewById(R.id.roomDetailsTextView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        leaveButton = findViewById(R.id.leaveButton);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        roomName = getIntent().getStringExtra("roomName");
        nickname = getIntent().getStringExtra("nickname");
        roomDetailsTextView.setText("Room: " + roomName);

        clientProfileLogo = getIntent().getIntExtra("selectedAvatar", -1);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        socket = SocketManager.getSocket();
        socket.on(Socket.EVENT_CONNECT, args -> {
            JSONObject joinData = new JSONObject();
            try {
                joinData.put("room", roomName);
                joinData.put("username", nickname);
                socket.emit("joinRoom", joinData);
                Log.d("CurrentRoomActivity", "Joined room: " + roomName);
            } catch (JSONException e) {
                Log.e("CurrentRoomActivity", "JSON error", e);
            }
        });
        socket.on("message", this::onMessage);
        Log.d("CurrentRoomActivity", "Socket connected and listener registered");

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            sendMessage(message);
        });

        leaveButton.setOnClickListener(v -> {
            if (socket != null) {
                JSONObject leaveData = new JSONObject();
                try {
                    leaveData.put("roomName", roomName);
                    leaveData.put("username", nickname);
                    socket.emit("leaveRoom", leaveData);
                    Log.d("CurrentRoomActivity", "Left room: " + roomName);
                } catch (JSONException e) {
                    Log.e("CurrentRoomActivity", "JSON error", e);
                }
                socket.off("message", this::onMessage);
            }
            finish();
        });
    }



    private void sendMessage(String message) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", nickname);
            data.put("message", message);
            data.put("gameCode", roomName);
            data.put("selectedAvatar", clientProfileLogo);
            socket.emit("sendMessage", data);
            messageList.add(new Message(nickname, message, clientProfileLogo));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            messagesRecyclerView.scrollToPosition(messageList.size() - 1);
            messageEditText.setText("");
            Log.d("CurrentRoomActivity", "Sent message: " + message + " to " + roomName + " as " + nickname + " with data: " + data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onMessage(Object... args) {
        runOnUiThread(() -> {
            Log.d("CurrentRoomActivity", "onMessage called with args: " + Arrays.toString(args));
            if (args.length > 0 && args[0] instanceof JSONObject) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");
                    Integer clientProfileLogo = data.getInt("clientProfileLogo");
                    Log.d("CurrentRoomActivity", "Received message: " + message + " from " + username);
                    messageList.add(new Message(username, message, clientProfileLogo));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                } catch (JSONException e) {
                    Log.e("CurrentRoomActivity", "JSON parsing error", e);
                }
            } else {
                Log.e("CurrentRoomActivity", "Invalid message data received");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("message", this::onMessage);
    }
}