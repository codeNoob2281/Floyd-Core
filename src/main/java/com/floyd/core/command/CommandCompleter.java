package com.floyd.core.command;

import java.util.List;

/**
 * Command completer interface for tab completion
 *
 * @author floyd
 * @date 2026/3/30
 */
public interface CommandCompleter {
    
    /**
     * Add a command to the completer
     *
     * @param command the command to add
     */
    void addCommand(String command);
    
    /**
     * Add multiple commands to the completer
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
