package com.floyd.core.i18n;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import com.floyd.core.FloydPlugin;
import com.floyd.core.PluginBizException;
import com.floyd.core.logging.ConsoleLoggerFactory;
import com.floyd.core.logging.Logger;
import com.floyd.core.util.FileUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author floyd
 */
public class I18nSettingManagerImpl implements I18nSettingManager {

    private static final Logger logger = ConsoleLoggerFactory.get(I18nSettingManagerImpl.class);

    protected static final String DEFAULT_LOCALE_NAME = "en";

    protected final List<Class<? extends SettingsHolder>> i18nMessageHolders;

    protected final Map<String, Resource> buildInMessageResourceMap;

    protected Map<String, SettingsManager> localeSettingsManagerMap;

    public I18nSettingManagerImpl(List<I18nMessageHolder> i18nMessageHolders) {
        this.i18nMessageHolders = i18nMessageHolders.stream()
                .map(I18nMessageHolder::getClass)
                .collect(Collectors.toList());
        this.buildInMessageResourceMap = new ConcurrentHashMap<>();
        this.localeSettingsManagerMap = new ConcurrentHashMap<>();
        scanMessageResources();
        initSettingsManagers();
    }

    @Override
    public synchronized void reloadAll() {
        logger.info("Reload all i18n message config");
        localeSettingsManagerMap.values()
                .forEach(SettingsManager::reload);
    }

    @Override
    public synchronized void reload(Locale locale) {
        logger.info("Reload i18n message config, current locale is {}", locale);
        SettingsManager settingsManager = localeSettingsManagerMap.get(getLocaleMappingKey(locale));
        if (settingsManager != null) {
            settingsManager.reload();
        }
    }

    @Override
    public @NotNull SettingsManager getSettingsManager(Locale locale) {
        if (locale == null) {
            return localeSettingsManagerMap.get(DEFAULT_LOCALE_NAME);
        }
        SettingsManager settingsManager = localeSettingsManagerMap.get(getLocaleMappingKey(locale));
        if (settingsManager == null) {
            settingsManager = localeSettingsManagerMap.get(DEFAULT_LOCALE_NAME);
        }
        return settingsManager;
    }

    protected void scanMessageResources() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:language/*");
            if (resources.length == 0) {
                logger.warn("No language file found");
                return;
            }
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName == null) {
                    continue;
                }
                String localeName = FileUtil.getFileNameWithoutSuffix(fileName).toLowerCase();
                buildInMessageResourceMap.put(localeName, resource);
            }
        } catch (IOException e) {
            throw new PluginBizException("Exception occurred while loading the built-in i18n resources", e);
        }
    }

    protected void initSettingsManagers() {
        buildInMessageResourceMap.forEach((localeName, resource) -> {
            SettingsManager settingsManager = buildSettingManager(resource);
            // Reload first to sync configs
            settingsManager.reload();
            localeSettingsManagerMap.put(localeName, settingsManager);
        });
    }

    @SneakyThrows
    protected SettingsManager buildSettingManager(Resource resource) {
        // Load properties
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        Properties properties = factoryBean.getObject();
        if (properties == null) {
            properties = new Properties();
        }

        // Build configurationData
        Map<String, StringProperty> stringPropertyMap = new HashMap<>();
        for (Object key : properties.keySet()) {
            String path = (String) key;
            String defaultValue = (String) properties.get(key);
            StringProperty stringProperty = new StringProperty(path, defaultValue);
            stringPropertyMap.put(path, stringProperty);
        }
        ConfigurationDataBuilder.createConfiguration(i18nMessageHolders);
        // Load StringProperty from i18nMessageHolders
        List<Property<?>> defaultProperties = ConfigurationDataBuilder.createConfiguration(i18nMessageHolders).getProperties();
        for (Property<?> defaultProperty : defaultProperties) {
            StringProperty stringProperty = (StringProperty) defaultProperty;
            stringPropertyMap.putIfAbsent(stringProperty.getPath(), stringProperty);
        }
        // Build configurationData
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(new ArrayList<>(stringPropertyMap.values()));

        // Get config PropertyResource
        File configYamlFile =
                Path.of(FloydPlugin.getPluginDataPath().toString(), "language", Objects.requireNonNull(resource.getFilename())).toFile();
        if (!configYamlFile.exists()) {
            configYamlFile.createNewFile();
        }
        PropertyResource propertyResource = new YamlFileResource(configYamlFile.toPath());

        // Build settingsManager
        return SettingsManagerBuilder
                .withResource(propertyResource)
                .configurationData(configurationData)
                .migrationService(new PlainMigrationService())
                .create();
    }

    protected String getLocaleMappingKey(Locale locale) {
        return locale.toString().toLowerCase();
    }
}
