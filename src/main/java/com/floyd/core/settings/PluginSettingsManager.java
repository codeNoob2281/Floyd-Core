package com.floyd.core.settings;

import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.resource.PropertyResource;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author floyd
 */
public class PluginSettingsManager extends SettingsManagerImpl {

    private static final Logger logger = ConsoleLoggerFactory.get(PluginSettingsManager.class);

    @Autowired
    @Lazy
    List<SettingsReloadAware> settingsReloadListeners;

    /**
     * Constructor. Use {@link SettingsManagerBuilder} to create instances.
     *
     * @param resource          the property resource to read from and write to
     * @param configurationData the configuration data
     * @param migrationService  migration service to check the property resource with
     */
    protected PluginSettingsManager(@NotNull PropertyResource resource,
                                    @NotNull ConfigurationData configurationData,
                                    @Nullable MigrationService migrationService) {
        super(resource, configurationData, migrationService);
        this.settingsReloadListeners = new ArrayList<>();
    }

    @Override
    public void reload() {
        super.reload();
        settingsReloadListeners.forEach(listener -> {
            logger.debug("Reloading settings for listener: {}", listener);
            listener.onSettingsReload(this);
        });
    }
}
