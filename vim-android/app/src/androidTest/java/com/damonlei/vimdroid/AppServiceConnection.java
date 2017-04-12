package com.damonlei.vimdroid;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
class AppServiceConnection implements ServiceConnection {

    private DeviceControllerService windowInspectorService;
    private Looper looper;

    AppServiceConnection(Looper looper, DeviceControllerService service) {
        this.looper = looper;
        this.windowInspectorService = service;
    }

    // 非发起Connect的线程, 应该是Binder线程池
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        IDeviceControllerConnection asInterface = IDeviceControllerConnection.Stub.asInterface(service);
        try {
            asInterface.bindInspector(windowInspectorService);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        closeController();
    }

    private void closeController() {
        if (looper != null) {
            looper.quitSafely();
        }
    }
}
