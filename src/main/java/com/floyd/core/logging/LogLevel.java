package com.floyd.core.logging;

import lombok.Getter;

/**
 * @author floyd
 */
public enum LogLevel {

    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3);

    private final Integer value;

    LogLevel(Integer value) {
        this.value = value;
    }

    /**
     * Check if log level is included
     *
     * @param logLevel the log level to check
     * @return true if included
     */
    public boolean include(LogLevel logLevel) {
        return this.value <= logLevel.value;
    }
}
