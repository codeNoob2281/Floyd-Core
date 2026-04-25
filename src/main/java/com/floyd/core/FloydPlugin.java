package com.floyd.core;

import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.settings.PluginSettingsManager;
import com.floyd.core.util.StrUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author floyd
 */
public abstract class FloydPlugin extends JavaPlugin {

    private static volatile FloydPlugin floydPlugin;

    private static final ConsoleLogger logger = ConsoleLoggerFactory.get(FloydPlugin.class);

    private static final String LOG_FILE_NAME = "mc-plugin.log";

    private static AnnotationConfigApplicationContext applicationContext;

    @Override
    public void onEnable() {
        setPluginInstance(this);
        // Initialize default config
        initConfig();
        // Initialize spring container
        initSpringApplication();
        // Initialize logger
        initConsoleLogger();
        // Custom plugin initialization logic
        initialize();
        printBanner();
        logger.info("Thank you for using plugin: " + getPluginName());
        logger.info("Author: " + PluginConstants.AUTHOR);
    }

    /**
     * Set the plugin instance. Use double check lock to avoid multiple initialization.
     *
     * @param instance The plugin instance
     */
    private static void setPluginInstance(FloydPlugin instance) {
        if (floydPlugin == null) {
            synchronized (FloydPlugin.class) {
                if (floydPlugin == null) {
                    floydPlugin = instance;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Custom plugin disable logic
        cleanup();
        // Close spring application
        if (applicationContext != null) {
            logger.info("Closing spring application...");
            applicationContext.close();
        }
        // Close log file writer
        logger.info("Closing log file...");
        ConsoleLogger.closeFileWriter();
        logger.info(getPluginName() + " plugin has been disabled, thank you for using");
        logger.info("Author: " + PluginConstants.AUTHOR);
    }

    /**
     * Get the initialized plugin instance.
     *
     * @return The instance of the plugin
     */
    public static FloydPlugin instance() {
        if (floydPlugin == null) {
            throw new IllegalStateException("fail to get the plugin instance, please make sure the plugin is initialized");
        }
        return floydPlugin;
    }

    /**
     * Get the plugin data path.
     *
     * @return The plugin data path
     */
    public static Path getPluginDataPath() {
        return instance().getDataFolder().toPath();
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
        if (applicationContext == null) {
            throw new IllegalStateException("fail to get spring application context, please make sure the plugin is initialized");
        }
        return applicationContext;
    }

    protected void initConsoleLogger() {
        ConsoleLogger.initializeFirst(getLogger(), new File(getDataFolder(), LOG_FILE_NAME));
        PluginSettingsManager pluginSettingsManager = getApplicationContext().getBean(PluginSettingsManager.class);
        ConsoleLoggerFactory.reloadFromConfig(pluginSettingsManager);
    }

    public abstract String getPluginName();

    private void printBanner() {
        String banner = getBanner();
        if (banner != null && !banner.isBlank()) {
            String[] splitLines = banner.split("\n");
            for (String splitLine : splitLines) {
                logger.info(splitLine);
            }
        }
    }

    protected String getBanner() {
        return StrUtil.EMPTY;
    }
}
