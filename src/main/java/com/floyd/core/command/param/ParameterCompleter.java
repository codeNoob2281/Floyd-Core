package com.floyd.core.command.param;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command completer interface for params completion
 *
 * @author floyd
 */
@FunctionalInterface
public interface ParameterCompleter {

    /**
     * Get all possible completions for the given input
     *
     * @param commandSender the command sender
     * @param partial       the partial command input
     * @return list of possible completions
     */
    List<String> complete(CommandSender commandSender, String partial);
}
