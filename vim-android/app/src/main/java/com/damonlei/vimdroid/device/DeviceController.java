package com.damonlei.vimdroid.device;


import android.content.Context;
import android.os.RemoteException;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.vimdroid.IDeviceController;

import java.util.List;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
public class DeviceController {

    private static DeviceController INSTANCE;

    public static void setInstance(IDeviceController controller, Context context) {
        INSTANCE = new DeviceController(controller, context);
    }

    public static DeviceController getInstance() {
        return INSTANCE;
    }

    public static boolean isInit() {
        return INSTANCE != null;
    }

    private IDeviceController mRemoteController;

    private int mDisplayWidth, mDisplayHeight;

    private DeviceController(IDeviceController proxy, Context context) {
        mRemoteController = proxy;
        mDisplayWidth = ResourceHelper.getWidthPixels(context);
        mDisplayHeight = ResourceHelper.getHeightPixels(context);
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        try {
            return mRemoteController.getRootInActiveWindow();
        } catch (RemoteException e) {
            Timber.e(e, "getRootInActiveWindow()");
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否强行update新的window
     */
    public List<AccessibilityNodeInfo> getClickableNodes() {
        try {
            return mRemoteController.getClickableNodes(false, true);
        } catch (RemoteException e) {
            Timber.e("getClickableNodes(69) ");
            throw new RuntimeException(e);
        }
    }

    public boolean openNotification() {
        try {
            return mRemoteController.openNotification();
        } catch (RemoteException e) {
            Timber.e("openNotification()");
            throw new RuntimeException(e);
        }
    }

    public boolean performGlobalAction(int action) {
        try {
            return mRemoteController.performGlobalAction(action);
        } catch (RemoteException e) {
            Timber.e("performGlobalAction(60)");
            throw new RuntimeException(e);
        }

    }

    public boolean click(int x, int y) {
        try {
            return mRemoteController.click(x, y);
        } catch (RemoteException e) {
            Timber.e("click(70)");
            throw new RuntimeException(e);
        }
    }

    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        try {
            return mRemoteController.swipe(startX, startY, endX, endY, steps);
        } catch (RemoteException e) {
            Timber.e("swipe(79) ");
            throw new RuntimeException(e);
        }
    }

    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        try {
            return mRemoteController.drag(startX, startY, endX, endY, steps);
        } catch (RemoteException e) {
            Timber.e("drag(88)");
            throw new RuntimeException(e);
        }
    }

    public boolean isScreenOn() {
        try {
            return mRemoteController.isScreenOn();
        } catch (RemoteException e) {
            Timber.e("isScreenOn(97) ");
            throw new RuntimeException(e);
        }
    }

    public void wakeUp() {
        try {
            mRemoteController.wakeUp();
        } catch (RemoteException e) {
            Timber.e("wakeUp(106) ");
            throw new RuntimeException(e);
        }
    }

    public void sleep() {
        try {
            mRemoteController.sleep();
        } catch (RemoteException e) {
            Timber.e("sleep(115) ");
            throw new RuntimeException(e);
        }
    }

    public boolean pressRecentApps() {
        try {
            return mRemoteController.pressRecentApps();
        } catch (RemoteException e) {
            Timber.e("pressRecentApps(124) ");
            throw new RuntimeException(e);
        }
    }

    public boolean pressKeyCode(int keyCode) {
        try {
            return mRemoteController.pressKeyCode(keyCode);
        } catch (RemoteException e) {
            Timber.e("pressKeyCode(133) ");
            throw new RuntimeException(e);
        }
    }

}
