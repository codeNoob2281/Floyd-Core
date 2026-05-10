package com.floyd.core.util;

import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    /**
     * Format message
     * <p>
     * For example,
     * <pre>
     * // will return the string "Hi Alice. My name is Bob.".
     * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
     * </pre>
     * </p>
     * <p>
     * Use \{} to escape braces: {}. For example,
     * <pre>
     * // will return the string "Hi {}. My name is Alice.".
     * MessageFormatter.format(&quot;Hi \\{}. My name is {}.&quot;, &quot;Alice&quot;);
     * </pre>
     *
     *
     * </p>
     *
     * @param format The message template
     * @param args   The message arguments
     * @return The Formatted message
     */
    public static String format(String format, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, args);
        Throwable throwable = formattingTuple.getThrowable();
        String message = formattingTuple.getMessage();
        if (throwable != null) {
            return message + System.lineSeparator() + getStackTraceString(throwable);
        } else {
            return message;
        }
    }


    /**
     * Get stack trace string
     *
     * @param throwable The throwable
     * @return The stack trace string
     */
    @Nullable
    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
