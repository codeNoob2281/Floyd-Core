package com.floyd.core.command;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author floyd
 */
@Configuration
public class CommandDispatcherConfiguration {

    @Bean
    CommandDispatcher commandDispatcher(List<CommandHandlerAdapter> handlerAdapters) {
        return new CommandDispatcher(handlerAdapters);
    }

    @Bean
    SubCommandHandlerAdapter subCommandHandlerAdapter() {
        return new SubCommandHandlerAdapter();
    }
}
