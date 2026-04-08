package com.floyd.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date util
 *
 * @author floyd
 */
public class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String PURE_DATE_FORMAT = "yyyyMMdd";
    public static final String PURE_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * Format date to date string
     *
     * @param date the date
     * @return date string
     */
    public static String formatDate(Date date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }


    /**
     * Format date to date time string
     *
     * @param date the date
     * @return date time string
     */
    public static String formatDateTime(Date date) {
        return format(date, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * Format date to date string
     *
     * @param date    the date
     * @param pattern date pattern
     * @return date string
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

}
