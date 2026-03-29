package com.floyd.core.inventory.io;

/**
 * ItemStack serialization exception
 *
 * @author floyd
 * @date 2026/3/24
 */
public class ItemStackSerializeException extends RuntimeException {
    public ItemStackSerializeException(String message) {
        super(message);
    }

    public ItemStackSerializeException() {
        super();
    }

    public ItemStackSerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemStackSerializeException(Throwable cause) {
        super(cause);
    }

    protected ItemStackSerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
