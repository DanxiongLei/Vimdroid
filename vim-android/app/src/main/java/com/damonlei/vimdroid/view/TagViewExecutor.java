package com.damonlei.vimdroid.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v4.util.Pools.SynchronizedPool;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.Global;
import com.damonlei.vimdroid.R;
import com.damonlei.vimdroid.command.base.CommandExecuteException;
import com.damonlei.vimdroid.command.base.CommandExecutorBase;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.device.WindowRoot;
import com.damonlei.vimdroid.keyBoard.KeyBoardCommandExecutor;
import com.damonlei.vimdroid.keyBoard.KeyRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * @author damonlei
 * @time 2017/3/2
 * @email danxionglei@foxmail.com
 */
public class TagViewExecutor extends CommandExecutorBase<KeyRequest, Resp> implements IAttachableView {

    private ViewGroup mViewRoot;

    private SynchronizedPool<ITagViewItem> mViewPool = new SynchronizedPool<>(50);

    private List<ITagViewItem> mAttachedItemList = new ArrayList<>();

    private KeyBoardCommandExecutor mKeyBoardController;

    private TagViewLogic mTagViewLogic;

    private INodeChoosedCallback mNodeChoosedCallback;

    /**
     * 候选的node节点，如果设置了候选节点，那么就使用TagView的方式选择候选节点。否则，则默认自动获取Clickable的节点。
     */
    private List<AccessibilityNodeInfo> mCandidateNodesContainer;

    /**
     * 暂存钩子任务的执行结果
     */
    private Resp pendingResp;

    public TagViewExecutor() {
        this(WindowRoot.getInstance());
    }

    @SuppressWarnings("WeakerAccess")
    public TagViewExecutor(ViewGroup viewRoot) {
        setViewRoot(viewRoot);
        mTagViewLogic = new TagViewLogic(this);
    }

    public void setKeyBoardController(KeyBoardCommandExecutor executor) {
        mKeyBoardController = executor;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Looper getLooper() {
        return Looper.getMainLooper();
    }

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        Timber.d("execute() called with: data = [" + data + "]");
        if (Utils.nullOrNil(data) || Utils.nullOrNil(data.name)) {
            throw new CommandExecuteException("REQUEST PARAMS ERROR!!!");
        }
        if (!DeviceController.isInit()) {
            throw new CommandExecuteException("Window Inspector have not been prepared.");
        }
        pendingResp = null;
        boolean ret = mTagViewLogic.process(data.name);
        if (pendingResp != null) {
            return pendingResp;
        }
        return ret ? Resp.SUCCESS_RESP : Resp.FAILURE_RESP;
    }

    public interface Factory {
        TagViewViewItem getTagItem(Context context);
    }

    private Factory mFactory;

    public void setFactory(Factory factory) {
        mFactory = factory;
    }

    public Factory getFactory() {
        if (mFactory == null) {
            mFactory = new DefaultTagFactory();
        }
        return mFactory;
    }

    @Override
    public void setViewRoot(ViewGroup viewRoot) {
        this.mViewRoot = viewRoot;
    }

    @Override
    public ViewGroup getViewRoot() {
        return this.mViewRoot;
    }

    List<ITagViewItem> getAttachedItemList() {
        return mAttachedItemList;
    }

    public void setCandidatesList(List<AccessibilityNodeInfo> candidates) {
        this.mCandidateNodesContainer = candidates;
    }

    List<AccessibilityNodeInfo> getCandidatesNodeList() {
        if (mCandidateNodesContainer == null) {
            mCandidateNodesContainer = getClickableNodesCandidates();
        }
        return mCandidateNodesContainer;
    }

    public void setNodeChoosedCallback(INodeChoosedCallback callback) {
        this.mNodeChoosedCallback = callback;
    }

