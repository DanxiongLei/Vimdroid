package com.damonlei.vimdroid.keyBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author damonlei
 * @time 2017/3/12
 * @email danxionglei@foxmail.com
 */
public enum KeyCode {
    A("A", 0), B("B", 0), C("C", 0), D("D", 0), E("E", 0), F("F", 0), G("G", 0), H("H", 0), I("I", 0), J("J", 0), K("K", 0),
    L("L", 0), M("M", 0), N("N", 0), O("O", 0), P("P", 0), Q("Q", 0), R("R", 0), S("S", 0), T("T", 0), U("U", 0), V("V", 0),
    W("W", 0), X("X", 0), Y("Y", 0), Z("Z", 0),
    NUM_0("0", 1), NUM_1("1", 1), NUM_2("2", 1), NUM_3("3", 1), NUM_4("4", 1), NUM_5("5", 1),
    NUM_6("6", 1), NUM_7("7", 1), NUM_8("8", 1), NUM_9("9", 1),
    UP("Up", 2), DOWN("Down", 2), LEFT("Left", 2), RIGHT("Right", 2),
    ESC("Esc", 3), ENTER("Enter", 3), TAB("Tab", 3), SPACE("Space", 3),
    BACKSPACE("Backspace", 3), DELETE("Delete", 3);

    private static Map<String, KeyCode> map = new HashMap<>();

    static {
        map.put("Escape", ESC);
        map.put("escape", ESC);
        map.put("Return", ENTER);
        map.put("return", ENTER);
        for (KeyCode keyCode : KeyCode.values()) {
            map.put(keyCode.code, keyCode);
            map.put(keyCode.code.toLowerCase(), keyCode);
        }
    }

    private String code;

    private int type;

    KeyCode(String code, int type) {
        this.code = code;
        this.type = type;
    }

    public boolean isLetter() {
        return type == 0;
    }

    public boolean isNumber() {
        return type == 1;
    }

    public boolean isDirection() {
        return type == 2;
    }

    public boolean isControlKey() {
        return type == 3;
    }

    public int getAsNumber() {
        if (!isNumber()) {
            throw new IllegalStateException();
        }
        return Integer.valueOf(code);
    }

    public char getAsUpperLetter() {
        if (!isLetter()) {
            throw new IllegalStateException();
        }
        return Character.toUpperCase(code.charAt(0));
    }

    public char getAsLowerLetter() {
        if (!isLetter()) {
            throw new IllegalStateException();
        }
        return Character.toLowerCase(code.charAt(0));
    }

    public String getAsStr() {
        return code;
    }

    public String getAsLowerStr() {
        return code.toLowerCase();
    }

    public static String parse(KeyCode code) {
        return code.code;
    }

    public static KeyCode parse(String code) {
        return map.get(code);
    }


}
