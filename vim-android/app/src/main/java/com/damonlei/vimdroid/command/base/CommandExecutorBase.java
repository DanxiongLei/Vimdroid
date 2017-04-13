package com.damonlei.vimdroid.command.base;

import android.os.Looper;

/**
 * @author damonlei
 * @time 2017/4/12
 * @email danxionglei@foxmail.com
 */
public abstract class CommandExecutorBase<Request, Response> implements ICommandExecutor<Request, Response>{

    @Override
    public int allowLongTimeExecute() {
        return -1;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Looper getLooper() {
        return null;
    }

}