    boolean performAction(AccessibilityNodeInfo nodeInfo) {
        mNodeChoosedCallback = null;
        if (mCandidateNodesContainer != null) {
            mCandidateNodesContainer.clear();
            mCandidateNodesContainer = null;
        }
        if (mNodeChoosedCallback != null) {
            pendingResp = mNodeChoosedCallback.nodeChoosed(nodeInfo);
            // 当该Callback任务完成后，清理该Callback
            return true;
        }
        // use click action as default.
        return performClickAction(nodeInfo);
    }

    void cancel() {
        mNodeChoosedCallback = null;
        if (mCandidateNodesContainer != null) {
            mCandidateNodesContainer.clear();
            mCandidateNodesContainer = null;
        }
        if (mNodeChoosedCallback != null) {
            pendingResp = mNodeChoosedCallback.nodeChoosed(null);
        }
    }

    ITagViewItem getValidTagItem() {
        ITagViewItem item = mViewPool.acquire();
        if (item == null) {
            item = getFactory().getTagItem(mViewRoot.getContext());
        }
        item.reset();
        return item;
    }

    @Override
    public void attach() {
        if (Utils.nullOrNil(mAttachedItemList)) {
            return;
        }
        for (ITagViewItem item : mAttachedItemList) {
            ((WindowRoot) mViewRoot).addViewAtScreenCoordinate(((View) item), item.getAbsoluteX(), item.getAbsoluteY());
        }
        // message to keyboard controller
        mKeyBoardController.setState(KeyBoardCommandExecutor.STATE_TAG_VIEW_ATTACHED);
    }

    private boolean performClickAction(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int x = rect.centerX();
        int y = rect.centerY();
        boolean result = DeviceController.getInstance().click(x, y);
        // 显示点击区域和点击点
        if (Global.SETTINGS.displayClickableRegion) {
            ImageView clickPointRoundPointer = new ImageView(mViewRoot.getContext());
            Drawable round_pointer = ResourceHelper.getDrawable(mViewRoot.getContext(), R.drawable.round_pointer);
            clickPointRoundPointer.setImageDrawable(round_pointer);
            int halfDrawableWidth = round_pointer.getIntrinsicWidth() / 2;
            ((WindowRoot) mViewRoot).addViewAtScreenCoordinate(clickPointRoundPointer, x - halfDrawableWidth, y - halfDrawableWidth);
            ImageView nodeInfoRectBounds = new ImageView(mViewRoot.getContext());
            nodeInfoRectBounds.setImageDrawable(ResourceHelper.getDrawable(mViewRoot.getContext(), R.drawable.rectangle_pointer));
            nodeInfoRectBounds.setMinimumWidth(rect.width());
            nodeInfoRectBounds.setMinimumHeight(rect.height());
            ((WindowRoot) mViewRoot).addViewAtScreenCoordinate(nodeInfoRectBounds, rect.left, rect.top);
        }

        return result;
    }

    private List<AccessibilityNodeInfo> getClickableNodesCandidates() {
        List<AccessibilityNodeInfo> nodeInfos = null;
        nodeInfos = DeviceController.getInstance().getClickableNodes();
        if (nodeInfos == null) {
            throw new RuntimeException("Tag View getRoot null");
        }
        return nodeInfos;
    }

    @Override
    public void detach() {
        // recycle
        if (!Utils.nullOrNil(mAttachedItemList)) {
            Iterator<ITagViewItem> iterator = mAttachedItemList.iterator();
            while (iterator.hasNext()) {
                ITagViewItem item = iterator.next();
                item.reset();
                mViewPool.release(item);
                iterator.remove();
            }
        }
        // detach
        if (mViewRoot == null || mViewRoot.getChildCount() <= 0) {
            return;
        }
        for (int i = mViewRoot.getChildCount() - 1; i >= 0; i--) {
            if (mViewRoot.getChildAt(i) instanceof ITagViewItem) {
                mViewRoot.removeViewAt(i);
            }
        }

        // event to KeyBoardController
        mKeyBoardController.setState(KeyBoardCommandExecutor.STATE_IDLE);

    }

}
