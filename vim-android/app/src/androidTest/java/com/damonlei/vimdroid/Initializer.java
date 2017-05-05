package com.damonlei.vimdroid;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityEvent;

import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Initializer {

    @Test
    public void testAccessibility() throws Exception {
        final UiAutomation automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        automation.setOnAccessibilityEventListener(new UiAutomation.OnAccessibilityEventListener() {
            @Override
            public void onAccessibilityEvent(AccessibilityEvent event) {
                String actName = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).getCurrentActivityName();
                int type = event.getEventType();
                if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                    Timber.e("onAccessibilityEvent(63) type = TYPE_WINDOW_CONTENT_CHANGED , [%s], [%s]", event.getSource().getClassName(), actName);
                } else if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    Timber.e("onAccessibilityEvent(63) type = TYPE_WINDOW_STATE_CHANGED, [%s], [%s]", event.getSource().getClassName(), actName);
                } else if (type == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
                    Timber.e("onAccessibilityEvent(63) type = TYPE_WINDOWS_CHANGED");
                } else {
                }
            }
        });
        Looper.prepare();
        Looper.loop();
    }

    @Test
    public void startup() throws Exception {
        // Context of the app under test.
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        DeviceControllerService windowInspectorService = new DeviceControllerService(instrumentation);
        Looper.prepare();
        Intent intent = new Intent(context, AppService.class);
        context.startService(intent);
        AppServiceConnection conn = new AppServiceConnection(Looper.myLooper(), windowInspectorService);
        context.bindService(intent, conn, Context.BIND_ABOVE_CLIENT);
        Looper.loop();
        context.unbindService(conn);
        context.stopService(intent);
    }
}
