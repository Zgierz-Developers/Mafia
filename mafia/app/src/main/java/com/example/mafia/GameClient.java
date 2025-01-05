package com.example.mafia;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameClient {

    private static final String TAG = "GameClient";
    private final String serverUrl = "https://mafia-server-sigma.vercel.app/socket.io/";
    private final String gameCode;
    private Socket socket;
    private OnMessageReceivedListener messageListener;
    private OnPlayerJoinedListener playerJoinedListener;
    private boolean isConnected = false;

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

    public interface OnPlayerJoinedListener {
        void onPlayerJoined();
    }

    public GameClient(String gameCode) {
        this.gameCode = gameCode;
    }

    public void connect() {
        if (isConnected) {
            Log.d(TAG, "Already connected to server");
            return;
        }

        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            socket = IO.socket(serverUrl, options);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Connected to server");
                    isConnected = true;
                    joinGame();
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "ERRCONN Connection error: " + args[0]);
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Disconnected from server");
                    isConnected = false;
                }
            });

            socket.on("playerJoined", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Player joined");
                    if (playerJoinedListener != null) {
                        playerJoinedListener.onPlayerJoined();
                    }
                }
            });

            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String message = data.getString("message");
                        Log.d(TAG, "Received message: " + message);
                        if (messageListener != null) {
                            messageListener.onMessageReceived(message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing message JSON: " + e.getMessage());
                    }
                }
            });

        } catch (URISyntaxException e) {
            Log.e(TAG, "Error connecting to server: " + e.getMessage());
        }
    }

    private void joinGame() {
        socket.emit("joinGame", gameCode);
    }

    public void sendMessage(String message) {
        JSONObject data = new JSONObject();
        try {
            data.put("gameCode", gameCode);
            data.put("message", message);
            socket.emit("sendMessage", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating message JSON: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            isConnected = false;
        }
    }

    public void setMessageListener(OnMessageReceivedListener listener) {
        this.messageListener = listener;
    }

    public void setPlayerJoinedListener(OnPlayerJoinedListener listener) {
        this.playerJoinedListener = listener;
    }
}