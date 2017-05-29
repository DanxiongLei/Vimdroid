package com.damonlei.vimdroid;

import android.app.Application;
import android.content.Context;

import com.damonlei.vimdroid.utils.GsonLogic;
import com.google.gson.Gson;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/5
 * @email danxionglei@foxmail.com
 */
public final class Global extends Application {

    public static final int RESP_OK = 0;

    public static final int RESP_FAILURE = -1;

    public static final int RESP_FAILURE_NOT_FATAL = -2;

    public static final int CMD_ID_PING = 236;

    public static final int CMD_ID_KEYBOARD = 237;

    public static final int CMD_ID_PREPARED = 238;

    public static final int CMD_ID_SHUTDOWN = 239;

    public static Settings SETTINGS;

    public static Gson getGson() {
        return GsonLogic.getGson();
    }

    public static String toJson(Object json) {
        return GsonLogic.getGson().toJson(json);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        SETTINGS = new Settings.Builder(this).build();
    }
}
