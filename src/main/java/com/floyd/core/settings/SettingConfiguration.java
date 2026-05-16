package com.floyd.core.settings;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.PlainMigrationService;
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
    PluginSettingsManager pluginSettingsManager(List<PluginSettingsHolder> pluginSettingsHolders) {
        Path configPath = Paths.get(FloydPlugin.getPluginDataPath().toString(), "config.yml");
        YamlFileResource resource = new YamlFileResource(configPath);
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(pluginSettingsHolders.stream()
                .map(SettingsHolder::getClass)
                .collect(Collectors.toSet()));
        PlainMigrationService migrationService = new PlainMigrationService();
        return new PluginSettingsManager(resource, configurationData, migrationService);
    }

}
