package com.floyd.core.command;

/**
 *
 * @author floyd
 */
public interface CommandHandlerAdapter {

    /**
     *
     * @param handler
     * @return
     */
    CommandHandlerMapping getCommandHandlerMapping(Object handler);

    /**
     *
     * @param handler
     * @return
     */
    boolean support(Object handler);
}
