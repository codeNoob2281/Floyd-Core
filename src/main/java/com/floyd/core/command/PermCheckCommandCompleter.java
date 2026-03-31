package com.floyd.core.command;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Permission checkable command completer interface for tab completion
 *
 * @author floyd
 * @date 2026/3/31
 */
public interface PermCheckCommandCompleter extends CommandCompleter {

    /**
     * Add a command with permission to the completer
     * <p>
     * the format of Minecraft command is <code>commandName [arg1] [arg2] ...</code> ,it not contains <code>/</code> ,each of args is separated by space
     * for example:
     * if your full command in Minecraft is <code>/fun reload [arg1] [arg2]</code> ,the string of command to be added can be <code>fun reload</code>
     *
     * </p>
     *
     * @param command    the command to add
     * @param permission the permission of the command
     */
    void addCommand(String command, String permission);

    /**
     * Get all possible completions for the given input
     *
     * @param player the player who is executing the command
     * @param input  the partial command input
     * @return list of possible completions
     */
    List<String> complete(Player player, String input);

    /**
     * Get the next argument for the given command
     *
     * @param player  the player who is executing the command
     * @param command the command
     * @param args    the current arguments
     * @return the next argument
     */
    List<String> nextArgs(Player player, String command, String... args);
}
