// SocketManager.java
package com.example.mafia;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class SocketManager {
    private static Socket socket;

    public static Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket("https://mafia-3zvq.onrender.com");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }
}