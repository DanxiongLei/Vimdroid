package com.damonlei.vimdroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.damonlei.utils.Utils;
import com.damonlei.utils.ui.BaseActivity;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author damonlei
 * @time 2017/4/10
 * @email danxionglei@foxmail.com
 */
public class PermissionActivity extends BaseActivity {

    private static String[] needRequestPermission;

    private static FutureTask<Boolean> future;

    private static boolean result;

    public static Future<Boolean> getPermission(Context context, String... permissions) {
        needRequestPermission = lackPermission(context, permissions);
        if (Utils.nullOrNil(needRequestPermission)) {
            return null;
        }
        future = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return result;
            }
        });

        context.startActivity(new Intent(context, PermissionActivity.class));
        return future;
    }


    public static boolean lackPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }


    public static String[] lackPermission(Context context, String... permissions) {
        LinkedList<String> lacks = new LinkedList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                lacks.add(permission);
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
    protected void onResume() {
        super.onResume();
        ActivityCompat.requestPermissions(this, needRequestPermission, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode != Activity.RESULT_OK) {
                result = false;
                future.run();
            } else {
                boolean hit = false;
                for (String permission : needRequestPermission) {
                    if (lackPermission(this, permission)) {
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    result = false;
                    future.run();
                } else {
                    result = true;
                    future.run();
                }
            }
        }
    }
}
