package com.floyd.core.logging;

import com.floyd.core.util.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author floyd
 * @date 2026/3/22
 */
@Slf4j
public class DefaultConsoleLogger implements ConsoleLogger {

    private final Logger logger;

    private final LogConfig logConfig;

    private final File logFile;

    private BufferedWriter fileWriter;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral("[")
            .appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .appendLiteral("]")
            .toFormatter();

    public DefaultConsoleLogger(Logger logger, File logFile, LogConfig logConfig) {
        this.logger = logger;
        this.logConfig = logConfig;
        this.logFile = logFile;
        initFileWriter();
    }

    private synchronized void initFileWriter() {
        if (!logConfig.isLogFileEnabled() || fileWriter != null) {
            return;
        }
        try {
            if (!this.logFile.exists()) {
                if (!this.logFile.createNewFile()) {
                    throw new RuntimeException("Failed to create log file: " + this.logFile.getAbsolutePath());
                }
            }
            fileWriter = new BufferedWriter(new FileWriter(logFile, StandardCharsets.UTF_8, true), FileUtil.BUFFER_SIZE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create log file writer", e);
        }
    }

    public synchronized void closeFileWriter() {
        if (fileWriter != null) {
            try {
                fileWriter.close();
                fileWriter = null;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to close log file writer", e);
            }
        }
    }

    protected synchronized void writeLog(String message) {
        if (!logConfig.isLogFileEnabled() || fileWriter == null) {
            return;
        }
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


    @Override
    public void debug(String message) {
        String debugMessage = "[DEBUG] " + message;
        logger.log(Level.INFO, debugMessage);
        writeLog(debugMessage);
    }

    @Override
    public void info(String message) {
        logger.info(message);
        writeLog("[INFO] " + message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
        writeLog("[WARNING] " + message);
    }

    @Override
    public void error(String message) {
        String errorMessage = "[ERROR] " + message;
        logger.severe(errorMessage);
        writeLog(errorMessage);
    }

    @Override
    public void error(String message, Throwable throwable) {
        String errorMessage = "[ERROR] " + message;
        logger.log(Level.SEVERE, errorMessage, throwable);
        writeLog(errorMessage + System.lineSeparator() + getStackTraceString(throwable));
    }

    private String getStackTraceString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @Override
    public Level getLevel() {
        return logger.getLevel();
    }

}
