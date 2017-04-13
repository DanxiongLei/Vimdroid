package com.damonlei.vimdroid.view;

import android.content.Context;
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

    public TagViewExecutor() {
        this(WindowRoot.getInstance());
    }

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
        if (mTagViewLogic.process(data.name)) {
            return Resp.SUCCESS_RESP;
        }
        return Resp.FAILURE_RESP;
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

    void hit(ITagViewItem item) {
        int x = item.getAbsoluteX() + (item.getItemWidth() >> 1);
        int y = item.getAbsoluteY() + (item.getItemHeight() >> 1);
        DeviceController.getInstance().click(x, y);
        // 显示点击区域和点击点
        if (Global.SETTINGS.displayClickableRegion) {
            ImageView v = new ImageView(mViewRoot.getContext());
            Drawable round_pointer = ResourceHelper.getDrawable(mViewRoot.getContext(), R.drawable.round_pointer);
            v.setImageDrawable(round_pointer);
            int halfDrawableWidth = round_pointer.getIntrinsicWidth() / 2;
            ((WindowRoot) mViewRoot).addViewAtScreenCoordinate(v, x - halfDrawableWidth, y - halfDrawableWidth);
            ImageView v2 = new ImageView(mViewRoot.getContext());
            v2.setImageDrawable(ResourceHelper.getDrawable(mViewRoot.getContext(), R.drawable.rectangle_pointer));
            v2.setMinimumWidth(item.getItemWidth());
            v2.setMinimumHeight(item.getItemHeight());
            ((WindowRoot) mViewRoot).addViewAtScreenCoordinate(v2, item.getAbsoluteX(), item.getAbsoluteY());
        }
    }

    void performAction(ITagViewItem item) {
    }

    List<AccessibilityNodeInfo> getClickableNodes() {
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
