package com.damonlei.vimdroid.connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * @author damonlei
 * @time 2017/3/3
 * @email danxionglei@foxmail.com
 */
public class Server {
    public static final int SERVER_PORT = 10086;

    private AcceptConnectionThread acceptConnectionThread;

    private DataReceivedListener listener;

    public boolean isConnected() {
        return acceptConnectionThread != null && acceptConnectionThread.isConnected();
    }

    public boolean isAlive() {
        return acceptConnectionThread.isServerAlive();
    }

    // construct a thread to accept connection in loop.
    public void connect(int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.setReuseAddress(true);
        server.bind(new InetSocketAddress(port));
        acceptConnectionThread = new AcceptConnectionThread(server, listener);
        acceptConnectionThread.start();
    }

    public void shutdown() {
        if (acceptConnectionThread != null) {
            acceptConnectionThread.stopAccept();
        }
        listener = null;
    }

    public void send(String data) {
        // not impl
    }

    public void setReceiveListener(DataReceivedListener listener) {
        this.listener = listener;
    }

    public interface DataReceivedListener {
        String receive(int cmdId, String data);
    }
}
