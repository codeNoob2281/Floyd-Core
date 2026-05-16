package com.floyd.core.i18n;

import ch.jalu.configme.SettingsManager;

import java.util.Locale;

/**
 * @author floyd
 */
public interface I18nSettingManager {

    /**
     * Reload all settings
     */
    void reloadAll();

    /**
     * Reload settings
     *
     * @param locale
     */
    void reload(Locale locale);

    /**
     * Get settings manager
     *
     * @param locale
     * @return
     */
    SettingsManager getSettingsManager(Locale locale);
}
