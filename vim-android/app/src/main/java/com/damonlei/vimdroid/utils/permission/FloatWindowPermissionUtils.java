package com.damonlei.vimdroid.utils.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.damonlei.vimdroid.utils.permission.rom.HuaweiUtils;
import com.damonlei.vimdroid.utils.permission.rom.MeizuUtils;
import com.damonlei.vimdroid.utils.permission.rom.MiuiUtils;
import com.damonlei.vimdroid.utils.permission.rom.QikuUtils;
import com.damonlei.vimdroid.utils.permission.rom.RomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author damonlei
 * @time 2017/4/13
 * @email danxionglei@foxmail.com
 */
public class FloatWindowPermissionUtils {

    private static final String TAG = "WindowPermissionUtils";

    public static boolean checkHasPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return MiuiUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return MeizuUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return HuaweiUtils.checkFloatWindowPermission(context);
            } else if (RomUtils.checkIs360Rom()) {
                return QikuUtils.checkFloatWindowPermission(context);
            }
        }

        return commonROMPermissionCheck(context);
    }

    private static boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtils.checkIsMeizuRom()) {
            return MeizuUtils.checkFloatWindowPermission(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    //noinspection unchecked
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            return result;
        }
    }

    public static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                MiuiUtils.applyMiuiPermission(context);
                return;
            } else if (RomUtils.checkIsMeizuRom()) {
                MeizuUtils.applyPermission(context);
                return;
            } else if (RomUtils.checkIsHuaweiRom()) {
                HuaweiUtils.applyPermission(context);
                return;
            } else if (RomUtils.checkIs360Rom()) {
                QikuUtils.applyPermission(context);
                return;
            }
        }
        commonROMPermissionApply(context);
    }


    /**
     * 通用 rom 权限申请
     */
    private static void commonROMPermissionApply(final Context context) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.checkIsMeizuRom()) {
            MeizuUtils.applyPermission(context);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                    Intent intent = new Intent(field.get(null).toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

}
