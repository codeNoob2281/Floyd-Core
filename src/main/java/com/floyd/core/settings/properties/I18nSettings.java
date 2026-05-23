package com.floyd.core.settings.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.properties.Property;
import com.floyd.core.settings.PluginSettingsHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author floyd
 */
@Component
public class I18nSettings implements PluginSettingsHolder {

    @Comment("Locale to use for internationalization")
    public static final Property<String> LOCALE = newProperty("i18n.locale", Locale.getDefault().toString());

}
