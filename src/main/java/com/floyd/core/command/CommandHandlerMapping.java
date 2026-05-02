package com.floyd.core.command;

import org.bukkit.command.CommandExecutor;

/**
 * @author floyd
 */
public interface CommandHandlerMapping {

    /**
     * Get the command executor
     *
     * @return the command executor
     */
    CommandExecutor getCommandExecutor();

    /**
     * Get the root command
     *
     * @return the root command
     */
    String rootCommand();
}
