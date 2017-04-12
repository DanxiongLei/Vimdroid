package com.damonlei.vimdroid.command;

import android.app.Service;
import android.os.Looper;

import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;

/**
 * @author damonlei
 * @time 2017/4/11
 * @email danxionglei@foxmail.com
 */
public class ShutdownExecutor implements ICommandExecutor<Object,Resp> {


    private Service service;

    public ShutdownExecutor(Service service) {
        this.service = service;
    }

    @Override
    public int getType() {
        return Global.CMD_ID_SHUTDOWN;
    }

    @Override
    public Looper getLooper() {
        return null;
    }

    @Override
    public Resp execute(Object data) throws Exception {
        service.stopSelf();
        return Resp.SUCCESS_RESP;
    }
}
