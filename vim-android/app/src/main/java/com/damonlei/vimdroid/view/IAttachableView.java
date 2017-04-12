package com.damonlei.vimdroid.view;

import android.view.ViewGroup;

/**
 * @author damonlei
 * @time 2017/3/2
 * @email danxionglei@foxmail.com
 */
public interface IAttachableView {

    void setViewRoot(ViewGroup viewRoot);

    ViewGroup getViewRoot();

    void attach();

    void detach();

}
