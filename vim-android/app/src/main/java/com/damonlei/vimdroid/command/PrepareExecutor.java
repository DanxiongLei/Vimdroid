package com.damonlei.vimdroid.command;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.R;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.utils.PermissionActivity;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 检查是否准备好，以及申请权限。
 *
 * @author damonlei
 * @time 2017/4/10
 * @email danxionglei@foxmail.com
 */
public class PrepareExecutor implements ICommandExecutor<Object, Resp> {

    private Service context;

    private AtomicBoolean isUsed = new AtomicBoolean(false);

    public PrepareExecutor(Service context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return Global.CMD_ID_PREPARED;
    }

    @Override
    public Looper getLooper() {
        return null;
    }

    @Override
    public Resp execute(Object data) throws Exception {
        if (isUsed.getAndSet(true)) {
            return new Resp(Global.RESP_FAILURE_NOT_FATAL, ResourceHelper.getString(context, R.string.please_wait));
        }
        // 查看是否有WindowInspector辅助
        if (!DeviceController.isInit()) {
            return Resp.failure(ResourceHelper.getString(context, R.string.error_window_inspector_not_prepared));
        }
        // 查看是否有悬浮窗权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
            Future<Boolean> future = PermissionActivity.getPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (future != null) {
                boolean isSucceedToGetAllPermission = future.get();
                if (!isSucceedToGetAllPermission) {
                    return Resp.failure(ResourceHelper.getString(context, R.string.error_no_window_attach_permission));
                }
            }
        }
        return Resp.SUCCESS_RESP;
    }
}
