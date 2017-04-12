package com.damonlei.vimdroid;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.RemoteException;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/10
 * @email danxionglei@foxmail.com
 */
class DeviceControllerService extends IDeviceController.Stub {

    private UiAutomation uiAutomation;

    private Instrumentation instrumentation;

    private UiDevice uiDevice;

    private Rect cacheRect;

    private int displayWidth, displayHeight;

    DeviceControllerService(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.uiAutomation = instrumentation.getUiAutomation();
        this.cacheRect = new Rect();
        this.uiDevice = UiDevice.getInstance(instrumentation);
        this.displayWidth = uiDevice.getDisplayWidth();
        this.displayHeight = uiDevice.getDisplayHeight();
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return uiAutomation.getRootInActiveWindow();
    }

    @Override
    public List<AccessibilityNodeInfo> getClickableNodes(boolean bypassCache, boolean onlyVisible) throws RemoteException {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        return filterClickableNode(root, onlyVisible);
    }

    private List<AccessibilityNodeInfo> filterClickableNode(AccessibilityNodeInfo root, boolean onlyVisible) {
        if (root == null) {
            Timber.e("filterClickableNode(60) get not root.");
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }
        Queue<AccessibilityNodeInfo> queue = new LinkedList<>();
        LinkedList<AccessibilityNodeInfo> result = new LinkedList<>();
        int count;
        AccessibilityNodeInfo node;
        queue.offer(root);
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (node == null) {
                continue;
            }
            if (node.isClickable() && (!onlyVisible || isVisible(node))) {
                result.offer(node);
            }
            count = node.getChildCount();
            for (int i = 0; i < count; i++) {
                queue.offer(node.getChild(i));
            }
        }
        Timber.d("Finally, we get [%d] clickable nodes.", result.size());
        return result;
    }

    private boolean isVisible(AccessibilityNodeInfo info) {
        if (info == null) {
            return false;
        }
        info.getBoundsInScreen(cacheRect);
        return cacheRect.intersect(0, 0, displayWidth, displayHeight);
    }

    @Override
    public boolean performGlobalAction(int action) {
        return uiAutomation.performGlobalAction(action);
    }

    @Override
    public boolean click(int x, int y) {
        return uiDevice.click(x, y);
    }

    @Override
    public boolean isScreenOn() throws RemoteException {
        return uiDevice.isScreenOn();
    }

    @Override
    public boolean openNotification() {
        return uiDevice.openNotification();
    }

    @Override
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        return uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    @Override
    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    @Override
    public void wakeUp() throws RemoteException {
        uiDevice.wakeUp();
    }

    @Override
    public void sleep() throws RemoteException {
        uiDevice.sleep();
    }

    @Override
    public boolean pressRecentApps() throws RemoteException {
        return uiDevice.pressRecentApps();
    }

    @Override
    public boolean pressKeyCode(int keyCode) {
        return uiDevice.pressKeyCode(keyCode);
    }

    public int getScreenWidth() {
        return displayWidth;
    }

    public int getScreenHeight() {
        return displayHeight;
    }

}
