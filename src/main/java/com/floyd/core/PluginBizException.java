package com.floyd.core;

/**
 * Plugin business exception
 *
 * @author floyd
 * @date 2026/3/24
 */
public class PluginBizException extends RuntimeException {

    public PluginBizException() {
        super();
    }

    public PluginBizException(String message) {
        super(message);
    }

    public PluginBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginBizException(Throwable cause) {
        super(cause);
    }

    protected PluginBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
