package com.floyd.core.settings;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import com.floyd.core.FloydPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author floyd
 */
@Configuration
public class SettingConfiguration {

    @Bean
    PluginSettingsManager pluginSettingsManager(PropertyResource resource,
                                                ConfigurationData configurationData,
                                                MigrationService migrationService) {
        return new PluginSettingsManager(resource, configurationData, migrationService);
    }

    @Bean
    PropertyResource propertyResource() {
        FloydPlugin instance = FloydPlugin.instance();
        Path configPath = Paths.get(instance.getDataFolder().getPath(), "config.yml");
        return new YamlFileResource(configPath);
    }

    @Bean
    public ConfigurationData configurationData(List<SettingsHolder> settingsHolders) {
        return ConfigurationDataBuilder.createConfiguration(settingsHolders.stream()
                .map(SettingsHolder::getClass)
                .collect(Collectors.toSet()));
    }

    @Bean
    public MigrationService migrationService() {
        return new PlainMigrationService();
    }

}
