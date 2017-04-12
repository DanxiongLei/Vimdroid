package com.damonlei.vimdroid.command;

import android.os.Looper;

import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;

/**
 * @author damonlei
 * @time 2017/3/8
 * @email danxionglei@foxmail.com
 */
public class PingExecutor implements ICommandExecutor<Object, Resp> {

    @Override
    public int getType() {
        return Global.CMD_ID_PING;
    }

    @Override
    public Looper getLooper() {
        return null;
    }

    @Override
    public Resp execute(Object data) throws Exception {
        return Resp.SUCCESS_RESP;
    }
}
