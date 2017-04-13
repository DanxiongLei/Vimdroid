package com.damonlei.vimdroid.keyBoard;

import android.os.Looper;

import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.command.EnsurePrepareExecutor;
import com.damonlei.vimdroid.command.GlobalControlKeyExecutor;
import com.damonlei.vimdroid.command.base.CommandExecutorBase;
import com.damonlei.vimdroid.command.base.ICommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.view.TagViewExecutor;

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

    private TagViewExecutor mTagViewExecutor;

    private GlobalControlKeyExecutor mGlobalKeyExecutor;

    private EnsurePrepareExecutor mEnsurePrepareExecutor;

    /**
     * 判断当前状态下，键盘指令应该交给哪个Executor来响应。
     */
    private int mState = STATE_IDLE;

    public static final int STATE_IDLE = 1;

    public static final int STATE_TAG_VIEW_ATTACHED = 2;

    public KeyBoardCommandExecutor(EnsurePrepareExecutor executor) {
        mTagViewExecutor = new TagViewExecutor();
        mGlobalKeyExecutor = new GlobalControlKeyExecutor();
        mEnsurePrepareExecutor = executor;
        mTagViewExecutor.setKeyBoardController(this);
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
        if (mState != STATE_IDLE && mState != STATE_TAG_VIEW_ATTACHED) {
            Timber.e("State is not STATE_IDLE or STATE_TAG_VIEW_ATTACHED");
            throw new IllegalStateException();
        }
        if (mState == STATE_TAG_VIEW_ATTACHED) {
            return mTagViewExecutor.execute(data);
        }
        if (data.name == F) {
            return mTagViewExecutor.execute(data);
        } else if (data.name == ESC || (data.shift && (data.name == H || data.name == R))) {
            return mGlobalKeyExecutor.execute(data);
        } else if (data.name == P) {
            return mEnsurePrepareExecutor.execute(null);
        }
        return Resp.FAILURE_RESP;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public int getState() {
        return mState;
    }

}
