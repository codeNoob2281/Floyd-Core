package com.floyd.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Sub command executor impl
 *
 * @author floyd
 */
public class SubCommandExecutor implements CommandExecutor, TabCompleter {

    private final CommandHandlerMapping commandHandlerMapping;

    public SubCommandExecutor(CommandHandlerMapping commandHandlerMapping) {
        this.commandHandlerMapping = commandHandlerMapping;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        SubCommandMethodHandler mappingHandler = commandHandlerMapping.getMethodHandler(args);
        if (mappingHandler == null) {
            return false;
        }
        int subCmdLen = mappingHandler.getSubCommands() == null ? 0 : mappingHandler.getSubCommands().length;
        String[] subCmdArgs = new String[args.length - subCmdLen];
        System.arraycopy(args, subCmdLen, subCmdArgs, 0, args.length - subCmdLen);
        SubCommandInvokeResult invokeResult = mappingHandler.invoke(sender, subCmdArgs);
        return invokeResult.isCommandValid();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        PermCheckCommandCompleter commandCompleter = commandHandlerMapping.getCommandCompleter();
        if (sender instanceof Player) {
            return commandCompleter.nextArgs((Player) sender, command.getName(), args);
        } else {
            return commandCompleter.nextArgs(command.getName(), args);
        }
    }
}
