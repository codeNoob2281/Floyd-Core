package com.floyd.core.command;

import com.floyd.core.util.StrUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author floyd
 */
public class SubCommandHandlerAdapter implements CommandHandlerAdapter {

    @Override
    public CommandHandlerMapping getCommandHandlerMapping(Object handler) {
        SubCommandHandler handlerAnnotation = AnnotationUtils.findAnnotation(handler.getClass(), SubCommandHandler.class);
        if (handlerAnnotation == null) {
            throw new IllegalArgumentException("SubCommandHandler annotation is required");
        }
        String rootCmd = handlerAnnotation.rootCommand();
        if (StrUtil.isBlank(rootCmd)) {
            throw new IllegalArgumentException("SubCommandHandler annotation value cannot be empty");
        }

        List<SubCommandMethodHandler> methodHandlers = parseSubCommandMethodHandlers(handler);
        SubCommandHandlerMapping handlerMapping = new SubCommandHandlerMapping(handlerAnnotation.rootCommand());
        methodHandlers.forEach(handlerMapping::registerHandler);
        return handlerMapping;
    }

    private static @NonNull List<SubCommandMethodHandler> parseSubCommandMethodHandlers(Object handler) {
        List<SubCommandMethodHandler> methodHandlers = new ArrayList<>();
        Method[] methods = ReflectionUtils.getDeclaredMethods(AopUtils.getTargetClass(handler));
        for (Method method : methods) {
            SubCommandMapping subAnnotation = method.getAnnotation(SubCommandMapping.class);
            if (subAnnotation == null) {
                continue;
            }
            SubCommandMethodHandler methodHandler;
            if (subAnnotation.isFallback()) {
                methodHandler = new SubCommandMethodHandler(handler, method);
            } else {
                methodHandler = new SubCommandMethodHandler(handler, method, subAnnotation.commands(), subAnnotation.permission());
            }
            methodHandlers.add(methodHandler);
        }
        return methodHandlers;
    }

    @Override
    public boolean support(Object handler) {
        if (handler == null) {
            return false;
        }
        SubCommandHandler handlerAnnotation = AnnotationUtils.findAnnotation(handler.getClass(), SubCommandHandler.class);
        if (handlerAnnotation == null) {
            return false;
        }
        return true;
    }
}
