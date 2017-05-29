package com.damonlei.vimdroid.command.base;

import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import com.damonlei.vimdroid.device.WindowRoot;
import com.damonlei.vimdroid.keyBoard.KeyBoardCommandExecutor;
import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.damonlei.vimdroid.view.SelectedNodeBorderDrawable;

import java.util.List;

import static com.damonlei.vimdroid.keyBoard.KeyCode.BACKSPACE;
import static com.damonlei.vimdroid.keyBoard.KeyCode.DELETE;
import static com.damonlei.vimdroid.keyBoard.KeyCode.ESC;

/**
 * @author damonlei
 * @time 2017/5/5
 * @email danxionglei@foxmail.com
 */
public abstract class MultiNodeCommandExecutor<Req, Response> extends CommandExecutorBase<Req, Response> {

    private KeyBoardCommandExecutor mKeyBoardCommandExecutor;

    /**
     * 有多个可以被滚动的组件，需要用户选择
     */
    private List<AccessibilityNodeInfo> mCandidateNodeInfo;

    /**
     * 用户选择完成后，真正执行scroll的组件
     */
    private AccessibilityNodeInfo mChoosedNodeInfo;

    private int mChoosedState = CANDIDATES_NOT_PREPARED;

    private static int CANDIDATES_NOT_PREPARED = 1;
    private static int NEED_CHOOSE_TARGET_NODE = 2;
    private static int TARGET_NODE_CHOOSED = 3;

    private WindowRoot mWindowRoot;

    private View mSelectedNodeBorderView;

    private Rect mCacheRect;

    public MultiNodeCommandExecutor() {
        init();
    }

    public MultiNodeCommandExecutor(KeyBoardCommandExecutor executor) {
        this.mKeyBoardCommandExecutor = executor;
        init();
    }

    private void init() {
        mWindowRoot = WindowRoot.getInstance();
    }

    public void setKeyBoardCommandExecutor(KeyBoardCommandExecutor executor) {
        this.mKeyBoardCommandExecutor = executor;
    }

    public void setCandidateNodeInfo(List<AccessibilityNodeInfo> mCandidateNodeInfo) {
        this.mCandidateNodeInfo = mCandidateNodeInfo;
        setChoosedState(NEED_CHOOSE_TARGET_NODE);
    }

    public void setChoosedTargetNodeInfo(AccessibilityNodeInfo mChoosedNodeInfo) {
        this.mChoosedNodeInfo = mChoosedNodeInfo;
        setChoosedState(TARGET_NODE_CHOOSED);
        mKeyBoardCommandExecutor.setState(KeyBoardCommandExecutor.STATE_NODE_SELECTED);
        drawChoosedNodeBorder(mChoosedNodeInfo);
    }

    public void clearChoosedNodeInfo() {
        mChoosedNodeInfo = null;
        mCandidateNodeInfo = null;
        setChoosedState(CANDIDATES_NOT_PREPARED);
        mKeyBoardCommandExecutor.setState(KeyBoardCommandExecutor.STATE_IDLE);
        clearChoosedNodeBorder();
    }

    private void drawChoosedNodeBorder(AccessibilityNodeInfo node) {
        if (node == null) {
            return;
        }
        if (mSelectedNodeBorderView == null) {
            ImageView border = new ImageView(mWindowRoot.getContext());
            border.setImageDrawable(new SelectedNodeBorderDrawable());
            mSelectedNodeBorderView = border;
        }
        if (mCacheRect == null) {
            mCacheRect = new Rect();
        }
        node.getBoundsInScreen(mCacheRect);
        mSelectedNodeBorderView.setMinimumWidth(mCacheRect.width());
        mSelectedNodeBorderView.setMinimumHeight(mCacheRect.height());
        mWindowRoot.addViewAtScreenCoordinate(mSelectedNodeBorderView, mCacheRect.left, mCacheRect.top);
    }

    private void clearChoosedNodeBorder() {
        if (mSelectedNodeBorderView == null) {
            return;
        }
        mWindowRoot.removeView(mSelectedNodeBorderView);
    }

    public List<AccessibilityNodeInfo> getCandidateNodeInfo() {
        return mCandidateNodeInfo;
    }

    protected AccessibilityNodeInfo getChoosedNodeInfo() {
        return mChoosedNodeInfo;
    }

    public boolean isNodeChoosed() {
        return mChoosedState == TARGET_NODE_CHOOSED;
    }

    public boolean isCandidateNotPrepared() {
        return mChoosedState == CANDIDATES_NOT_PREPARED;
    }

    public boolean isCandidatePrepared() {
        return mChoosedState == NEED_CHOOSE_TARGET_NODE;
    }

    private void setChoosedState(int choosed) {
        this.mChoosedState = choosed;
    }

    protected Resp handleCancelRequest(KeyRequest data) {
        if (isNodeChoosed() && data != null && (data.name == ESC || data.name == BACKSPACE || data.name == DELETE)) {
            // 取消选择状态
            clearChoosedNodeInfo();
            return Resp.SUCCESS_RESP;

        }
        return null;
    }
}
