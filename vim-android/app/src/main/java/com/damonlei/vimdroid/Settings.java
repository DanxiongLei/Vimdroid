package com.damonlei.vimdroid;

import android.app.Application;

/**
 * @author damonlei
 * @time 2017/4/12
 * @email danxionglei@foxmail.com
 */
public class Settings {
    // 是否显示点击区域
    public boolean displayClickableRegion;

    // 每次滑动的距离，up/down/left/right
    public int scrollPx;

    // 是否平滑滑动
    public boolean smoothScroll;

    private Settings(Builder builder) {
        displayClickableRegion = builder.displayClickableRegion;
        scrollPx = builder.scrollPx;
        smoothScroll = builder.smoothScroll;
    }

    public static final class Builder {
        private boolean displayClickableRegion = false;
        private int scrollPx = 100 * 3;
        private boolean smoothScroll = false;

        public Builder() {
        }

        public Builder displayClickableRegion(boolean val) {
            displayClickableRegion = val;
            return this;
        }

        public Builder scrollPx(int val) {
            scrollPx = val;
            return this;
        }

        public Builder smoothScroll(boolean val) {
            smoothScroll = val;
            return this;
        }

        public Settings build() {
            return new Settings(this);
        }
    }
}