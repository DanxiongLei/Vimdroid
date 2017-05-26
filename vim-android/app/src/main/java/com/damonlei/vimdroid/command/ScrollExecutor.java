package com.damonlei.vimdroid.command;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.base.MultiNodeCommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.keyBoard.KeyCode;
import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.damonlei.vimdroid.keyBoard.KeyRequestConsumer;
import com.damonlei.vimdroid.utils.ThreadPool;

import java.util.List;

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

    private Rect cacheRect = new Rect();

    private KeyCode[] mAcceptKeyCodeArray = new KeyCode[]{
            H, J, K, L, UP, DOWN, LEFT, RIGHT
    };

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        if (!isNodeChoosed() && !isCandidateCleared()) {
            throw new IllegalStateException("MultiNodeCommandExecutor state not right.");
        }
        // 判断是否是取消选择命令，如果是，则取消刚才的选择。
        Resp ret = handleCancelRequest(data);
        if (ret != null) {
            return ret;
        }
        KeyCode code = unifyKeyCode(data);
        if (isCandidateCleared()) {
            // 如果没有已经选中的Node，需要准备后，将控制权交出去，由用户进行选择。
            List<AccessibilityNodeInfo> nodes = getScrollableNodes(code);
            if (Utils.nullOrNil(nodes)) {
                return Resp.FAILURE_RESP;
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

        private Rect cacheRect;

        public ScrollRunnable(KeyCode code, AccessibilityNodeInfo node) {
            this.code = code;
            this.node = node;
            this.cacheRect = new Rect();
        }

        @Override
        public void run() {
            node.getBoundsInScreen(cacheRect);
            int maxSwipeDistancePerTime = Global.SETTINGS.scrollPx;
            // 每个step执行意味着5ms
            int steps = 40/*ms*/ / 5;
            // 部分可滑动滑块不能从边缘滑动，否则可能将事件传递给其他view.
            // slop就是主动向内缩小一段距离。
            int slop = 1;
            if (cacheRect.width() > slop * 2) {
                cacheRect.left += slop;
                cacheRect.right -= slop;
            }
            if (cacheRect.height() > slop * 2) {
                cacheRect.top += slop;
                cacheRect.bottom -= slop;
            }
            if (code == UP) {
                int centerX = cacheRect.centerX();
                DeviceController.getInstance().swipe(centerX, cacheRect.top, centerX,
                        cacheRect.top + Math.min(cacheRect.height(), maxSwipeDistancePerTime), steps);
            } else if (code == DOWN) {
                int centerX = cacheRect.centerX();
                DeviceController.getInstance().swipe(centerX, cacheRect.bottom, centerX,
                        cacheRect.bottom - Math.min(cacheRect.height(), maxSwipeDistancePerTime), steps);
            } else if (code == LEFT) {
                int centerY = cacheRect.centerY();
                DeviceController.getInstance().swipe(cacheRect.left, centerY,
                        cacheRect.left + Math.min(cacheRect.width(), maxSwipeDistancePerTime), centerY, steps);
            } else {
                int centerY = cacheRect.centerY();
                DeviceController.getInstance().swipe(cacheRect.right, centerY,
                        cacheRect.right - Math.min(cacheRect.width(), maxSwipeDistancePerTime), centerY, steps);
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
