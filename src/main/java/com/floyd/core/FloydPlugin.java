package com.floyd.core;

import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.DefaultConsoleLogger;
import com.floyd.core.logging.LogConfig;
import com.floyd.core.util.StrUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.*;

/**
 * @author floyd
 * @date 2026/3/22
 */
public abstract class FloydPlugin extends JavaPlugin {

    private static FloydPlugin floydPlugin;

    private static DefaultConsoleLogger consoleLogger;

    private static final String LOG_FILE_NAME = "mc-plugin.log";

    private static AnnotationConfigApplicationContext applicationContext;

    @Override
    public void onEnable() {
        printBanner();
        floydPlugin = this;
        // Initialize default config
        initConfig();
        // Initialize logger
        initConsoleLogger();
        // Initialize spring container
        initSpringApplication();
        // Custom plugin initialization logic
        initialize();
        getLogger().info("Thank you for using plugin: " + getPluginName());
        getLogger().info("Author: " + PluginConstants.AUTHOR);
    }

    @Override
    public void onDisable() {
        // Custom plugin disable logic
        cleanup();
        // Close spring application
        if (applicationContext != null) {
            getLogger().info("Closing spring application...");
            applicationContext.close();
        }
        // Close log file writer
        getLogger().info("Closing log file...");
        consoleLogger.closeFileWriter();
        getLogger().info(getPluginName() + " plugin has been disabled, thank you for using");
        getLogger().info("Author: " + PluginConstants.AUTHOR);
    }

    public static FloydPlugin instance() {
        return floydPlugin;
    }

    protected abstract void initialize();

    protected void initSpringApplication() {
        ClassLoader oriClassLoader = Thread.currentThread().getContextClassLoader();
        // Use current plugin's classloader to make sure spring application can initialize correctly
        Thread.currentThread().setContextClassLoader(getClassLoader());
        try {
            List<Class<?>> configs = new ArrayList<>();
            configs.add(SpringConfig.class);
            configs.addAll(getCustomConfigClasses());
            Class<?>[] configClasses = new Class<?>[configs.size()];
            configClasses = configs.toArray(configClasses);
            applicationContext = new AnnotationConfigApplicationContext(configClasses);
        } finally {
            Thread.currentThread().setContextClassLoader(oriClassLoader);
        }
    }

    protected List<Class<?>> getCustomConfigClasses() {
        return new ArrayList<>();
    }

    protected abstract void cleanup();

    protected void initConfig() {
        saveDefaultConfig();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected void initConsoleLogger() {
        LogConfig logConfig = new LogConfig();
        logConfig.setLogFileEnabled(getConfig().getBoolean("logging.file.enable", false));
        consoleLogger = new DefaultConsoleLogger(getLogger(), new File(getDataFolder(), LOG_FILE_NAME), logConfig);
    }

    public static ConsoleLogger logger() {
        if (consoleLogger == null) {
            throw new IllegalStateException("console logger is not initialized");
        }
        return consoleLogger;
    }

    public abstract String getPluginName();

    private void printBanner() {
        String banner = getBanner();
        if (banner != null && !banner.isBlank()) {
            String[] splitLines = banner.split("\n");
            for (String splitLine : splitLines) {
                getLogger().info(splitLine);
            }
        }
    }

    protected String getBanner() {
        return StrUtil.EMPTY;
    }
}
