package com.example.mafia;

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
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CurrentRoomActivity extends AppCompatActivity {

    private TextView roomDetailsTextView;
    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String roomName;
    private String nickname;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_room);

        roomDetailsTextView = findViewById(R.id.roomDetailsTextView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        roomName = getIntent().getStringExtra("roomName");
        nickname = getIntent().getStringExtra("nickname");
        roomDetailsTextView.setText("Room: " + roomName);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        try {
            socket = IO.socket("https://mafia-3zvq.onrender.com");
            socket.connect();
            socket.on("message", onMessage);
            Log.d("CurrentRoomActivity", "Socket connected and listener registered");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            sendMessage(message);
        });
    }



    private void sendMessage(String message) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", nickname);
            data.put("message", message);
            data.put("gameCode", roomName);
            socket.emit("sendMessage", data);
            messageEditText.setText("");
            Log.d("CurrentRoomActivity", "Sent message: " + message + " to " + roomName + " as " + nickname + " with data: " + data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");
                    Log.d("CurrentRoomActivity", "Received message: " + message + " from " + username);
                    messageList.add(new Message(username, message));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("message", onMessage);
    }
}