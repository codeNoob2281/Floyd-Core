package com.floyd.core.command.param;

import com.floyd.core.command.param.ParameterBinding.BindingType;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the parameter list of @SubCommandMapping methods and generates parameter binding metadata.
 *
 * @author floyd
 */
public class ParameterResolver {

    /**
     * Resolves method parameters and returns a list of parameter binding metadata.
     * Ordered by parameter declaration order.
     */
    public static List<ParameterBinding> resolve(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<ParameterBinding> bindings = new ArrayList<>(paramTypes.length);

        for (int i = 0; i < paramTypes.length; i++) {
            bindings.add(resolveParameter(i, paramTypes[i], paramAnnotations[i]));
        }
        return bindings;
    }

    private static ParameterBinding resolveParameter(
            int position, Class<?> type, Annotation[] annotations) {

        // 1. CommandSender — bind directly
        if (type == CommandSender.class) {
            return ParameterBinding.builder(BindingType.SENDER)
                    .parameterIndex(position)
                    .build();
        }

        // 2. Find @SubCommandParam annotation
        SubCommandParam paramAnnotation = findAnnotation(annotations, SubCommandParam.class);
        if (paramAnnotation != null) {
            return ParameterBinding.builder(BindingType.SINGLE_PARAM)
                    .parameterIndex(position)
                    .argIndex(paramAnnotation.index())
                    .targetType(type)
                    .description(paramAnnotation.description())
                    .required(paramAnnotation.required())
                    .defaultValue(paramAnnotation.defaultValue())
                    .parameterCompleter(paramAnnotation.completer())
                    .build();
        }

        // 3. Find @SubCommandBody annotation
        SubCommandBody bodyAnnotation = findAnnotation(annotations, SubCommandBody.class);
        if (bodyAnnotation != null) {
            return ParameterBinding.builder(BindingType.BODY)
                    .parameterIndex(position)
                    .bodyType(type)
                    .build();
        }

        // 4. Compatibility: String[] — pass raw arguments
        if (type == String[].class) {
            return ParameterBinding.builder(BindingType.RAW_ARGS)
                    .parameterIndex(position)
                    .build();
        }

        // 5. Unsupported parameter type
        throw new IllegalArgumentException(
                "Unsupported parameter type at position " + position + ": " + type.getName()
                        + ". Use @SubCommandParam, @SubCommandBody, CommandSender, or String[].");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T findAnnotation(
            Annotation[] annotations, Class<T> annotationType) {
        for (Annotation a : annotations) {
            if (annotationType.isInstance(a)) {
                return (T) a;
            }
        }
        return null;
    }
}
