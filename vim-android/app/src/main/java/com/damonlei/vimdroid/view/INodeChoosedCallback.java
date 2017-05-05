package com.damonlei.vimdroid.view;

import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.vimdroid.command.base.Resp;

/**
 * @author damonlei
 * @time 2017/5/3
 * @email danxionglei@foxmail.com
 */
public interface INodeChoosedCallback {
    /**
     * @param nodeInfo 选中的nodeInfo，如果中途取消，则为null
     */
    public Resp nodeChoosed(AccessibilityNodeInfo nodeInfo);
}
