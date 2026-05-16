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

    protected final I18nSettingManager i18nSettingManager;

    @Setter
    @Getter
    private volatile Locale currentLocale;

    private final ReadWriteLock accessLock;


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
    }

    @Override
    public String getMessage(LocaleMessage localeMessage, Object... args) {
        Lock readLock = accessLock.readLock();
        readLock.lock();
        try {
            SettingsManager settingsManager = i18nSettingManager.getSettingsManager(currentLocale);
            String message = settingsManager.getProperty(localeMessage);
            if (args == null || args.length == 0) {
                return message;
            }
            return new MessageFormat(message).format(args);
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
            i18nSettingManager.reload(currentLocale);
        } finally {
            writeLock.unlock();
        }
    }
}
