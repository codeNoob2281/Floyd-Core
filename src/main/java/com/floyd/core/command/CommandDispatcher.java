package com.floyd.core.command;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default command dispatcher
 *
 * @author floyd
 */
public class CommandDispatcher implements BeanPostProcessor, SmartInitializingSingleton {

    private final Map<String, CommandHandlerMapping> handlerMappingMap = new ConcurrentHashMap<>();

    private final List<CommandHandlerAdapter> handlerAdapters;

    public CommandDispatcher(List<CommandHandlerAdapter> handlerAdapters) {
        this.handlerAdapters = handlerAdapters;
    }

    @Override
    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        for (CommandHandlerAdapter handlerAdapter : handlerAdapters) {
            if (!handlerAdapter.support(bean)) {
                continue;
            }
            CommandHandlerMapping cmdHandlerMapping = handlerAdapter.getCommandHandlerMapping(bean);
            handlerMappingMap.compute(cmdHandlerMapping.rootCommand(), (rootCmd, existHandlerMapping) -> {
                if (existHandlerMapping == null) {
                    return cmdHandlerMapping;
                }
                if (existHandlerMapping instanceof MergeableCommandHandlerMapping ehm &&
                        cmdHandlerMapping instanceof MergeableCommandHandlerMapping nhm) {
                    ehm.merge(nhm);
                } else {
                    throw new IllegalArgumentException("Duplicate root command: " + rootCmd);
                }
                return existHandlerMapping;
            });
        }
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        handlerMappingMap.forEach((rootCmd, commandHandlerMapping) -> {
            Bukkit.getPluginCommand(rootCmd)
                    .setExecutor(commandHandlerMapping.getCommandExecutor());
        });
    }
}
