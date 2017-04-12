package com.damonlei.vimdroid.command.base;

import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.connect.Server;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
public class CommandDispatcher implements Server.DataReceivedListener {

    private Service service;

    private SparseArray<ICommandExecutor> observers;

    private int TIME_OUT_MILLISECONDS = 2000;

    public CommandDispatcher(Service service) {
        this.observers = new SparseArray<>();
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    public String dispatch(int cmdId, String data) {
        Timber.d("dispatch(42) called at thread(%s) with cmdId = [%d], data = [%s]", Thread.currentThread().getName(), cmdId, data);
        if (null == data) {
            return Global.toJson(new Resp(Global.RESP_FAILURE, "PARAMS ERROR!!! data is null"));
        }
        ICommandExecutor observer = observers.get(cmdId);
        if (observer == null) {
            throw new CommandExecuteException("No such command type is found.");
        }
        String resp;
        try {
            Object reqJson = parseJson(data, observer);
            Object respJson = execute(observer, reqJson);
            resp = Global.toJson(respJson);
        } catch (Exception e) {
            Timber.e(e, "Command Executor error.");
            return Global.toJson(new Resp(Global.RESP_FAILURE, e.toString()));
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    private Object execute(final ICommandExecutor observer, Object reqJson) throws Exception {
        Looper looper = observer.getLooper();
        if (looper == null) {
            return observer.execute(reqJson);
        }
        CommandExecuteRunnable task = new CommandExecuteRunnable(observer, reqJson);
        Handler handler = new Handler(looper);
        handler.post(task);
        Object respJson;
        try {
            respJson = task.get(TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Timber.d("execute(90) error [%s]", e);
            throw e;
        }
        return respJson;
    }

    public void register(ICommandExecutor observer) {
        observers.put(observer.getType(), observer);
    }

    public void unregister(ICommandExecutor observer) {
        observers.remove(observer.getType());
    }

    public void clear() {
        observers.clear();
    }

    public int getTimeout() {
        return TIME_OUT_MILLISECONDS;
    }

    public void setTimeout(int timeout) {
        this.TIME_OUT_MILLISECONDS = timeout;
    }

    private <E, R> E parseJson(String data, ICommandExecutor<E, R> executor) {
        Type[] genericInterfaces = executor.getClass().getGenericInterfaces();
        Type hit = null;
        for (Type type : genericInterfaces) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            if (((ParameterizedType) type).getRawType() == ICommandExecutor.class) {
                hit = type;
                break;
            }
        }
        if (hit == null) {
            throw new IllegalStateException("Type not hit...");
        }

        Type[] arguments = ((ParameterizedType) hit)
                .getActualTypeArguments();
        return Global.getGson().fromJson(data,
                arguments[0]);
    }

    @Override
    public String receive(int cmdId, String data) {
        return dispatch(cmdId, data);
    }

    @SuppressWarnings("unchecked")
    private static class CommandExecuteRunnable extends FutureTask {

        private CommandExecuteRunnable(final ICommandExecutor executor, final Object reqJson) {
            super(new Callable() {
                @Override
                public Object call() throws Exception {
                    return executor.execute(reqJson);
                }
            });
        }
    }
}
