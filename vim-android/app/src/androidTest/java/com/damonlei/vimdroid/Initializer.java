package com.damonlei.vimdroid;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
    public void test() throws Exception {
        System.out.println("test is it running.");
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
        Timber.d("device controller end");
    }
}
