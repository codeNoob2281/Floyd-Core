package com.floyd.core.common.convert;

import java.util.UUID;

/**
 * The default type converter provider
 *
 * @author floyd
 */
public class DefaultTypeConvertProvider implements TypeConvertProvider {

    @Override
    public boolean support(Class<?> targetType) {
        return true;
    }

    @Override
    public Object convert(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // String — return directly
        if (targetType == String.class) {
            return value;
        }

        try {
            // Integer / int
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(value);
            }

            // Long / long
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(value);
            }

            // Double / double
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(value);
            }

            // Float / float
            if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(value);
            }
        } catch (NumberFormatException e) {
            throw new TypeConversionException(
                    "Cannot convert '" + value + "' to " + targetType.getSimpleName(), e);
        }

        // Boolean / boolean — supports true/false/yes/no/on/off
        if (targetType == Boolean.class || targetType == boolean.class) {
            return parseBoolean(value);
        }

        // Enum
        if (targetType.isEnum()) {
            return parseEnum(targetType, value);
        }

        // UUID
        if (targetType == UUID.class) {
            return UUID.fromString(value);
        }

        throw new TypeConversionException(
                "Unsupported type conversion: String → " + targetType.getSimpleName());
    }


    private static boolean parseBoolean(String value) {
        return switch (value.toLowerCase()) {
            case "true", "yes", "on", "1" -> true;
            case "false", "no", "off", "0" -> false;
            default -> throw new TypeConversionException(
                    "Cannot convert '" + value + "' to boolean");
        };
    }

    private static Enum<?> parseEnum(Class<?> enumClass, String value) {
        if (!enumClass.isEnum()) {
            throw new TypeConversionException(
                    "target class " + enumClass.getName() + " is not enum");
        }

        for (Object enumItem : enumClass.getEnumConstants()) {
            Enum<?> enumConstant = (Enum<?>) enumItem;
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }
        throw new TypeConversionException(
                "Cannot convert '" + value + "' to " + enumClass.getName());
    }
}
