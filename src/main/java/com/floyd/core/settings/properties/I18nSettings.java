package com.floyd.core.settings.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import org.springframework.stereotype.Component;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author floyd
 */
@Component
public class I18nSettings implements SettingsHolder {

    @Comment("Locale to use for internationalization")
    public static final Property<String> LOCALE = newProperty("i18n.locale", "en");

    @Component
    public static class Cache implements SettingsHolder {

        @Comment("Enable i18n message cache")
        public static final Property<Boolean> ENABLE = newProperty("i18n.cache.enable", true);

        @Comment("Cache expire time in seconds")
        public static final Property<Integer> EXPIRE_TIME = newProperty("i18n.cache.expire-time", 60);
    }

}
