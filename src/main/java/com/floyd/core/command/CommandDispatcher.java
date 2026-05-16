package com.floyd.core.command;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.Collections;
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

    private final ObjectProvider<List<CommandHandlerAdapter>> adapterProvider;

    private final List<Object> deferredBeans = new ArrayList<>();

    public CommandDispatcher(ObjectProvider<List<CommandHandlerAdapter>> adapterProvider) {
        this.adapterProvider = adapterProvider;
    }

    @Override
    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        deferredBeans.add(bean);
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // Collect adapters from both ObjectProvider and programmatic registrations
        List<CommandHandlerAdapter> adapters = adapterProvider.getIfAvailable(Collections::emptyList);

        for (Object bean : deferredBeans) {
            for (CommandHandlerAdapter adapter : adapters) {
                if (!adapter.support(bean)) {
                    continue;
                }
                CommandHandlerMapping cmdHandlerMapping = adapter.getCommandHandlerMapping(bean);
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
        }
        deferredBeans.clear();

        handlerMappingMap.forEach((rootCmd, commandHandlerMapping) -> {
            Bukkit.getPluginCommand(rootCmd)
                    .setExecutor(commandHandlerMapping.getCommandExecutor());
        });
    }
}
