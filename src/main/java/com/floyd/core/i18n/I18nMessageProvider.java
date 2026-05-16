package com.floyd.core.i18n;

/**
 * @author floyd
 */
public interface I18nMessageProvider {

    /**
     * Get message
     *
     * @param localeMessage The localeMessage
     * @param args          The message args
     * @return
     */
    String getMessage(LocaleMessage localeMessage, Object... args);
}
