package com.damonlei.vimdroid.view;

import android.content.Context;

/**
 * @author damonlei
 * @time 2017/3/2
 * @email danxionglei@foxmail.com
 */
class DefaultTagFactory implements TagViewExecutor.Factory {
    @Override
    public TagViewViewItem getTagItem(Context context) {
        TagViewViewItem textView = new TagViewViewItem(context);
        textView.setSelectIndex(0);
        textView.setText(null);
        return textView;
    }
}
