package com.floyd.core.command;

/**
 * @author floyd
 */
public class CommandInvokeException extends RuntimeException {
    protected CommandInvokeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CommandInvokeException(Throwable cause) {
        super(cause);
    }

    public CommandInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandInvokeException(String message) {
        super(message);
    }

    public CommandInvokeException() {
        super();
    }
}
