package com.damonlei.vimdroid.utils.permission;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.damonlei.utils.Utils;
import com.damonlei.utils.ui.BaseActivity;
import com.damonlei.vimdroid.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/4/10
 * @email danxionglei@foxmail.com
 */
public class PermissionActivity extends BaseActivity {

    private static String[] needRequestNormalPermission;

    private static FutureTask<Boolean> future;

    private static boolean result;

    private static boolean needRequestAlertWindow;

    private static boolean goingSettings;

    public static Future<Boolean> getPermission(Context context, String... permissions) {
        needRequestNormalPermission = lackPermission(context, permissions);
        if (Utils.nullOrNil(needRequestNormalPermission) && !needRequestAlertWindow) {
            return null;
        }
        future = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return result;
            }
        });
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return future;
    }

    public static boolean lackPermission(Context context, String permission) {
        if (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return !FloatWindowPermissionUtils.checkHasPermission(context);
        }
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static String[] lackPermission(Context context, String... permissions) {
        LinkedList<String> lacks = new LinkedList<>();
        for (String permission : permissions) {
            if (lackPermission(context, permission)) {
                if (android.Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
                    needRequestAlertWindow = true;
                } else if (android.Manifest.permission.WRITE_SETTINGS.equals(permission)) {
                    // not impl
                } else {
                    lacks.add(permission);
                }
            }
        }
        String[] result = new String[lacks.size()];
        lacks.toArray(result);
        return result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!Utils.nullOrNil(needRequestNormalPermission)) {
            ActivityCompat.requestPermissions(this, needRequestNormalPermission, 1);
        } else {
            requestAlertWindow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRequestAlertWindow && goingSettings) {
            if (lackPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                grantPermissionFailed();
            } else {
                grantPermissionSucceed();
            }
        }
    }

    private void requestAlertWindow() {
        Timber.d("requestAlertWindow() called");
        new AlertDialog.Builder(this).
                setMessage(R.string.please_grant_alert_window_permission).
                setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goingSettings = true;
                        FloatWindowPermissionUtils.applyPermission(PermissionActivity.this);
                    }
                }).setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        grantPermissionFailed();
                    }
                }).show();
    }

    private void grantPermissionFailed() {
        result = false;
        future.run();
        finish();
    }

    private void grantPermissionSucceed() {
        result = true;
        future.run();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Timber.d("onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + Arrays.toString(permissions) + "], grantResults = [" + Arrays.toString(grantResults) + "]");
        if (requestCode == 1) {
            boolean hit = false;
            for (String permission : needRequestNormalPermission) {
                if (lackPermission(this, permission)) {
                    hit = true;
                    break;
                }
            }
            if (hit) {
                grantPermissionFailed();
                return;
            }
            if (needRequestAlertWindow) {
                requestAlertWindow();
            } else {
                grantPermissionSucceed();
            }
        }
    }
}
