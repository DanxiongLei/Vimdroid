package com.damonlei.vimdroid.connect;

import com.damonlei.utils.IOUtil;
import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.command.base.Resp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/3
 * @email danxionglei@foxmail.com
 */
public class IOSocketThreadRunnable implements Runnable {

    private static final String TAG = "connect.IOSocketHandlerThread";

    private Socket socket;

    private boolean stop;

    private Server.DataReceivedListener listener;

    public IOSocketThreadRunnable(Socket socket, Server.DataReceivedListener listener) {
        this.socket = socket;
        this.listener = listener;
    }


    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = IOUtil.bufferReader(socket.getInputStream());
            writer = IOUtil.bufferedWriter(socket.getOutputStream());
            do {
                int cmdId;
                String data;
                try {
                    cmdId = Utils.toInt(reader.readLine());
                    data = reader.readLine();
                } catch (IOException e) {
                    Timber.e(e, "run(53) IOException");
                    throw e;
                } catch (Exception e) {
                    Timber.e(e, "run(52) params error.");
                    writer.write(Resp.FAILURE_RESP.toString() + "\n");
                    writer.close();
                    break;
                }
                String resp = null;
                try {
                    resp = listener.receive(cmdId, data);
                } catch (Exception e) {
                    Timber.e(e, "run(68) UnexpectedException e [%s]");
                    writer.write(Resp.FAILURE_RESP.toString() + "\n");
                    writer.close();
                    break;
                }
                writer.write(resp + "\n");
                writer.close();
            } while (false);
        } catch (IOException e) {
            Timber.e(e, "run(53) Got IOException %s");
        } catch (Exception e) {
            Timber.e(e, "run(55) Got Exception %s");
        } finally {
            IOUtil.closeSilently(reader);
            IOUtil.closeSilently(writer);
            try {
                socket.close();
            } catch (Exception ignored) {

            }
        }
    }

    public void stopIO() {
        this.stop = true;
        try {
            socket.close();
        } catch (Exception e) {
            Timber.e("stopIO(49) stop socket error...%s", e);
        }
    }
}
