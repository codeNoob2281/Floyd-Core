package com.floyd.core.common.convert;

/**
 * Type converter provider interface
 *
 * @author floyd
 */
public interface TypeConvertProvider {

    /**
     * Check whether the provider supports the target type
     *
     * @param targetType
     * @return
     */
    boolean support(Class<?> targetType);

    /**
     * Convert string value to target type
     *
     * @param value      the original string value
     * @param targetType the target type
     * @return
     */
    Object convert(String value, Class<?> targetType);
}
