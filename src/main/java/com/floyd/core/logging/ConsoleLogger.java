package com.floyd.core.logging;

import java.util.logging.Level;

/**
 * @author floyd
 * @date 2026/3/22
 */
public interface ConsoleLogger {

    void debug(String message);

    void info(String message);

    void warning(String message);

    void error(String message);

    void error(String message, Throwable throwable);

    Level getLevel();
}
