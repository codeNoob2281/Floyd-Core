package com.floyd.core.util;

import com.floyd.core.BaseTest;
import com.floyd.core.common.util.StrUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StrUtil unit tests
 *
 * @author floyd
 */
class StrUtilTest extends BaseTest {

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

    // Tests for format method
    @Test
    void testFormat_WithSimpleMessage() {
        String format = "Hello, {}!";
        String result = StrUtil.format(format, "World");
        
        assertEquals("Hello, World!", result);
    }

    @Test
    void testFormat_WithMultipleArguments() {
        String format = "{} is {} years old";
        String result = StrUtil.format(format, "Alice", 25);
        
        assertEquals("Alice is 25 years old", result);
    }

    @Test
    void testFormat_WithNoArguments() {
        String format = "Hello, World!";
        String result = StrUtil.format(format);
        
        assertEquals("Hello, World!", result);
    }

    @Test
    void testFormat_WithNullArgument() {
        String format = "Value: {}";
        String result = StrUtil.format(format, (Object) null);
        
        assertEquals("Value: null", result);
    }

    @Test
    void testFormat_WithEmptyFormat() {
        String format = "";
        String result = StrUtil.format(format, "arg");
        
        assertEquals("", result);
    }

    @Test
    void testFormat_WithNullFormat() {
        String result = StrUtil.format(null, "arg");
        
        assertNull(result);
    }

    @Test
    void testFormat_WithThrowable() {
        String format = "Error occurred: {}";
        Exception exception = new RuntimeException("Test exception");
        String result = StrUtil.format(format, "test error", exception);
        
        assertTrue(result.contains("Error occurred: test error"));
        assertTrue(result.contains("java.lang.RuntimeException: Test exception"));
        assertTrue(result.contains("at com.floyd.core.util.StrUtilTest"));
    }

    @Test
    void testFormat_WithThrowableAndNoPlaceholders() {
        String format = "Error";
        Exception exception = new IllegalStateException("Test error");
        String result = StrUtil.format(format, exception);
        
        assertTrue(result.contains("Error"));
        assertTrue(result.contains("java.lang.IllegalStateException: Test error"));
    }

    @Test
    void testFormat_WithSpecialCharacters() {
        String format = "Message: {} {}";
        String result = StrUtil.format(format, "Hello", "World!");
        
        assertEquals("Message: Hello World!", result);
    }

    @Test
    void testFormat_WithNumericArguments() {
        String format = "Pi is approximately {}";
        String result = StrUtil.format(format, 3.14159);
        
        assertEquals("Pi is approximately 3.14159", result);
    }

    @Test
    void testFormat_WithBooleanArguments() {
        String format = "Result: {} and {}";
        String result = StrUtil.format(format, true, false);
        
        assertEquals("Result: true and false", result);
    }

    @Test
    void testFormat_WithEscapedBraces() {
        //String format = "Use \\{} to escape braces: {}";
        //String result = StrUtil.format(format, "value");
        //
        //assertEquals("Use {} to escape braces: value", result);
        String alice = StrUtil.format("Hi \\{}. My name is {}.", "Alice");
        assertEquals("Hi {}. My name is Alice.", alice);
    }

    // Tests for getStackTraceString method
    @Test
    void testGetStackTraceString_WithRuntimeException() {
        Exception exception = new RuntimeException("Test exception");
        String result = StrUtil.getStackTraceString(exception);
        
        assertNotNull(result);
        assertTrue(result.contains("java.lang.RuntimeException: Test exception"));
        assertTrue(result.contains("at com.floyd.core.util.StrUtilTest"));
    }

    @Test
    void testGetStackTraceString_WithCheckedException() {
        Exception exception = new java.io.IOException("IO error");
        String result = StrUtil.getStackTraceString(exception);
        
        assertNotNull(result);
        assertTrue(result.contains("java.io.IOException: IO error"));
    }

    @Test
    void testGetStackTraceString_WithCustomException() {
        Exception exception = new CustomException("Custom error message");
        String result = StrUtil.getStackTraceString(exception);
        
        assertNotNull(result);
        assertTrue(result.contains("CustomException: Custom error message"));
    }

    @Test
    void testGetStackTraceString_WithNestedException() {
        Exception cause = new IllegalArgumentException("Cause exception");
        Exception exception = new RuntimeException("Outer exception", cause);
        String result = StrUtil.getStackTraceString(exception);
        
        assertNotNull(result);
        assertTrue(result.contains("java.lang.RuntimeException: Outer exception"));
        assertTrue(result.contains("Caused by: java.lang.IllegalArgumentException: Cause exception"));
    }

    @Test
    void testGetStackTraceString_WithNullException() {
        assertNull(StrUtil.getStackTraceString(null));
    }

    @Test
    void testGetStackTraceString_ContainsLineNumber() {
        Exception exception = new RuntimeException("Test");
        String result = StrUtil.getStackTraceString(exception);

        assertNotNull(result);
        assertTrue(result.matches("(?s).*at com\\.floyd\\.core\\.util\\.StrUtilTest\\.testGetStackTraceString_ContainsLineNumber.*"));
    }

    @Test
    void testGetStackTraceString_MultipleStackFrames() {
        Exception exception = createExceptionWithDeepStack();
        String result = StrUtil.getStackTraceString(exception);
        
        assertNotNull(result);
        assertTrue(result.contains("createExceptionWithDeepStack"));
        assertTrue(result.contains("testGetStackTraceString_MultipleStackFrames"));
    }

    // Helper method for nested exception test
    private Exception createExceptionWithDeepStack() {
        return new RuntimeException("Deep stack exception");
    }

    // Custom exception class for testing
    static class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }
}
