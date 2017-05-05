package com.damonlei.vimdroid.command.base;

import com.damonlei.vimdroid.Global;

/**
 * @author damonlei
 * @time 2017/3/5
 * @email danxionglei@foxmail.com
 */
public class Resp {
    public int status;
    public String data;

    public Resp() {
        this(0);
    }

    public Resp(int status) {
        this(status, null);
    }

    public Resp(int status, String data) {
        this.status = status;
        this.data = data;
    }

    public static Resp failure(String data) {
        return new Resp(Global.RESP_FAILURE, data);
    }

    public static Resp success(String data) {
        return new Resp(Global.RESP_OK, data);
    }

    @Override
    public String toString() {
        return Global.toJson(this);
    }

    public static final Resp FAILURE_RESP = new Resp(Global.RESP_FAILURE, "");

    public static final Resp SUCCESS_RESP = new Resp(Global.RESP_OK, "");
}
