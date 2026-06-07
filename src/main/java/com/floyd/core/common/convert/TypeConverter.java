package com.floyd.core.common.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * Command argument type converter
 *
 * @author floyd
 */
public class TypeConverter {

    private static final DefaultTypeConvertProvider defaultTypeConverterProvider = new DefaultTypeConvertProvider();

    private static final List<TypeConvertProvider> customTypeConvertProviders = new ArrayList<>();

    /**
     * Converts a string value to the target type.
     *
     * @param value      the original string value
     * @param targetType the target type
     * @return the converted object
     * @throws TypeConversionException if conversion fails
     */
    public static Object convert(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // Try custom type converter providers
        for (TypeConvertProvider customProvider : customTypeConvertProviders) {
            if (!customProvider.support(targetType)) {
                continue;
            }
            return customProvider.convert(value, targetType);
        }

        // Use default type converter provider
        if (defaultTypeConverterProvider.support(targetType)) {
            return defaultTypeConverterProvider.convert(value, targetType);
        }

        throw new TypeConversionException(
                "Unsupported type conversion: String → " + targetType.getSimpleName());
    }

    /**
     * Adds a custom type converter provider.
     *
     * @param provider the custom type converter provider
     */
    public static void addProvider(TypeConvertProvider provider) {
        if (!customTypeConvertProviders.contains(provider)) {
            customTypeConvertProviders.add(provider);
        }
    }

}
