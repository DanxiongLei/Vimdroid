package com.damonlei.vimdroid.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.vimdroid.R;

/**
 * @author damonlei
 * @time 2017/3/5
 * @email danxionglei@foxmail.com
 */
class TagViewViewItem extends AppCompatTextView implements ITagViewItem {

    private int selectIndex;

    private SpannableString spannableString;

    private Object spanObject;

    private int itemWidth, itemHeight;

    private int absoluteX, absoluteY;

    private AccessibilityNodeInfo source;

    public TagViewViewItem(Context context) {
        super(context);
        init();
    }

    public TagViewViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagViewViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.drawable_default_tag_view_bg);
        setTextSize(15);
        setTypeface(getTypeface(), Typeface.BOLD);
        setTextColor(ResourceHelper.getColor(getContext(), android.R.color.black));
        setPadding(
                ResourceHelper.fromDPToPix(getContext(), 6),
                ResourceHelper.fromDPToPix(getContext(), 0),
                ResourceHelper.fromDPToPix(getContext(), 6),
                ResourceHelper.fromDPToPix(getContext(), 0));
        // cast shadow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(ResourceHelper.fromDPToPix(getContext(), 20));
        } else {
            // not impl
        }
    }

    @Override
    public void setSelectIndex(int index) {
        if (index < 0) {
            selectIndex = 0;
            return;
        }
        if (spannableString == null || spannableString.length() == 0) {
            selectIndex = 0;
            return;
        }
        if (index > spannableString.length()) {
            selectIndex = spannableString.length();
        } else {
            selectIndex = index;
        }
        spannableString.removeSpan(spanObject);
        spannableString.setSpan(
                spanObject = new ForegroundColorSpan(ResourceHelper.getColor(getContext(), android.R.color.holo_orange_dark)),
                0, selectIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    @Override
    public int getSelectIndex() {
        return selectIndex;
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            spannableString = new SpannableString("");
        } else {
            spannableString = new SpannableString(text);
        }
        super.setText(spannableString);
    }

    @Override
    public String getText() {
        if (spannableString == null) {
            return null;
        }
        return spannableString.toString();
    }

    @Override
    public Character getNextChar() {
        if (spannableString == null || spannableString.length() == 0) {
            return null;
        }
        if (selectIndex >= spannableString.length()) {
            return null;
        }
        return spannableString.charAt(selectIndex);
    }

    @Override
    public int length() {
        if (spannableString == null) {
            return 0;
        }
        return spannableString.length();
    }

    @Override
    public void reset() {
        setText(null);
        selectIndex = 0;
        setVisibility(false);
        setAbsoluteX(0);
        setAbsoluteY(0);
        setItemWidth(0);
        setItemHeight(0);
    }

    @Override
    public void setAbsoluteX(int x) {
        this.absoluteX = x;
    }

    @Override
    public void setAbsoluteY(int y) {
        this.absoluteY = y;
    }

    @Override
    public int getAbsoluteX() {
        return absoluteX;
    }

    @Override
    public int getAbsoluteY() {
        return absoluteY;
    }

    @Override
    public void setVisibility(boolean visibility) {
        if (visibility) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public int getItemWidth() {
        return itemWidth;
    }

    @Override
    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public void setItemWidth(int width) {
        this.itemWidth = width;
    }

    @Override
    public void setItemHeight(int height) {
        this.itemHeight = height;
    }

    @Override
    public void setSourceNodeInfo(AccessibilityNodeInfo nodeInfo) {
        this.source = nodeInfo;
    }

    @Override
    public AccessibilityNodeInfo getSourceNodeInfo() {
        return this.source;
    }
}
