package com.floyd.core.command;

import java.util.List;

/**
 * Command completer interface for tab completion
 *
 * @author floyd
 */
public interface CommandCompleter {

    /**
     * Add a command to the completer
     * <p>
     * the format of Minecraft command is <code>commandName [arg1] [arg2] ...</code> ,it not contains <code>/</code> ,each of args is separated by space
     * for example:
     * if your full command in Minecraft is <code>/fun reload [arg1] [arg2]</code> ,the string of command to be added can be <code>fun reload</code>
     *
     * </p>
     *
     * @param command the command to add
     */
    void addCommand(String command);

    /**
     * Add multiple commands to the completer
     * <p>
     * the format of Minecraft command is <code>commandName [arg1] [arg2] ...</code> ,it not contains <code>/</code> ,each of args is separated by space
     * for example:
     * if your full command in Minecraft is <code>/fun reload [arg1] [arg2]</code> ,the string of command to be added can be <code>fun reload</code>
     * </p>
     *
     * @param commands the commands to add
     */
    void addCommands(List<String> commands);

    /**
     * Remove a command from the completer
     *
     * @param command the command to remove
     * @return true if removed successfully
     */
    boolean removeCommand(String command);

    /**
     * Get all possible completions for the given input
     *
     * @param input the partial command input
     * @return list of possible completions
     */
    List<String> complete(String input);

    /**
     * Get the next argument for the given command
     *
     * @param command the command
     * @param args    the current arguments
     * @return the next argument
     */
    List<String> nextArgs(String command, String... args);

    /**
     * Check if the completer contains a specific command
     *
     * @param command the command to check
     * @return true if contains
     */
    boolean containsCommand(String command);

    /**
     * Clear all commands from the completer
     */
    void clear();
}
