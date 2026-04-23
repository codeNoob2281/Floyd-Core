package com.floyd.core.util;

/**
 * @author floyd
 */
public class StrUtil {

    public static final String EMPTY = "";

    public static final String SPACE = " ";

    /**
     * Check if string is empty
     *
     * @param str The string to check
     * @return true if empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Check if string is empty
     *
     * @param str The string to check
     * @return the string if not empty, empty string otherwise
     */
    public static String emptyIfNull(String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * Check if string is blank
     *
     * @param str The string to check
     * @return true if blank
     */
    public static boolean isBlank(String str) {
        return isEmpty(str) || str.isBlank();
    }

    /**
     * Return default value if string is blank
     *
     * @param str          The string to check
     * @param defaultValue The default value
     * @return the string if not blank, default value otherwise
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }
}
