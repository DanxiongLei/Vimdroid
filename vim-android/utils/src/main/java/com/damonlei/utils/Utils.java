package com.damonlei.utils;

import android.os.SystemClock;
import android.support.annotation.IntegerRes;

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author damonlei
 * @time 2016/12/20
 * @email danxionglei@foxmail.com
 */
public class Utils {

    public static long ticksToNow(final long before) {
        return (SystemClock.elapsedRealtime() - before);
    }

    public static long currentTicks() {
        return SystemClock.elapsedRealtime();
    }

    public static boolean nullOrNil(String string) {
        return string == null ||
                "".equals(string);
    }

    public static boolean nullOrNil(Object object) {
        return object == null;
    }

    public static int toInt(String str) {
        return Integer.valueOf(str);
    }

    public static boolean nullOrNil(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean nullOrNil(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(String[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nullOrNil(boolean[] array) {
        return array == null || array.length == 0;
    }

}
