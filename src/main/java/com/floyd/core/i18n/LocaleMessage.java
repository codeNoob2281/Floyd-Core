package com.floyd.core.i18n;

import ch.jalu.configme.properties.StringProperty;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

/**
 * @author floyd
 */
public class LocaleMessage extends StringProperty {

    protected static volatile I18nMessageProvider i18nMessageProvider;

    public LocaleMessage(@NotNull String path, @NotNull String defaultMessage) {
        super(path, defaultMessage);
    }

    public @NotNull String content(Object... args) {
        if (i18nMessageProvider == null) {
            if (args.length == 0) {
                return getDefaultValue();
            }
            MessageFormat format = new MessageFormat(getDefaultValue());
            return format.format(args);
        }

        return i18nMessageProvider.getMessage(this, args);
    }

    public static void setI18nMessageProvider(I18nMessageProvider i18nMessageProvider) {
        LocaleMessage.i18nMessageProvider = i18nMessageProvider;
    }

    /**
     * Create a LocaleMessage
     *
     * @param path           The message path
     * @param defaultMessage The default message
     * @return The LocaleMessage
     */
    public static LocaleMessage of(String path, String defaultMessage) {
        return new LocaleMessage(path, defaultMessage);
    }
}
