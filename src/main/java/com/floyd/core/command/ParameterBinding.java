package com.floyd.core.command;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Parameter binding metadata that describes how a method parameter is bound from command arguments.
 * Immutable object, constructed via Builder.
 *
 * @author floyd
 */
@Getter
public class ParameterBinding {

    private final int parameterIndex;
    private final BindingType bindingType;

    // === SINGLE_PARAM ===
    private final int argIndex;
    private final Class<?> targetType;
    private final String description;
    private final boolean required;
    private final String defaultValue;

    // === BODY ===
    private final Class<?> bodyType;

    private ParameterBinding(Builder builder) {
        this.parameterIndex = builder.parameterIndex;
        this.bindingType = builder.bindingType;
        this.argIndex = builder.argIndex;
        this.targetType = builder.targetType;
        this.description = builder.description;
        this.required = builder.required;
        this.defaultValue = builder.defaultValue;
        this.bodyType = builder.bodyType;
    }

    // ==================== Builder ====================

    public static Builder builder(BindingType bindingType) {
        return new Builder(bindingType);
    }

    public static class Builder {
        private final BindingType bindingType;
        private int parameterIndex;

        // SINGLE_PARAM fields
        private int argIndex = -1;
        private Class<?> targetType;
        private String description = "";
        private boolean required = true;
        private String defaultValue = "";

        // BODY field
        private Class<?> bodyType;

        Builder(BindingType bindingType) {
            this.bindingType = bindingType;
        }

        public Builder parameterIndex(int parameterIndex) {
            this.parameterIndex = parameterIndex;
            return this;
        }

        public Builder argIndex(int argIndex) {
            this.argIndex = argIndex;
            return this;
        }

        public Builder targetType(Class<?> targetType) {
            this.targetType = targetType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder bodyType(Class<?> bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        public ParameterBinding build() {
            switch (bindingType) {
                case SINGLE_PARAM -> {
                    if (argIndex < 0) {
                        throw new IllegalStateException("argIndex is required for SINGLE_PARAM");
                    }
                    if (targetType == null) {
                        throw new IllegalStateException("targetType is required for SINGLE_PARAM");
                    }
                }
                case BODY -> {
                    if (bodyType == null) {
                        throw new IllegalStateException("bodyType is required for BODY");
                    }
                }
                case SENDER, RAW_ARGS -> {
                    // no additional fields required
                }
            }
            return new ParameterBinding(this);
        }
    }

    public enum BindingType {
        SENDER,       // CommandSender
        RAW_ARGS,     // String[]
        SINGLE_PARAM, // @SubCommandParam
        BODY          // @SubCommandBody
    }
}
