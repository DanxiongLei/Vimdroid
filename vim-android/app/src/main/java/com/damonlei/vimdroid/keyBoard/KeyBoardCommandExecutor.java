package com.damonlei.vimdroid.keyBoard;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.EnsurePrepareExecutor;
import com.damonlei.vimdroid.command.GlobalControlKeyExecutor;
import com.damonlei.vimdroid.command.ScrollExecutor;
import com.damonlei.vimdroid.command.base.CommandExecutorBase;
import com.damonlei.vimdroid.command.base.MultiNodeCommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.view.INodeChoosedCallback;
import com.damonlei.vimdroid.view.TagViewExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import timber.log.Timber;

import static com.damonlei.vimdroid.keyBoard.KeyCode.ESC;
import static com.damonlei.vimdroid.keyBoard.KeyCode.F;
import static com.damonlei.vimdroid.keyBoard.KeyCode.H;
import static com.damonlei.vimdroid.keyBoard.KeyCode.P;
import static com.damonlei.vimdroid.keyBoard.KeyCode.R;

/**
 * @author damonlei
 * @time 2017/3/14
 * @email danxionglei@foxmail.com
 */
public class KeyBoardCommandExecutor extends CommandExecutorBase<KeyRequest, Resp> {

    private Context mContext;

    private TagViewExecutor mTagViewExecutor;

    private GlobalControlKeyExecutor mGlobalKeyExecutor;

    private EnsurePrepareExecutor mEnsurePrepareExecutor;

    /**
     * 控制并执行scroll动作，当屏幕上有多个scrollable的组件时，调用TagViewExecutor，让用户进行选择。
     */
    private ScrollExecutor mScrollExecutor;

    /**
     * 判断当前状态下，键盘指令应该交给哪个Executor来响应。
     */
    @State
    private int mState = STATE_IDLE;

    public static final int STATE_IDLE = 1;

    // 待选择状态
    public static final int STATE_TAG_VIEW_ATTACHED = 2;

    // 已选择状态
    public static final int STATE_NODE_SELECTED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_IDLE, STATE_TAG_VIEW_ATTACHED, STATE_NODE_SELECTED})
    @interface State {
    }

    public KeyBoardCommandExecutor(Context context, EnsurePrepareExecutor executor) {
        mContext = context;
        mTagViewExecutor = new TagViewExecutor();
        mTagViewExecutor.setKeyBoardController(this);
        mGlobalKeyExecutor = new GlobalControlKeyExecutor();
        mEnsurePrepareExecutor = executor;
        mScrollExecutor = new ScrollExecutor(this);
    }

    @Override
    public int getType() {
        return Global.CMD_ID_KEYBOARD;
    }

    @Override
    public Looper getLooper() {
        return Looper.getMainLooper();
    }

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        Timber.d("execute() called with: data = [" + data + "]");
        if (mState == STATE_TAG_VIEW_ATTACHED) {
            return mTagViewExecutor.execute(data);
        }
        if (mState == STATE_NODE_SELECTED) {
            if (mScrollExecutor.isNodeChoosed()) {
                return mScrollExecutor.execute(data);
            }
        }
        if (data.name == F) {
            return mTagViewExecutor.execute(data);
        } else if (data.name == ESC || (data.shift && (data.name == H || data.name == R))) {
            return mGlobalKeyExecutor.execute(data);
        } else if (data.name == P) {
            return mEnsurePrepareExecutor.execute(null);
        } else if (mScrollExecutor.accept(data)) {
            return mayChooseNodeLogic(mScrollExecutor, data);
        }
        return Resp.FAILURE_RESP;
    }

    private Resp mayChooseNodeLogic(final MultiNodeCommandExecutor<KeyRequest, Resp> executor, final KeyRequest data) throws Exception {
        Resp result = executor.execute(data);
        // 如果执行了scroll动作，无论成功或失败，都直接返回
        if (!Utils.nullOrNil(result)) {
            return result;
        }
        // 如果有多个scrollable对象，需要选择，需要调用TagViewExecutor帮助
        //noinspection unchecked
        mTagViewExecutor.setCandidatesList(executor.getCandidateNodeInfo());
        mTagViewExecutor.setNodeChoosedCallback(new INodeChoosedCallback() {
            @Override
            public Resp nodeChoosed(AccessibilityNodeInfo nodeInfo) {
                if (nodeInfo == null) {
                    // cancelled
                    executor.clearChoosedNodeInfo();
                    Timber.d("nodeChoosed(109) User cancelled");
                    return Resp.FAILURE_RESP;
                }
                // choosed
                executor.setChoosedTargetNodeInfo(nodeInfo);
                try {
                    return executor.execute(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return mTagViewExecutor.execute(new KeyRequest(F, false, false, false));
    }

    public void setState(@State int state) {
        this.mState = state;
    }

    @State
    public int getState() {
        return mState;
    }

    public Context getContext() {
        return mContext;
    }

}
