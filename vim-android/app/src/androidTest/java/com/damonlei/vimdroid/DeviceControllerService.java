package com.damonlei.vimdroid;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.RemoteException;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/10
 * @email danxionglei@foxmail.com
 */
class DeviceControllerService extends IDeviceController.Stub implements UiAutomation.OnAccessibilityEventListener {

    private UiAutomation uiAutomation;

    private Instrumentation instrumentation;

    private UiDevice uiDevice;

    private Rect cacheRect;

    private int displayWidth, displayHeight;

    private long updateTime;

    private final Object updateTimeLock = new Object();

//    private AccessibilityNodeInfo rootCache;

    private Filter FILTER_CLICKABLE = new Filter() {
        @Override
        public boolean filter(AccessibilityNodeInfo node) {
            return node != null && node.isClickable();
        }
    };

    private Filter FILTER_SCROLLABLE = new Filter() {
        @Override
        public boolean filter(AccessibilityNodeInfo node) {
            return node != null && node.isScrollable();
        }
    };

    private Filter FILTER_EDITABLE = new Filter() {
        @Override
        public boolean filter(AccessibilityNodeInfo node) {
            return node != null && node.isEditable();
        }
    };

    DeviceControllerService(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.uiAutomation = instrumentation.getUiAutomation();
        this.cacheRect = new Rect();
        this.uiDevice = UiDevice.getInstance(instrumentation);
        this.displayWidth = uiDevice.getDisplayWidth();
        this.displayHeight = uiDevice.getDisplayHeight();
//        this.uiAutomation.setOnAccessibilityEventListener(this);
//        synchronized (updateTimeLock) {
//            updateTime = System.currentTimeMillis();
//        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Timber.d("onAccessibilityEvent(63) type = TYPE_WINDOW_CONTENT_CHANGED");
        } else if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Timber.d("onAccessibilityEvent(63) type = TYPE_WINDOW_STATE_CHANGED");
        } else if (type == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            Timber.d("onAccessibilityEvent(63) type = TYPE_WINDOWS_CHANGED");
        } else {
            return;
        }
//        synchronized (updateTimeLock) {
//            updateTime = System.currentTimeMillis();
//        }
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        AccessibilityNodeInfo root = uiAutomation.getRootInActiveWindow();
//        this.rootCache = root;
        return root;
    }

//    private AccessibilityNodeInfo getRootInActiveWindow(boolean bypassCache) {
//        if (bypassCache) {
//            return getRootInActiveWindow();
//        } else {
//            return this.rootCache;
//        }
//    }

    @Override
    public List<AccessibilityNodeInfo> getClickableNodes(boolean bypassCache, boolean onlyVisible) throws RemoteException {
        AccessibilityNodeInfo root = this.getRootInActiveWindow();
        return mergeCoincide(filterNodesTree(root, onlyVisible, FILTER_CLICKABLE));
    }

    @Override
    public List<AccessibilityNodeInfo> getScrollableNodes(boolean bypassCache, boolean onlyVisible) throws RemoteException {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        return mergeCoincide(filterNodesTree(root, onlyVisible, FILTER_SCROLLABLE));
    }

    @Override
    public List<AccessibilityNodeInfo> getEditableNodes(boolean bypassCache, boolean onlyVisible) throws RemoteException {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        return mergeCoincide(filterNodesTree(root, onlyVisible, FILTER_EDITABLE));
    }

    private List<AccessibilityNodeInfo> mergeCoincide(List<AccessibilityNodeInfo> accessibilityNodeList) {
        if (accessibilityNodeList == null || accessibilityNodeList.size() <= 1) {
            return accessibilityNodeList;
        }
        HashSet<String> set = new HashSet<>();
        for (int i = accessibilityNodeList.size() - 1;  i>=0; i--) {
            AccessibilityNodeInfo info = accessibilityNodeList.get(i);
            info.getBoundsInScreen(cacheRect);
            String key = cacheRect.flattenToString();
            if (set.contains(key)) {
                accessibilityNodeList.remove(i);
            } else {
                set.add(key);
            }
        }
        return accessibilityNodeList;
    }

    private interface Filter {
        public boolean filter(AccessibilityNodeInfo node);
    }

    private List<AccessibilityNodeInfo> filterNodesTree(AccessibilityNodeInfo root, boolean onlyVisible, Filter filter) {
        if (root == null) {
            Timber.e("filterNode(60) get not root.");
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }
        Queue<AccessibilityNodeInfo> queue = new LinkedList<>();
        ArrayList<AccessibilityNodeInfo> result = new ArrayList<>();
        int count;
        AccessibilityNodeInfo node;
        queue.offer(root);
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (node == null) {
                continue;
            }
            if (filter.filter(node) && (!onlyVisible || isVisible(node))) {
                result.add(node);
            }
            count = node.getChildCount();
            for (int i = 0; i < count; i++) {
                queue.offer(node.getChild(i));
            }
        }
        Timber.d("Finally, we get [%d] nodes.", result.size());
        return result;
    }

    private boolean isVisible(AccessibilityNodeInfo info) {
        if (info == null) {
            return false;
        }
        if (!info.isVisibleToUser()) {
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
