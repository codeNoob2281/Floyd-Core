package com.floyd.core.i18n;

import ch.jalu.configme.SettingsManager;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import com.floyd.core.settings.PluginSettingsManager;
import com.floyd.core.settings.SettingsReloadAware;
import com.floyd.core.settings.properties.I18nSettings;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author floyd
 */
public class DefaultI18nMessageProvider implements I18nMessageProvider, SettingsReloadAware {

    private static final Logger logger = ConsoleLoggerFactory.get(DefaultI18nMessageProvider.class);

    protected static final int DEFAULT_EXPIRE_TIME = 60;

    protected final I18nSettingManager i18nSettingManager;

    @Setter
    @Getter
    private volatile Locale currentLocale;

    private final ReadWriteLock accessLock;

    // todo support cache
    protected boolean cacheEnable;

    private int cacheTtlSeconds = DEFAULT_EXPIRE_TIME;


    public DefaultI18nMessageProvider(I18nSettingManager i18nSettingManager) {
        this.i18nSettingManager = i18nSettingManager;
        this.accessLock = new ReentrantReadWriteLock();
    }

    /**
     * Refresh message source
     *
     * @param settingsManager The settings manager
     */
    protected void updateCurrentLocale(PluginSettingsManager settingsManager) {
        currentLocale = Locale.of(settingsManager.getProperty(I18nSettings.LOCALE));
        logger.info("The config is reload. Current I18n message locale: {}", currentLocale);
    }

    /**
     * Refresh message source cache config
     *
     * @param settingsManager The settings manager
     */
    protected void updateCacheConfig(PluginSettingsManager settingsManager) {
        Boolean enable = settingsManager.getProperty(I18nSettings.Cache.ENABLE);
        int expireTime = settingsManager.getProperty(I18nSettings.Cache.EXPIRE_TIME);
        if (enable) {
            if (expireTime <= 0) {
                logger.warn("I18n message cache expire time must be greater than 0 but got {}. Use default value {} instead.",
                        expireTime, DEFAULT_EXPIRE_TIME);
                expireTime = DEFAULT_EXPIRE_TIME;
            }
        }
        this.cacheEnable = enable;
        this.cacheTtlSeconds = expireTime;
    }


    @Override
    public String getMessage(LocaleMessage localeMessage, Object... args) {
        Lock readLock = accessLock.readLock();
        readLock.lock();
        try {
            SettingsManager settingsManager = i18nSettingManager.getSettingsManager(currentLocale);
            String message = settingsManager.getProperty(localeMessage);
            MessageFormat messageFormat = new MessageFormat(message);
            return messageFormat.format(args);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void onSettingsReload(PluginSettingsManager settingsManager) {
        Lock writeLock = accessLock.writeLock();
        writeLock.lock();
        try {
            updateCurrentLocale(settingsManager);
            updateCacheConfig(settingsManager);
            i18nSettingManager.reload(currentLocale);
        } finally {
            writeLock.unlock();
        }
    }
}
