package com.damonlei.vimdroid.command.base;

import android.os.Looper;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */

public interface ICommandExecutor<Request, Response> {

    int allowLongTimeExecute();

    int getType();

    // 如果为null，在当前线程(即IO线程)中执行，不进行线程切换
    Looper getLooper();

    Response execute(Request data) throws Exception;
}
