package com.floyd.core.i18n;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

/**
 * @author floyd
 */
public class LocaleMessage {

    private final String code;

    private final String defaultMessage;

    protected static volatile I18nMessageProvider i18nMessageProvider;

    public LocaleMessage(@NotNull String code, @NotNull String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public @NotNull String content(Object... args) {
        if (i18nMessageProvider == null) {
            if (args.length == 0) {
                return defaultMessage;
            }
            MessageFormat format = new MessageFormat(defaultMessage);
            return format.format(args);
        }

        return i18nMessageProvider.getMessageOrDefault(code, defaultMessage, args);
    }

    public static void setI18nMessageProvider(I18nMessageProvider i18nMessageProvider) {
        LocaleMessage.i18nMessageProvider = i18nMessageProvider;
    }

    public static LocaleMessage of(String code, String defaultMessage) {
        return new LocaleMessage(code, defaultMessage);
    }
}
