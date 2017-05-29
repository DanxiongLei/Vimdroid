// IWindowInspectorProxy.aidl
package com.damonlei.vimdroid;

import android.view.accessibility.AccessibilityNodeInfo;

interface IDeviceController {

    AccessibilityNodeInfo getRootInActiveWindow();

    List<AccessibilityNodeInfo> getClickableNodes(boolean bypassCache, boolean onlyVisible);

    List<AccessibilityNodeInfo> getScrollableNodes(boolean bypassCache, boolean onlyVisible);

    List<AccessibilityNodeInfo> getEditableNodes(boolean bypassCache, boolean onlyVisible);

    boolean openNotification();

    boolean performGlobalAction(int action);

    boolean click(int x, int y);

    boolean swipe(int startX, int startY, int endX, int endY, int steps);

    boolean drag(int startX, int startY, int endX, int endY, int steps);

    boolean isScreenOn();

    void wakeUp();

    void sleep();

    boolean pressRecentApps();

    boolean pressKeyCode(int keyCode);
}

