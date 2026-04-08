package com.floyd.core.inventory.io;

/**
 * ItemStack deserialization exception
 *
 * @author floyd
 */
public class ItemStackDeserializeException extends RuntimeException {
    public ItemStackDeserializeException(String message) {
        super(message);
    }

    public ItemStackDeserializeException() {
        super();
    }

    public ItemStackDeserializeException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ItemStackDeserializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ItemStackDeserializeException(Throwable cause) {
        super(cause);
    }
}
