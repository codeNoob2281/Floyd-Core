package com.floyd.core.logging;

import com.floyd.core.settings.PluginSettingsManager;
import com.floyd.core.settings.properties.LoggingSettings;
import com.floyd.core.util.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author floyd
 */
@Slf4j
public class ConsoleLogger {

    private static volatile Logger logger;

    private static volatile File logFile;

    private static volatile boolean firstInitialized = false;

    private static BufferedWriter fileWriter;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral("[")
            .appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .appendLiteral("]")
            .toFormatter();

    @Getter
    private final String name;

    @Getter
    private LogLevel level;

    public ConsoleLogger(String name) {
        this.name = name;
    }

    /**
     * Initialize the first logger and log file.
     *
     * @param logger  The logger
     * @param logFile The log file
     */
    public static void initializeFirst(Logger logger, File logFile) {
        if (firstInitialized) {
            return;
        }
        synchronized (ConsoleLogger.class) {
            if (firstInitialized) {
                return;
            }
            ConsoleLogger.logger = logger;
            ConsoleLogger.logFile = logFile;
            firstInitialized = true;
        }
    }

    public static synchronized void initSharedConfig(PluginSettingsManager settingsManager) {
        boolean useLoggingFile = settingsManager.getProperty(LoggingSettings.ENABLE_LOGGING_FILE);
        if (useLoggingFile) {
            initFileWriter();
        } else {
            closeFileWriter();
        }
    }

    public synchronized void initIndividualConfig(PluginSettingsManager settingsManager) {
        this.level = settingsManager.getProperty(LoggingSettings.LEVEL);
    }

    private synchronized static void initFileWriter() {
        checkFirstInitialized();
        try {
            // 确保父目录存在
            File parent = logFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException("Failed to create log directory: " + parent.getAbsolutePath());
            }
            if (!logFile.exists()) {
                if (!logFile.createNewFile()) {
                    throw new RuntimeException("Failed to create log file: " + logFile.getAbsolutePath());
                }
            }
            fileWriter = new BufferedWriter(new FileWriter(logFile, StandardCharsets.UTF_8, true), FileUtil.BUFFER_SIZE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create log file writer", e);
        }
    }

    private static void checkFirstInitialized() {
        if (!firstInitialized) {
            throw new IllegalStateException("Console logger is not initialized");
        }
    }

    public static synchronized void closeFileWriter() {
        if (fileWriter != null) {
            try {
                fileWriter.close();
                fileWriter = null;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to close log file writer", e);
            }
        }
    }

    protected static synchronized void writeLog(String message) {
        if (fileWriter != null) {
            try {
                fileWriter.write(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
                fileWriter.write(" ");
                fileWriter.write(message);
                fileWriter.write(System.lineSeparator());
                fileWriter.flush();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to write log to file", e);
            }
        }
    }

    public void debug(String message) {
        if (getLevel().include(LogLevel.DEBUG)) {
            String debugMessage = "[DEBUG] " + message;
            // Since Paper MC's default log level is INFO, we use the INFO level to print debug logs
            // without modifying the server's log level configuration.
            logger.info(debugMessage);
            writeLog(debugMessage);
        }
    }

    public void info(String message) {
        if (getLevel().include(LogLevel.INFO)) {
            logger.info(message);
            writeLog("[INFO] " + message);
        }
    }

    public void warn(String message) {
        if (getLevel().include(LogLevel.WARNING)) {
            logger.warning(message);
            writeLog("[WARN] " + message);
        }
    }

    public void error(String message) {
        if (getLevel().include(LogLevel.ERROR)) {
            String errorMessage = "[ERROR] " + message;
            logger.severe(errorMessage);
            writeLog(errorMessage);
        }
    }

    public void error(Throwable throwable) {
        if (getLevel().include(LogLevel.ERROR)) {
            String errorMessage = "[ERROR] " + throwable.getMessage();
            logger.log(Level.SEVERE, errorMessage, throwable);
            writeLog(errorMessage + System.lineSeparator() + getStackTraceString(throwable));
        }
    }

    public void error(String message, Throwable throwable) {
        if (getLevel().include(LogLevel.ERROR)) {
            String errorMessage = "[ERROR] " + message;
            logger.log(Level.SEVERE, errorMessage, throwable);
            writeLog(errorMessage + System.lineSeparator() + getStackTraceString(throwable));
        }
    }

    private String getStackTraceString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
