package com.floyd.core.command;

/**
 * @author floyd
 */
public interface CommandHandlerMapping {

    /**
     * Get the command completer
     *
     * @return the command completer
     */
    PermCheckCommandCompleter getCommandCompleter();

    /**
     * Get the root command
     *
     * @return the root command
     */
    String rootCommand();


    /**
     * Get the method handler
     *
     * @param args the command args
     * @return the method handler
     */
    SubCommandMethodHandler getMethodHandler(String[] args);
}
