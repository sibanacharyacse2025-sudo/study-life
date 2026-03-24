package com.stdili.network;

import com.stdili.BuildConfig;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(BuildConfig.BACKEND_API_BASE_URL);
            } catch (Exception ignored) {
            }
        }
        return socket;
    }

    public void connect() {
        if (getSocket() != null && !socket.connected()) {
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
}
