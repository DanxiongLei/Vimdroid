package com.damonlei.vimdroid.view;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author damonlei
 * @time 2017/3/5
 * @email danxionglei@foxmail.com
 */
public interface ITagViewItem {

    /**
     * 改变当前的选中指针
     */
    void setSelectIndex(int index);

    /**
     * 获得下一个当前字符的index，比如"FE"，初始时index为0，指向F，当选中F时，index++，指向E
     */
    int getSelectIndex();

    void setText(String text);

    String getText();

    /**
     * 获得当前可操作的第一个字符
     */
    Character getNextChar();

    int length();

    void reset();

    void setAbsoluteX(int x);

    void setAbsoluteY(int y);

    int getAbsoluteX();

    int getAbsoluteY();

    void setVisibility(boolean visibility);

    boolean isVisible();

    int getItemWidth();

    int getItemHeight();

    void setItemWidth(int width);

    void setItemHeight(int height);

    void setSourceNodeInfo(AccessibilityNodeInfo nodeInfo);

    AccessibilityNodeInfo getSourceNodeInfo();
}
