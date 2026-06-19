package com.floyd.core.command.param;

/**
 * Parameter binding exception used to send readable error messages to players.
 * Does not extend CommandInvokeException because it is not an internal error, but a user input issue.
 *
 * @author floyd
 */
public class ParameterBindingException extends Exception {

    public ParameterBindingException() {
    }

    public ParameterBindingException(String message) {
        super(message);
    }

    public ParameterBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterBindingException(Throwable cause) {
        super(cause);
    }

    protected ParameterBindingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
