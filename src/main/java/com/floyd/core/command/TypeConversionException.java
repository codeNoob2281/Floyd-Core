package com.floyd.core.command;

/**
 * Type conversion exception
 *
 * @author floyd
 */
public class TypeConversionException extends RuntimeException {

    public TypeConversionException() {
    }

    public TypeConversionException(String message) {
        super(message);
    }

    public TypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    protected TypeConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
