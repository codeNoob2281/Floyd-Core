package com.floyd.core.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TypeConverter unit tests
 */
class TypeConverterTest {

    @Test
    void testConvertString() {
        assertEquals("hello", TypeConverter.convert("hello", String.class));
    }

    @Test
    void testConvertInteger() {
        assertEquals(42, TypeConverter.convert("42", Integer.class));
        assertEquals(42, TypeConverter.convert("42", int.class));
    }

    @Test
    void testConvertLong() {
        assertEquals(123456789L, TypeConverter.convert("123456789", Long.class));
        assertEquals(123456789L, TypeConverter.convert("123456789", long.class));
    }

    @Test
    void testConvertDouble() {
        assertEquals(3.14, TypeConverter.convert("3.14", Double.class));
        assertEquals(3.14, TypeConverter.convert("3.14", double.class));
    }

    @Test
    void testConvertFloat() {
        assertEquals(3.14f, TypeConverter.convert("3.14", Float.class));
        assertEquals(3.14f, TypeConverter.convert("3.14", float.class));
    }

    @Test
    void testConvertBoolean() {
        assertTrue((Boolean) TypeConverter.convert("true", Boolean.class));
        assertTrue((Boolean) TypeConverter.convert("yes", Boolean.class));
        assertTrue((Boolean) TypeConverter.convert("on", Boolean.class));
        assertTrue((Boolean) TypeConverter.convert("1", Boolean.class));

        assertFalse((Boolean) TypeConverter.convert("false", Boolean.class));
        assertFalse((Boolean) TypeConverter.convert("no", Boolean.class));
        assertFalse((Boolean) TypeConverter.convert("off", Boolean.class));
        assertFalse((Boolean) TypeConverter.convert("0", Boolean.class));
    }

    @Test
    void testConvertBooleanPrimitive() {
        assertTrue((boolean) TypeConverter.convert("true", boolean.class));
        assertFalse((boolean) TypeConverter.convert("false", boolean.class));
    }

    @Test
    void testConvertEnum() {
        TestEnum result = (TestEnum) TypeConverter.convert("value1", TestEnum.class);
        assertEquals(TestEnum.VALUE1, result);
    }

    @Test
    void testConvertEnumCaseInsensitive() {
        TestEnum result = (TestEnum) TypeConverter.convert("VALUE2", TestEnum.class);
        assertEquals(TestEnum.VALUE2, result);
    }

    @Test
    void testConvertUUID() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, TypeConverter.convert(uuid.toString(), UUID.class));
    }

    @Test
    void testConvertNull() {
        assertNull(TypeConverter.convert(null, String.class));
        assertNull(TypeConverter.convert(null, Integer.class));
    }

    @Test
    void testConvertInvalidInteger() {
        assertThrows(TypeConversionException.class, () ->
                TypeConverter.convert("abc", Integer.class));
    }

    @Test
    void testConvertInvalidBoolean() {
        assertThrows(TypeConversionException.class, () ->
                TypeConverter.convert("maybe", Boolean.class));
    }

    @Test
    void testConvertInvalidEnum() {
        assertThrows(IllegalArgumentException.class, () ->
                TypeConverter.convert("invalid", TestEnum.class));
    }

    @Test
    void testConvertUnsupportedType() {
        assertThrows(TypeConversionException.class, () ->
                TypeConverter.convert("test", Object.class));
    }

    enum TestEnum {
        VALUE1, VALUE2
    }
}
