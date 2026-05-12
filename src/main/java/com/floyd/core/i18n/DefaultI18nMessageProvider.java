package com.floyd.core.i18n;

import com.floyd.core.FloydPlugin;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import com.floyd.core.settings.PluginSettingsManager;
import com.floyd.core.settings.SettingsReloadAware;
import com.floyd.core.settings.properties.I18nSettings;
import org.springframework.core.io.FileSystemResourceLoader;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author floyd
 */
public class DefaultI18nMessageProvider implements I18nMessageProvider, SettingsReloadAware {

    private static final Logger logger = ConsoleLoggerFactory.get(DefaultI18nMessageProvider.class);

    private final YmlSupportedMessageSource messageSource;

    private volatile Locale currentLocale;

    private final ReadWriteLock accessLock;

    public static final int DEFAULT_EXPIRE_TIME = 60;


    public DefaultI18nMessageProvider(PluginSettingsManager settingsManager) {
        this.messageSource = new YmlSupportedMessageSource();
        this.accessLock = new ReentrantReadWriteLock();
        initMessageSource();
        refreshMessageSource(settingsManager);
    }

    /**
     * Init message source
     */
    protected void initMessageSource() {
        // Load from file system path
        String fileSystemPathBaseName = Paths.get(FloydPlugin.getPluginDataPath().toString(), "language").toString();
        messageSource.setBasenames(fileSystemPathBaseName + "/");
        messageSource.setResourceLoader(new FileSystemResourceLoader());
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setFallbackToSystemLocale(false);
    }

    /**
     * Refresh message source
     *
     * @param settingsManager The settings manager
     */
    protected void refreshMessageSource(PluginSettingsManager settingsManager) {
        currentLocale = Locale.of(settingsManager.getProperty(I18nSettings.LOCALE));
        logger.info("The config is reload. Current I18n message locale: {}", currentLocale);
        refreshMessageSourceCacheConfig(settingsManager);
    }

    /**
     * Refresh message source cache config
     *
     * @param settingsManager The settings manager
     */
    protected void refreshMessageSourceCacheConfig(PluginSettingsManager settingsManager) {
        Boolean enable = settingsManager.getProperty(I18nSettings.Cache.ENABLE);
        int expireTime = settingsManager.getProperty(I18nSettings.Cache.EXPIRE_TIME);
        if (enable) {
            if (expireTime <= 0) {
                logger.warn("I18n message cache expire time must be greater than 0 but got {}. Use default value {} instead.",
                        expireTime, DEFAULT_EXPIRE_TIME);
                expireTime = DEFAULT_EXPIRE_TIME;
            }
            messageSource.setCacheSeconds(expireTime);
        } else {
            messageSource.setCacheSeconds(0);
        }
    }


    @Override
    public String getMessage(String code, Object... args) {
        Lock readLock = accessLock.readLock();
        readLock.lock();
        try {
            return messageSource.getMessage(code, args, currentLocale);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String getMessageOrDefault(String code, String defaultMessage, Object... args) {
        Lock readLock = accessLock.readLock();
        readLock.lock();
        try {
            return messageSource.getMessage(code, args, defaultMessage, currentLocale);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void onSettingsReload(PluginSettingsManager settingsManager) {
        Lock writeLock = accessLock.writeLock();
        writeLock.lock();
        try {
            refreshMessageSource(settingsManager);
        } finally {
            writeLock.unlock();
        }
    }
}
