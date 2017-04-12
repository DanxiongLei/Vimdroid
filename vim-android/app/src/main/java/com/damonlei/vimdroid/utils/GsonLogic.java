package com.damonlei.vimdroid.utils;

import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonLogic {

    private static Gson sGson;

    public static Gson getGson() {
        if (sGson == null) {
            initGson();
        }
        return sGson;
    }

    private static void initGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(KeyRequest.class, new KeyRequestProcessor());
        sGson = builder.create();
    }
}
