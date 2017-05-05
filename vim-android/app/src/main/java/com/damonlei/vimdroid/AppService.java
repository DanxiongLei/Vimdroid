package com.damonlei.vimdroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.damonlei.vimdroid.command.EnsurePrepareExecutor;
import com.damonlei.vimdroid.command.PingExecutor;
import com.damonlei.vimdroid.command.ShutdownExecutor;
import com.damonlei.vimdroid.command.base.CommandDispatcher;
import com.damonlei.vimdroid.connect.Server;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.device.WindowRoot;
import com.damonlei.vimdroid.keyBoard.KeyBoardCommandExecutor;
import com.damonlei.vimdroid.utils.ThreadPool;

import java.io.IOException;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
public class AppService extends Service {

    private CommandDispatcher dispatcher;

    private Server server;

    @Override
    public void onCreate() {
        if (server == null || !server.isAlive()) {
            init();
        }
    }

    private void init() {
        // init ViewRoot to be attached on window
        WindowRoot.init(getApplication());
        WindowRoot.getInstance().attachOnWindow();
        // init CommandDispatcher to handle request
        dispatcher = new CommandDispatcher(this);
        initCommandExecutor(dispatcher);
        // init Server to receive command.
        server = new Server();
        server.setReceiveListener(dispatcher);
        try {
            server.connect(Server.SERVER_PORT);
        } catch (IOException e) {
            Timber.e(e, "init(73)");
        }
    }

    private void initCommandExecutor(CommandDispatcher dispatcher) {
        dispatcher.clear();
        EnsurePrepareExecutor ensurePrepareExecutor = new EnsurePrepareExecutor(this);
        dispatcher.register(ensurePrepareExecutor);
        dispatcher.register(new PingExecutor());
        dispatcher.register(new KeyBoardCommandExecutor(ensurePrepareExecutor));
        dispatcher.register(new ShutdownExecutor(this));
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        Timber.d("onDestroy() called");
        WindowRoot.release();
        if (dispatcher != null) {
            dispatcher.clear();
            dispatcher = null;
        }
        if (server != null) {
            server.shutdown();
            server = null;
        }
        ThreadPool.destroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IDeviceControllerConnection.Stub mBinder = new IDeviceControllerConnection.Stub() {

        @Override
        public void bindInspector(IDeviceController proxy) throws RemoteException {
            if (proxy == null) {
                Timber.e("bindInspector(102) Proxy is null.");
            }
            Timber.d("Proxy is transferred to AppService:remote.");
            DeviceController.setInstance(proxy, getApplicationContext());
        }

    };

}
