package com.damonlei.vimdroid.view;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.vimdroid.keyBoard.KeyCode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

import static com.damonlei.vimdroid.keyBoard.KeyCode.A;
import static com.damonlei.vimdroid.keyBoard.KeyCode.B;
import static com.damonlei.vimdroid.keyBoard.KeyCode.BACKSPACE;
import static com.damonlei.vimdroid.keyBoard.KeyCode.C;
import static com.damonlei.vimdroid.keyBoard.KeyCode.D;
import static com.damonlei.vimdroid.keyBoard.KeyCode.DELETE;
import static com.damonlei.vimdroid.keyBoard.KeyCode.E;
import static com.damonlei.vimdroid.keyBoard.KeyCode.F;
import static com.damonlei.vimdroid.keyBoard.KeyCode.G;
import static com.damonlei.vimdroid.keyBoard.KeyCode.H;
import static com.damonlei.vimdroid.keyBoard.KeyCode.I;
import static com.damonlei.vimdroid.keyBoard.KeyCode.J;
import static com.damonlei.vimdroid.keyBoard.KeyCode.K;
import static com.damonlei.vimdroid.keyBoard.KeyCode.L;
import static com.damonlei.vimdroid.keyBoard.KeyCode.M;
import static com.damonlei.vimdroid.keyBoard.KeyCode.N;
import static com.damonlei.vimdroid.keyBoard.KeyCode.O;
import static com.damonlei.vimdroid.keyBoard.KeyCode.P;
import static com.damonlei.vimdroid.keyBoard.KeyCode.R;
import static com.damonlei.vimdroid.keyBoard.KeyCode.S;
import static com.damonlei.vimdroid.keyBoard.KeyCode.V;
import static com.damonlei.vimdroid.keyBoard.KeyCode.W;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
class TagViewLogic {

    private static final int STATE_UNATTACHED = 1;

    private static final int STATE_ATTACHED = 2;

    private int mState = STATE_UNATTACHED;

    /**
     * 在Attached状态，用户在键盘上输入的所有键盘指令的记录
     */
    private LinkedList<KeyCode> mKeyBoardRecords = new LinkedList<>();

    private TagViewExecutor mExecutor;

    private Rect mCacheRect;

    TagViewLogic(TagViewExecutor executor) {
        this.mExecutor = executor;
        this.mCacheRect = new Rect();
    }

    public int getState() {
        return mState;
    }

    boolean process(KeyCode code) {
        if (mState == STATE_ATTACHED) {
            return processStateAttached(code);
        } else {
            return processStateUnattached(code);
        }
    }

    /**
     * 当系统处于Attached状态时，处理到来的键盘命令
     *
     * @return 如果接受了该命令则返回true，如果出现问题，返回false
     */
    private boolean processStateAttached(KeyCode code) {
        if (code == BACKSPACE || code == DELETE) {
            // 如果没有成功
            if (!backward()) {
                detachAndReset();
            }
        } else if (isAcceptedLetter(code)) {
            ITagViewItem mayHit = forward(code);
            if (mayHit != null) {
                mExecutor.hit(mayHit);
                detachAndReset();
            }
        } else {
            detachAndReset();
        }
        return true;
    }

    /**
     * 当系统处于Unattached状态时，处理到来的键盘命令。
     *
     * @return 如果接受了该命令则返回true，如果出现问题，返回false
     */
    private boolean processStateUnattached(KeyCode code) {
        if (code != F) {
            return false;
        }
        mState = STATE_ATTACHED;
        prepare();
        mExecutor.attach();
        return true;
    }

    private void prepare() {
        // ensure reset
        List<ITagViewItem> viewItemList = mExecutor.getAttachedItemList();
        viewItemList.clear();
        mKeyBoardRecords.clear();
        List<AccessibilityNodeInfo> infos = mExecutor.getClickableNodes();
        if (infos == null || infos.size() == 0) {
            throw new IllegalStateException("No clickable nodes found.");
        }
        List<String> hints = generateKeyHintList(infos.size());
        int size = hints.size();
        for (int i = 0; i < size; i++) {
            AccessibilityNodeInfo info = infos.get(i);
            String hint = hints.get(i);
            ITagViewItem item = mExecutor.getValidTagItem();
            item.setText(hint);
            info.getBoundsInScreen(mCacheRect);
            item.setAbsoluteX(mCacheRect.left);
            item.setAbsoluteY(mCacheRect.top);
            item.setItemWidth(mCacheRect.width());
            item.setItemHeight(mCacheRect.height());
            item.setVisibility(true);
            viewItemList.add(item);
        }
    }

