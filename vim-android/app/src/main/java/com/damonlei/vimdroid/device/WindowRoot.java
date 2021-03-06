package com.damonlei.vimdroid.device;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.damonlei.utils.ResourceHelper;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/2
 * @email danxionglei@foxmail.com
 */
public class WindowRoot extends FrameLayout {

    private static WindowRoot INSTANCE;

    public static void init(Context context) {
        INSTANCE = new WindowRoot(context);
    }

    public static WindowRoot getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("INSTANCE is null");
        }
        return INSTANCE;
    }

    public static void release() {
        if (INSTANCE != null) {
            INSTANCE.detachFromWindow();
        }
        INSTANCE = null;
    }

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private volatile boolean viewAttached = false;

    private Rect cacheRect = new Rect();

    private Rect screenRect;

    public WindowRoot(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public Rect getScreenRect() {
        if (screenRect == null) {
            screenRect = new Rect();
            screenRect.left = screenRect.top = 0;
            screenRect.right = ResourceHelper.getWidthPixels(getContext());
            screenRect.bottom = ResourceHelper.getHeightPixels(getContext());
        }
        return screenRect;
    }

    public boolean intersectWithScreen(Rect dst) {
        if (dst == null) {
            return false;
        }
        Rect screen = getScreenRect();
        boolean ret = screen.intersect(dst);
        if (!ret) {
            Timber.e("intersectWithScreen dst[%s] not intersect with screen[%s]", dst.toShortString(), screen.toShortString());
        }
        return ret;
    }

    public int getStatusBarHeight() {
        this.getWindowVisibleDisplayFrame(cacheRect);
        if (cacheRect.top != 0) {
            return cacheRect.top;
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        if (result != 0) {
            return result;
        }
        return ResourceHelper.fromDPToPix(getContext(), 25);
    }

    public void attachOnWindow() {
        WindowManager windowManager = mWindowManager;
        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT > 24) {
                    type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            mLayoutParams.type = type;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
            mLayoutParams.gravity = Gravity.START | Gravity.TOP;
            mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            mLayoutParams.x = 0;
            mLayoutParams.y = 0;
        }
        windowManager.addView(this, mLayoutParams);
        viewAttached = true;
    }

    private int adjustCoordinateY(int y) {
        return y - (ResourceHelper.getHeightPixels(getContext()) - getHeight());
//        return y;
    }

    public void addViewAtScreenCoordinate(View view, int x, int y) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = x;
        params.topMargin = adjustCoordinateY(y);
        addView(view, params);
    }

    public void detachFromWindow() {
        WindowManager windowManager = mWindowManager;
        windowManager.removeView(this);
        viewAttached = false;
    }
}
