package com.damonlei.vimdroid.connect;

import com.damonlei.vimdroid.utils.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/3
 * @email danxionglei@foxmail.com
 */
public class AcceptConnectionThread extends Thread {

    private ServerSocket serverSocket;

    private IOSocketThreadRunnable socketThreadRunnable;

    private boolean stop = false;

    private Server.DataReceivedListener listener;

    public AcceptConnectionThread(ServerSocket serverSocket, Server.DataReceivedListener listener) {
        this.serverSocket = serverSocket;
        this.listener = listener;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean isConnected() {
        return serverSocket != null
                && !serverSocket.isClosed()
                && socketThreadRunnable != null
                && socketThreadRunnable.getSocket() != null
                && socketThreadRunnable.getSocket().isConnected();
    }

    public boolean isServerAlive() {
        return serverSocket != null && !serverSocket.isClosed() && !stop;
    }

    @Override
    public void run() {
        Timber.d("Thread(%d:%s) is running.", getId(), getName());
        if (stop) {
            Timber.d("Thread finished immediately.");
            return;
        }
        try {
            while (!stop) {
                Socket socket = serverSocket.accept();
                Timber.d("Connection accept...");
                socket.setTcpNoDelay(true);
//                socket.setSoTimeout(1000);
                socketThreadRunnable = new IOSocketThreadRunnable(socket, listener);
                Timber.d("Start to communicate.");
                ThreadPool.post(socketThreadRunnable);
            }
        } catch (IOException e) {
            Timber.e("Got Exception %s", e);
        } finally {
            try {
                socketThreadRunnable.stopIO();
            } catch (Exception ignored) {

            }
            try {
                serverSocket.close();
            } catch (Exception ignored) {

            }
        }
    }

    public void stopAccept() {
        stop = true;
        if (socketThreadRunnable != null) {
            socketThreadRunnable.stopIO();
            socketThreadRunnable = null;
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception ignored) {

            }
        }
    }

}