    private KeyCode[] board = new KeyCode[]{
            S, A, D, F, H,
            K, P, J, M, L,
            W, C, N, R, E,
            I, G, O, P, B,
            V};

    private List<String> generateKeyHintList(int nodeCount) {
        if (nodeCount <= 0) {
            throw new IllegalStateException();
        }
        List<String> list = new ArrayList<>();
        int x = (int) Math.ceil((nodeCount - board.length) / (board.length - 1f));
        x = Math.max(Math.min(board.length, x), 0);
        for (int i = 0; i <= x; i++) {
            if (i == 0) {
                for (int j = x; list.size() < nodeCount && j < board.length; j++) {
                    list.add(board[j].getAsStr());
                }
            } else {
                for (KeyCode aBoard : board) {
                    if (list.size() >= nodeCount) {
                        break;
                    }
                    list.add(board[i - 1].getAsStr() + aBoard.getAsStr());
                }
            }
        }
        return list;
    }


    /**
     * 输入的是字母指令，使得tag上的选中部分前进
     *
     * @param keyCode 当前指令
     * @return 如果命中了某个tag上的指令，返回这个tag，否则返回null，即还需要继续按键进行指令操作
     */
    private ITagViewItem forward(KeyCode keyCode) {
        List<ITagViewItem> viewItemList = mExecutor.getAttachedItemList();
        if (viewItemList == null || viewItemList.isEmpty()) {
            Timber.d("No view item found.");
            return null;
        }
        for (ITagViewItem iTagViewItem : viewItemList) {
            if (!iTagViewItem.isVisible()) {
                continue;
            }
            if (Character.toUpperCase(iTagViewItem.getNextChar()) == keyCode.getAsUpperLetter()) {
                mKeyBoardRecords.add(keyCode);
                // hit the correct command.
                if (iTagViewItem.getSelectIndex() >= iTagViewItem.length() - 1) {
                    return iTagViewItem;
                }
                // if not hit, forward the selection of view.
                forward(iTagViewItem);
            } else {
                // 如果可见，同时输入的键盘指令和当前字符又不一样。
                iTagViewItem.setVisibility(false);
            }
        }
        return null;
    }

    /**
     * 将所有的tag 退回一个键
     *
     * @return 如果退回成功则返回true，否则如果退无可退，就detach掉
     */
    private boolean backward() {
        if (mKeyBoardRecords.isEmpty()) {
            return false;
        }
        List<ITagViewItem> viewItemList = mExecutor.getAttachedItemList();
        if (viewItemList == null || viewItemList.isEmpty()) {
            Timber.e("No view item found.");
            return false;
        }
        mKeyBoardRecords.removeLast();
        if (mKeyBoardRecords.isEmpty()) {
            emptySelectIndex(viewItemList);
        } else {
            for (ITagViewItem iTagViewItem : viewItemList) {
                redefineSelectIndex(iTagViewItem);
            }
        }
        return true;
    }

    private void emptySelectIndex(List<ITagViewItem> viewItems) {
        for (ITagViewItem viewItem : viewItems) {
            viewItem.setSelectIndex(0);
        }
    }

    private void redefineSelectIndex(ITagViewItem item) {
        if (item == null) {
            return;
        }
        if (startWithKeyCode(item, mKeyBoardRecords)) {
            item.setSelectIndex(mKeyBoardRecords.size());
            item.setVisibility(true);
        } else {
            item.setVisibility(false);
        }
    }

    private boolean startWithKeyCode(ITagViewItem item, List<KeyCode> records) {
        if (records == null || records.isEmpty()) {
            return true;
        }
        String text = item.getText();
        if (text.length() < records.size()) {
            return false;
        }
        int count = records.size(), index = 0;
        while (index != count) {
            if (Character.toUpperCase(text.charAt(index)) != records.get(index).getAsUpperLetter()) {
                return false;
            }
            index++;
        }
        return true;
    }


    private void forward(ITagViewItem item) {
        if (item == null) {
            return;
        }

        item.setSelectIndex(item.getSelectIndex() + 1);
    }

    private boolean isAcceptedLetter(KeyCode code) {
        if (!code.isLetter()) {
            return false;
        }
        List<ITagViewItem> tagViewItems = mExecutor.getAttachedItemList();
        if (tagViewItems == null || tagViewItems.isEmpty()) {
            return false;
        }
        for (ITagViewItem tagViewItem : tagViewItems) {
            Character anChar = tagViewItem.getNextChar();
            if (Character.toUpperCase(anChar) == code.getAsUpperLetter()) {
                return true;
            }
        }
        return false;
    }

    private void detachAndReset() {
        mState = STATE_UNATTACHED;
        mKeyBoardRecords.clear();
        mExecutor.detach();
    }

}
