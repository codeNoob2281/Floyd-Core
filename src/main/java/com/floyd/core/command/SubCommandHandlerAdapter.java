package com.floyd.core.command;

import com.floyd.core.util.StrUtil;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author floyd
 */
public class SubCommandHandlerAdapter implements CommandHandlerAdapter {

    @Override
    public CommandHandlerMapping getCommandHandlerMapping(Object handler) {
        SubCommandHandler handlerAnnotation = handler.getClass().getAnnotation(SubCommandHandler.class);
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
        for (Method method : handler.getClass().getDeclaredMethods()) {
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
        SubCommandHandler handlerAnnotation = handler.getClass().getAnnotation(SubCommandHandler.class);
        if (handlerAnnotation == null) {
            return false;
        }
        return true;
    }
}
