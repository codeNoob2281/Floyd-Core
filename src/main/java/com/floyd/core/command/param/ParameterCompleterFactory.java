package com.floyd.core.command.param;

import com.floyd.core.common.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
@Component
public class ParameterCompleterFactory implements ApplicationContextAware {

    private static volatile ApplicationContext appCtx;

    public static final String ONLINE_PLAYERS = "OnlinePlayersParameterCompleter";

    public static final String OFFLINE_PLAYERS = "OfflinePlayersParameterCompleter";


    public static ParameterCompleter create(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        if (appCtx == null) {
            throw new IllegalStateException("ApplicationContext not set");
        }
        try {
            Object completer = appCtx.getBean(name);
            if (!(completer instanceof ParameterCompleter)) {
                throw new IllegalArgumentException("Not a parameter completer: " + name);
            }
            return (ParameterCompleter) completer;
        } catch (NoSuchBeanDefinitionException be) {
            throw new IllegalArgumentException("No such parameter completer: " + name, be);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }
}
