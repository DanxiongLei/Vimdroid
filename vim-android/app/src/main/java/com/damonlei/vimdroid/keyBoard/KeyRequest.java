package com.damonlei.vimdroid.keyBoard;

import com.damonlei.vimdroid.utils.GsonLogic;

/**
 * @author damonlei
 * @time 2017/3/14
 * @email danxionglei@foxmail.com
 */
public class KeyRequest {
    public KeyCode name;
    public boolean ctrl;
    public boolean shift;
    public boolean meta;

    @Override
    public String toString() {
        return "KeyRequest " + GsonLogic.getGson().toJson(this);
    }
}
