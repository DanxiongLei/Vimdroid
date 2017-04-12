package com.damonlei.vimdroid.command.base;

/**
 * @author damonlei
 * @time 2017/3/2
 * @email danxionglei@foxmail.com
 */
public class CommandExecuteException extends RuntimeException {

    private boolean shouldInterupt;

    public CommandExecuteException() {
        this.shouldInterupt = false;
    }

    public CommandExecuteException(String message) {
        super(message);
        this.shouldInterupt = false;
    }

    public CommandExecuteException(boolean shouldInterrupt, String message) {
        super(message);
        this.shouldInterupt = shouldInterrupt;
    }

}
