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
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Check if string is empty
     *
     * @param str
     * @return
     */
    public static String emptyIfNull(String str) {
        return str == null ? EMPTY : str;
    }
}
