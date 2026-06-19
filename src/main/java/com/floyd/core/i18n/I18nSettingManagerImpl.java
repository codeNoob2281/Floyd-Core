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
import com.floyd.core.common.util.FileUtil;
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

    protected final Map<String, SettingsManager> localeSettingsManagerMap;

    public I18nSettingManagerImpl(List<I18nMessageHolder> i18nMessageHolders) {
        this.i18nMessageHolders = i18nMessageHolders.stream()
                .map(I18nMessageHolder::getClass)
                .collect(Collectors.toList());
        this.buildInMessageResourceMap = new ConcurrentHashMap<>();
        this.localeSettingsManagerMap = new ConcurrentHashMap<>();
        scanMessageResources();
        initBuildInSettingsManagers();
        scanCustomMessageResources();
    }

    @Override
    public synchronized void reloadAll() {
        logger.info("Reload all i18n message config");
        scanCustomMessageResources();
        localeSettingsManagerMap.values()
                .forEach(SettingsManager::reload);
    }

    @Override
    public synchronized void reload(Locale locale) {
        logger.info("Reload i18n message config: {}", locale);
        scanCustomMessageResources();
        SettingsManager settingsManager = getSettingsManager(locale);
        if (settingsManager != null) {
            settingsManager.reload();
        }
    }

    @Override
    public @NotNull SettingsManager getSettingsManager(Locale locale) {
        if (locale == null) {
            return localeSettingsManagerMap.get(DEFAULT_LOCALE_NAME);
        }
        // Try locale with language+region first (e.g., zh_CN), then language only (e.g., zh), fallback to default
        SettingsManager settingsManager = localeSettingsManagerMap.get(getLocaleMappingKey(locale));
        if (settingsManager == null) {
            settingsManager = localeSettingsManagerMap.get(locale.getLanguage().toLowerCase());
        }
        if (settingsManager == null) {
            settingsManager = localeSettingsManagerMap.get(DEFAULT_LOCALE_NAME);
        }
        if (settingsManager == null) {
            throw new IllegalStateException("Required default locale settings manager not found: " + DEFAULT_LOCALE_NAME);
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

    protected void scanCustomMessageResources() {
        File languageDir = getLanguageConfigDir();
        if (!languageDir.exists() || !languageDir.isDirectory()) {
            return;
        }
        File[] files = languageDir.listFiles((dir, name) ->
                name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String localeName = FileUtil.getFileNameWithoutSuffix(file.getName()).toLowerCase();
            if (!localeSettingsManagerMap.containsKey(localeName)) {
                SettingsManager settingsManager = buildCustomSettingManager(file);
                localeSettingsManagerMap.put(localeName, settingsManager);
                logger.info("Loaded custom i18n locale from plugin directory: {}", localeName);
            }
        }
    }

    protected void initBuildInSettingsManagers() {
        buildInMessageResourceMap.forEach((localeName, resource) -> {
            SettingsManager settingsManager = buildBuildInSettingManager(resource);
            // Reload first to sync configs
            settingsManager.reload();
            localeSettingsManagerMap.put(localeName, settingsManager);
        });
    }

    @SneakyThrows
    protected SettingsManager buildBuildInSettingManager(Resource resource) {
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

        // Load StringProperty from i18nMessageHolders
        List<Property<?>> defaultProperties = ConfigurationDataBuilder.createConfiguration(i18nMessageHolders).getProperties();
        for (Property<?> defaultProperty : defaultProperties) {
            StringProperty stringProperty = (StringProperty) defaultProperty;
            stringPropertyMap.putIfAbsent(stringProperty.getPath(), stringProperty);
        }
        // Build configurationData
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(new ArrayList<>(stringPropertyMap.values()));

        // Get config PropertyResource
        File languageConfigDir = getLanguageConfigDir();
        if (!languageConfigDir.exists()) {
            languageConfigDir.mkdirs();
        }
        File configYamlFile = Path.of(languageConfigDir.toString(), Objects.requireNonNull(resource.getFilename())).toFile();
        // Create Empty file if not exist
        if (!configYamlFile.exists()) {
            configYamlFile.createNewFile();
        }
        PropertyResource propertyResource = new YamlFileResource(configYamlFile.toPath());


        // Build settingsManager
        SettingsManager settingsManager = SettingsManagerBuilder
                .withResource(propertyResource)
                .configurationData(configurationData)
                .migrationService(new PlainMigrationService())
                .create();

        if (!configYamlFile.exists()) {
            settingsManager.save();
        }
        return settingsManager;
    }

    protected SettingsManager buildCustomSettingManager(File file) {
        // Build configurationData
        ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(i18nMessageHolders);

        // Get config PropertyResource
        PropertyResource propertyResource = new YamlFileResource(file.toPath());

        // Build settingsManager
        return SettingsManagerBuilder
                .withResource(propertyResource)
                .configurationData(configurationData)
                .migrationService(new PlainMigrationService())
                .create();
    }

    protected File getLanguageConfigDir() {
        return Path.of(FloydPlugin.getPluginDataPath().toString(), "language").toFile();
    }

    protected String getLocaleMappingKey(Locale locale) {
        return locale.toString().toLowerCase();
    }
}
