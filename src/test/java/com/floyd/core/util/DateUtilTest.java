package com.floyd.core.util;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateUtil unit tests
 *
 * @author floyd
 */
class DateUtilTest {

    @Test
    void testFormatDate_WithValidDate() {
        Date date = new Date(126, 3, 4, 10, 30, 45); // 2026-04-04 10:30:45
        
        String result = DateUtil.formatDate(date);
        
        assertEquals("2026-04-04", result, "date should be formatted as yyyy-MM-dd");
    }

    @Test
    void testFormatDate_WithEpochStart() {
        Date date = new Date(0); // 1970-01-01 00:00:00 UTC
        
        String result = DateUtil.formatDate(date);
        
        assertNotNull(result, "result should not be null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"), "result should match yyyy-MM-dd format");
    }

    @Test
    void testFormatDate_WithYearStart() {
        Date date = new Date(126, 0, 1, 0, 0, 0); // 2026-01-01 00:00:00
        
        String result = DateUtil.formatDate(date);
        
        assertEquals("2026-01-01", result, "year start date should be formatted correctly");
    }

    @Test
    void testFormatDate_WithYearEnd() {
        Date date = new Date(126, 11, 31, 23, 59, 59); // 2026-12-31 23:59:59
        
        String result = DateUtil.formatDate(date);
        
        assertEquals("2026-12-31", result, "year end date should be formatted correctly");
    }

    @Test
    void testFormatDate_WithSingleDigitMonthAndDay() {
        Date date = new Date(126, 0, 5, 8, 15, 30); // 2026-01-05 08:15:30
        
        String result = DateUtil.formatDate(date);
        
        assertEquals("2026-01-05", result, "single digit month and day should be zero-padded");
    }

    @Test
    void testFormatDateTime_WithValidDate() {
        Date date = new Date(126, 3, 4, 10, 30, 45); // 2026-04-04 10:30:45
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-04-04 10:30:45", result, "date should be formatted as yyyy-MM-dd HH:mm:ss");
    }

    @Test
    void testFormatDateTime_WithEpochStart() {
        Date date = new Date(0); // 1970-01-01 00:00:00 UTC
        
        String result = DateUtil.formatDateTime(date);
        
        assertNotNull(result, "result should not be null");
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), 
                "result should match yyyy-MM-dd HH:mm:ss format");
    }

    @Test
    void testFormatDateTime_WithMidnight() {
        Date date = new Date(126, 5, 15, 0, 0, 0); // 2026-06-15 00:00:00
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-06-15 00:00:00", result, "midnight time should be formatted correctly");
    }

    @Test
    void testFormatDateTime_WithNoon() {
        Date date = new Date(126, 5, 15, 12, 0, 0); // 2026-06-15 12:00:00
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-06-15 12:00:00", result, "noon time should be formatted correctly");
    }

    @Test
    void testFormatDateTime_WithEndOfDay() {
        Date date = new Date(126, 5, 15, 23, 59, 59); // 2026-06-15 23:59:59
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-06-15 23:59:59", result, "end of day time should be formatted correctly");
    }

    @Test
    void testFormatDateTime_WithSingleDigitValues() {
        Date date = new Date(126, 0, 1, 1, 2, 3); // 2026-01-01 01:02:03
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-01-01 01:02:03", result, "single digit values should be zero-padded");
    }

    @Test
    void testFormatDateTime_WithAfternoonTime() {
        Date date = new Date(126, 8, 20, 14, 30, 15); // 2026-09-20 14:30:15
        
        String result = DateUtil.formatDateTime(date);
        
        assertEquals("2026-09-20 14:30:15", result, "afternoon time should be formatted in 24-hour format");
    }

    @Test
    void testFormatDate_DefaultDateFormatConstant() {
        assertEquals("yyyy-MM-dd", DateUtil.DEFAULT_DATE_FORMAT, 
                "DEFAULT_DATE_FORMAT should be yyyy-MM-dd");
    }

    @Test
    void testFormatDateTime_DefaultDateTimeFormatConstant() {
        assertEquals("yyyy-MM-dd HH:mm:ss", DateUtil.DEFAULT_DATE_TIME_FORMAT, 
                "DEFAULT_DATE_TIME_FORMAT should be yyyy-MM-dd HH:mm:ss");
    }
}
