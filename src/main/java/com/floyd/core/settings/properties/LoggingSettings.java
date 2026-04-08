package com.floyd.core.settings.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.floyd.core.logging.LogLevel;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author floyd
 */
@Component
public class LoggingSettings implements SettingsHolder {

    @Comment("Enable logging to a file")
    public static final Property<Boolean> ENABLE_LOGGING_FILE = newProperty("logging.file.enable", true);

    @Comment("Log level")
    public static final Property<LogLevel> LEVEL = newProperty(LogLevel.class, "logging.level", LogLevel.INFO);
}
