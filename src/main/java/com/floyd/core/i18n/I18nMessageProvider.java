package com.floyd.core.i18n;

/**
 * @author floyd
 */
public interface I18nMessageProvider {

    /**
     * Get message
     *
     * @param code The message code
     * @param args The message args
     * @return
     */
    String getMessage(String code, Object... args);

    /**
     * Get message, if not exists, return default message
     *
     * @param code           The message code
     * @param defaultMessage The default message
     * @param args           The  message args
     * @return
     */
    String getMessageOrDefault(String code, String defaultMessage, Object... args);
}
