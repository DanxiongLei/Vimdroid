package com.damonlei.vimdroid.view;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.damonlei.utils.Utils;
import com.damonlei.vimdroid.device.DeviceController;
import com.damonlei.vimdroid.keyBoard.KeyCode;
import com.damonlei.vimdroid.utils.GsonLogic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.damonlei.vimdroid.keyBoard.KeyCode.A;
import static com.damonlei.vimdroid.keyBoard.KeyCode.D;
import static com.damonlei.vimdroid.keyBoard.KeyCode.S;

/**
 * @author damonlei
 * @time 2017/3/14
 * @email danxionglei@foxmail.com
 */
public class TagViewLogicTest {

    @Test
    public void testUtil() {
        Object obj = new ArrayList<>();
        Utils.nullOrNil(obj);
    }

    @Test
    public void testGenerateKeyHintList() {
        Assert.assertArrayEquals("1", generateKeyHintList(1).toArray(), new String[]{S.getAsStr()});
        Assert.assertArrayEquals("2", generateKeyHintList(2).toArray(), new String[]{S.getAsStr(), A.getAsStr()});
        Assert.assertArrayEquals("3", generateKeyHintList(3).toArray(), new String[]{S.getAsStr(), A.getAsStr(), D.getAsStr()});
        Assert.assertArrayEquals("4", generateKeyHintList(4).toArray(), new String[]{
                A.getAsStr(), D.getAsStr(), S.getAsStr() + S.getAsStr(), S.getAsStr() + A.getAsStr()});
        Assert.assertArrayEquals("5", generateKeyHintList(5).toArray(), new String[]{
                A.getAsStr(), D.getAsStr(),
                S.getAsStr() + S.getAsStr(), S.getAsStr() + A.getAsStr(), S.getAsStr() + D.getAsStr()});
        Assert.assertArrayEquals("7", generateKeyHintList(7).toArray(), new String[]{D.getAsStr(),
                S.getAsStr() + S.getAsStr(), S.getAsStr() + A.getAsStr(), S.getAsStr() + D.getAsStr(),
                A.getAsStr() + S.getAsStr(), A.getAsStr() + A.getAsStr(), A.getAsStr() + D.getAsStr()});
    }

    private KeyCode[] board = new KeyCode[]{
            S, A, D};

    private List<String> generateKeyHintList(int nodeCount) {
        if (nodeCount <= 0) {
            throw new IllegalStateException();
        }
        List<String> list = new LinkedList<>();
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

}