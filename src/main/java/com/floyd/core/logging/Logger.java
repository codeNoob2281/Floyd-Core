package com.floyd.core.logging;

/**
 * Logger interface for logging messages with support for parameterized logging.
 * <p>
 * All log methods support message templates using {@code {}} as placeholders for dynamic arguments,
 * similar to SLF4J's parameterize logging.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     logger.info("User {} logged in at {}", username, timestamp);
 *     logger.error("Failed to connect to {}: {}", host, exception.getMessage(), exception);
 * </pre>
 * </p>
 *
 * @author floyd
 */
public interface Logger {

    /**
     * Logs a message at DEBUG level.
     *
     * @param format the message template, using {@code {}} as placeholders
     * @param args   the arguments to fill the placeholders
     */
    void debug(String format, Object... args);

    /**
     * Logs a message at INFO level.
     *
     * @param format the message template, using {@code {}} as placeholders
     * @param args   the arguments to fill the placeholders
     */
    void info(String format, Object... args);

    /**
     * Logs a message at WARN level.
     *
     * @param format the message template, using {@code {}} as placeholders
     * @param args   the arguments to fill the placeholders
     */
    void warn(String format, Object... args);

    /**
     * Logs a message at ERROR level.
     *
     * @param format the message template, using {@code {}} as placeholders
     * @param args   the arguments to fill the placeholders
     */
    void error(String format, Object... args);

    /**
     * Logs an error with a Throwable at ERROR level.
     *
     * @param throwable the throwable to log
     */
    void error(Throwable throwable);

    /**
     * Gets the name of this logger.
     *
     * @return the logger name
     */
    String getName();

    /**
     * Gets the current log level of this logger.
     *
     * @return the log level
     */
    LogLevel getLevel();
}