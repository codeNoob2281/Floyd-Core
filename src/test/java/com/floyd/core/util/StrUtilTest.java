package com.floyd.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StrUtil unit tests
 *
 * @author floyd
 */
class StrUtilTest {

    @Test
    void testIsEmpty_WithNullString() {
        String nullString = null;
        
        boolean result = StrUtil.isEmpty(nullString);
        
        assertTrue(result, "null string should be considered empty");
    }

    @Test
    void testIsEmpty_WithEmptyString() {
        String emptyString = "";
        
        boolean result = StrUtil.isEmpty(emptyString);
        
        assertTrue(result, "empty string should be considered empty");
    }

    @Test
    void testIsEmpty_WithNonEmptyString() {
        String nonEmptyString = "Hello";
        
        boolean result = StrUtil.isEmpty(nonEmptyString);
        
        assertFalse(result, "non-empty string should not be considered empty");
    }

    @Test
    void testIsEmpty_WithStringWithSpaces() {
        String stringWithSpaces = "   ";
        
        boolean result = StrUtil.isEmpty(stringWithSpaces);
        
        assertFalse(result, "string with spaces should not be considered empty");
    }

    @Test
    void testIsEmpty_WithStringWithSpecialCharacters() {
        String specialString = "!@#$%^&*()";
        
        boolean result = StrUtil.isEmpty(specialString);
        
        assertFalse(result, "string with special characters should not be considered empty");
    }

    @Test
    void testIsEmpty_WithStringWithWhitespace() {
        String whitespaceString = " \t\n\r";
        
        boolean result = StrUtil.isEmpty(whitespaceString);
        
        assertFalse(result, "string with whitespace characters should not be considered empty");
    }

    @Test
    void testIsEmpty_WithSingleCharacterString() {
        String singleCharString = "a";
        
        boolean result = StrUtil.isEmpty(singleCharString);
        
        assertFalse(result, "single character string should not be considered empty");
    }

    @Test
    void testIsEmpty_WithNumericString() {
        String numericString = "12345";
        
        boolean result = StrUtil.isEmpty(numericString);
        
        assertFalse(result, "numeric string should not be considered empty");
    }
}
