package com.floyd.core.logging;

import com.floyd.core.settings.PluginSettingsManager;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author floyd
 */
public class ConsoleLoggerFactory {

    private static final Map<Class<?>, ConsoleLogger> LOGGER_MAP = new ConcurrentHashMap<>();

    private static PluginSettingsManager settingsManager;


    public static ConsoleLogger get(Class<?> clazz) {
        return LOGGER_MAP.computeIfAbsent(clazz, clazz1 -> {
            ConsoleLogger logger = new ConsoleLogger(clazz.getName());
            if (settingsManager != null) {
                logger.initIndividualConfig(settingsManager);
            }
            return logger;
        });
    }

    public static void reloadFromConfig(PluginSettingsManager settings) {
        settingsManager = settings;
        if (settingsManager != null) {
            ConsoleLogger.initSharedConfig(settings);
            LOGGER_MAP.values().forEach(logger -> logger.initIndividualConfig(settings));
        }
    }

}
