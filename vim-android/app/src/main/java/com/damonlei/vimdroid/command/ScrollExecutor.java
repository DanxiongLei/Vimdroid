package com.damonlei.vimdroid.command;

import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.R;
import com.damonlei.vimdroid.command.base.MultiNodeCommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.device.WindowRoot;
import com.damonlei.vimdroid.keyBoard.KeyBoardCommandExecutor;
import com.damonlei.vimdroid.keyBoard.KeyCode;
import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.damonlei.vimdroid.keyBoard.KeyRequestConsumer;
import com.damonlei.vimdroid.utils.ThreadPool;

import java.util.List;

import timber.log.Timber;

import static com.damonlei.vimdroid.keyBoard.KeyCode.DOWN;
import static com.damonlei.vimdroid.keyBoard.KeyCode.H;
import static com.damonlei.vimdroid.keyBoard.KeyCode.J;
import static com.damonlei.vimdroid.keyBoard.KeyCode.K;
import static com.damonlei.vimdroid.keyBoard.KeyCode.L;
import static com.damonlei.vimdroid.keyBoard.KeyCode.LEFT;
import static com.damonlei.vimdroid.keyBoard.KeyCode.RIGHT;
import static com.damonlei.vimdroid.keyBoard.KeyCode.UP;

/**
 * @author damonlei
 * @time 2017/4/13
 * @email danxionglei@foxmail.com
 */
public class ScrollExecutor extends MultiNodeCommandExecutor<KeyRequest, Resp> implements KeyRequestConsumer {

    private Context mContext;

    private KeyCode[] mAcceptKeyCodeArray = new KeyCode[]{
            H, J, K, L, UP, DOWN, LEFT, RIGHT
    };

    public ScrollExecutor(KeyBoardCommandExecutor executor) {
        super(executor);
        this.mContext = executor.getContext();
    }

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        if (!isNodeChoosed() && !isCandidateNotPrepared()) {
            throw new IllegalStateException("MultiNodeCommandExecutor state not right.");
        }
        // 判断是否是取消选择命令，如果是，则取消刚才的选择。
        Resp ret = handleCancelRequest(data);
        if (ret != null) {
            return ret;
        }
        KeyCode code = unifyKeyCode(data);
        if (isCandidateNotPrepared()) {
            // 如果没有已经选中的Node，需要准备后，将控制权交出去，由用户进行选择。
            List<AccessibilityNodeInfo> nodes = getScrollableNodes(code);
            if (Utils.nullOrNil(nodes)) {
                return Resp.failure(ResourceHelper.getString(mContext, R.string.can_not_find_scrollable_node));
            }
            if (nodes.size() != 1) {
                setCandidateNodeInfo(nodes);
                return null;
            }
            setChoosedTargetNodeInfo(nodes.get(0));
        }
        AccessibilityNodeInfo node = getChoosedNodeInfo();
        ThreadPool.post(new ScrollRunnable(code, node));
        return Resp.SUCCESS_RESP;
    }

    private List<AccessibilityNodeInfo> getScrollableNodes(KeyCode code) {
        return DeviceController.getInstance().getScrollableNodes();
    }

    private static class ScrollRunnable implements Runnable {

        private KeyCode code;

        private AccessibilityNodeInfo node;

        public ScrollRunnable(KeyCode code, AccessibilityNodeInfo node) {
            this.code = code;
            this.node = node;
        }

        @Override
        public void run() {
            // init params
            int maxSwipeDistancePerTime = Global.SETTINGS.scrollPx;
            // 每个step执行意味着5ms
            int steps = 40/*ms*/ / 5;
            Rect scrollBounds = getScrollBounds(node);
            if (scrollBounds == null) {
                return;
            }
            scroll(maxSwipeDistancePerTime, steps, scrollBounds);
        }

        private Rect getScrollBounds(AccessibilityNodeInfo node) {
            Rect scrollBounds = new Rect();

            // get scroll bounds
            node.getBoundsInScreen(scrollBounds);

            // 防止误触status_bar
            int statusHeight = WindowRoot.getInstance().getStatusBarHeight();
            scrollBounds.top = Math.max(scrollBounds.top, statusHeight);
            if (scrollBounds.top >= scrollBounds.bottom || scrollBounds.left >= scrollBounds.right) {
                Timber.e("getScrollBounds(118) ScrollBounds is invalid. %s", scrollBounds.toShortString());
                return null;
            }

            // 部分可滑动滑块不能从边缘滑动，否则可能将事件传递给其他view.
            // slop就是主动向内缩小一段距离。
            int slop = 1;
            if (scrollBounds.width() > slop * 2 && scrollBounds.height() > slop * 2) {
                scrollBounds.left += slop;
                scrollBounds.right -= slop;
                scrollBounds.top += slop;
                scrollBounds.bottom -= slop;
                return scrollBounds;
            }
            Timber.e("ScrollRunnable found that the scrollable node was too small. %s", scrollBounds.toShortString());
            return null;
        }

        private void scroll(int maxSwipeDistancePerTime, int steps, Rect scrollBounds) {
            if (code == UP) {
                // 从中心向下方滑动 (为了避免碰到可伸缩头部)
                int centerX = scrollBounds.centerX();
                int centerY = scrollBounds.centerY();
                DeviceController.getInstance().swipe(centerX, centerY, centerX,
                        Math.min(centerY + maxSwipeDistancePerTime, scrollBounds.bottom), steps);
            } else if (code == DOWN) {
                // 从下边界向上滑动
                int centerX = scrollBounds.centerX();
                DeviceController.getInstance().swipe(centerX, scrollBounds.bottom, centerX,
                        scrollBounds.bottom - Math.min(scrollBounds.height(), maxSwipeDistancePerTime), steps);
            } else if (code == LEFT) {
                // 从左边界向右
                int centerY = scrollBounds.centerY();
                DeviceController.getInstance().swipe(scrollBounds.left, centerY,
                        scrollBounds.left + Math.min(scrollBounds.width(), maxSwipeDistancePerTime), centerY, steps);
            } else {
                // 从右边界向左
                int centerY = scrollBounds.centerY();
                DeviceController.getInstance().swipe(scrollBounds.right, centerY,
                        scrollBounds.right - Math.min(scrollBounds.width(), maxSwipeDistancePerTime), centerY, steps);
            }
        }

    }

    private KeyCode unifyKeyCode(KeyRequest data) {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        KeyCode code = data.name;

        switch (code) {
            case H:
            case LEFT:
                code = LEFT;
                break;
            case J:
            case DOWN:
                code = DOWN;
                break;
            case K:
            case UP:
                code = UP;
                break;
            case L:
            case RIGHT:
                code = RIGHT;
                break;

            default:
                throw new IllegalStateException("KeyCode not acceptable. " + code);
        }
        return code;
    }

    @Override
    public boolean accept(KeyRequest request) {
        if (request == null) {
            return false;
        }

        if (request.ctrl || request.shift || request.meta) {
            return false;
        }

        KeyCode req = request.name;
        for (KeyCode code : mAcceptKeyCodeArray) {
            if (code == req) {
                return true;
            }
        }
        return false;
    }
}
