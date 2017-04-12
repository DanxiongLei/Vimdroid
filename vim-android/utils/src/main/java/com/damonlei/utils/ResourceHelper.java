package com.damonlei.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * @author damonlei
 * @time 2016/12/20
 * @email danxionglei@foxmail.com
 */
public class ResourceHelper {

    private static float sDensity = -1f;

    public static int fromDPToPix(Context context, float dp) {
        return Math.round(getDensity(context) * dp);
    }

    public static float fromPixToDP(Context context, int px) {
        return px / getDensity(context);
    }

    public static float getDensity(Context context) {
        if (sDensity < 0) {
            sDensity = context.getResources().getDisplayMetrics().density;
        }
        return sDensity;
    }

    public static int getColor(Context context, int resId) {
        return context.getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(Context context, int resId) {
        return context.getResources().getColorStateList(resId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }

    public static int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getString(Context context, int resId, Object... objects) {
        return context.getResources().getString(resId, objects);
    }

    public static int getWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
