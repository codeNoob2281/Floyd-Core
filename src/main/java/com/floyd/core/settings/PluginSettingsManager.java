package com.floyd.core.settings;

import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.resource.PropertyResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
public class PluginSettingsManager extends SettingsManagerImpl {

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
    }
}
