package com.floyd.core.command;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author floyd
 */
@Configuration
public class CommandDispatcherConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static CommandDispatcher commandDispatcher() {
        CommandDispatcher commandDispatcher = new CommandDispatcher();
        commandDispatcher.addHandlerAdapter(new SubCommandHandlerAdapter());
        return commandDispatcher;
    }
}
