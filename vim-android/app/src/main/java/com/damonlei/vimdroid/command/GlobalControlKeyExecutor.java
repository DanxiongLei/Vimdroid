package com.damonlei.vimdroid.command;

import android.os.Looper;

import com.damonlei.vimdroid.command.base.CommandExecuteException;
import com.damonlei.vimdroid.command.base.CommandExecutorBase;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.keyBoard.KeyRequest;

import timber.log.Timber;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS;
import static com.damonlei.vimdroid.keyBoard.KeyCode.ESC;
import static com.damonlei.vimdroid.keyBoard.KeyCode.H;
import static com.damonlei.vimdroid.keyBoard.KeyCode.R;

/**
 * @author damonlei
 * @time 2017/4/11
 * @email danxionglei@foxmail.com
 */
public class GlobalControlKeyExecutor extends CommandExecutorBase<KeyRequest, Resp> {

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Looper getLooper() {
        return Looper.getMainLooper();
    }

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        Timber.d("ReturnBack.execute() called with: data = [" + data + "]");
        if (!DeviceController.isInit()) {
            throw new CommandExecuteException("Window Inspector have not been prepared.");
        }
        int globalAction;
        if (data.name == ESC) {
            globalAction = GLOBAL_ACTION_BACK;
        } else if (data.shift && data.name == H) {
            globalAction = GLOBAL_ACTION_HOME;
        } else if (data.shift && data.name == R) {
            globalAction = GLOBAL_ACTION_RECENTS;
        } else {
            throw new IllegalStateException("GlobalControlKey not support with key " + data);
        }
        boolean result = DeviceController.getInstance().performGlobalAction(globalAction);
        if (result) {
            return Resp.SUCCESS_RESP;
        } else {
            return Resp.FAILURE_RESP;
        }
    }
}
