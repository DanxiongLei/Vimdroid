package com.damonlei.vimdroid.command;

import android.os.Looper;

import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.base.CommandDispatcher;
import com.damonlei.vimdroid.command.base.CommandExecutorBase;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/8
 * @email danxionglei@foxmail.com
 */
public class PingExecutor extends CommandExecutorBase<Object, Resp> {

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
        Timber.d("Ping answered.");
        return Resp.SUCCESS_RESP;
    }
}
