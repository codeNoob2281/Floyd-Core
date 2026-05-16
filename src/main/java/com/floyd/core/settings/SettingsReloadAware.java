package com.floyd.core.settings;

/**
 * Listen for setting reloads
 *
 * @author floyd
 */
public interface SettingsReloadAware {

    /**
     * Called when settings are reloaded
     *
     * @param settingsManager settings manager
     */
    void onSettingsReload(PluginSettingsManager settingsManager);
}
