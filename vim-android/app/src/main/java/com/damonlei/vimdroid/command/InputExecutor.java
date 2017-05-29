package com.damonlei.vimdroid.command;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.ResourceHelper;
import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.R;
import com.damonlei.vimdroid.command.base.MultiNodeCommandExecutor;
import com.damonlei.vimdroid.command.base.Resp;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.keyBoard.KeyBoardCommandExecutor;
import com.damonlei.vimdroid.keyBoard.KeyCode;
import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.damonlei.vimdroid.keyBoard.KeyRequestConsumer;

import java.util.List;

import static com.damonlei.vimdroid.keyBoard.KeyCode.I;

/**
 * @author damonlei
 * @time 2017/5/29
 * @email danxionglei@foxmail.com
 */
public class InputExecutor extends MultiNodeCommandExecutor<KeyRequest, Resp> implements KeyRequestConsumer {

    private Context context;

    public InputExecutor(KeyBoardCommandExecutor executor) {
        super(executor);
        context = executor.getContext();
    }

    @Override
    public Resp execute(KeyRequest data) throws Exception {
        if (!isNodeChoosed() && !isCandidateNotPrepared()) {
            throw new IllegalStateException("MultiNodeCommandExecutor state not right.");
        }
        Resp ret = handleCancelRequest(data);
        if (ret != null) {
            return ret;
        }
        if (isCandidateNotPrepared()) {
            List<AccessibilityNodeInfo> editableNodesList = getEditableNodes(data.name);
            if (Utils.nullOrNil(editableNodesList)) {
                return Resp.failure(ResourceHelper.getString(context, R.string.can_not_find_editable_node));
            }
            if (editableNodesList.size() != 1) {
                setCandidateNodeInfo(editableNodesList);
                return null;
            }
            setChoosedTargetNodeInfo(editableNodesList.get(0));
        }
        AccessibilityNodeInfo node = getChoosedNodeInfo();
        // TODO ExecuteInput
        return Resp.SUCCESS_RESP;
    }

    private List<AccessibilityNodeInfo> getEditableNodes(KeyCode code) {
        return DeviceController.getInstance().getEditableNodes();
    }


    @Override
    public boolean accept(KeyRequest request) {
        return request != null && request.name == I;
    }
}
