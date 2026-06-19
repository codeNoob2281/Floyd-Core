package com.floyd.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
        SubCommandMethodHandler methodHandler = commandHandlerMapping.getMethodHandler(args);
        if (methodHandler == null) {
            return false;
        }
        int subCmdLen = methodHandler.getSubCommands() == null ? 0 : methodHandler.getSubCommands().length;
        String[] subCmdArgs = new String[args.length - subCmdLen];
        System.arraycopy(args, subCmdLen, subCmdArgs, 0, args.length - subCmdLen);
        SubCommandInvokeResult invokeResult = methodHandler.invoke(sender, subCmdArgs);
        return invokeResult.isCommandValid();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        PermCheckCommandCompleter commandCompleter = commandHandlerMapping.getCommandCompleter();
        List<String> results = null;
        if (sender instanceof Player) {
            results = commandCompleter.nextArgs((Player) sender, command.getName(), args);
        } else {
            results = commandCompleter.nextArgs(command.getName(), args);
        }
        if (results != null && !results.isEmpty()) {
            return results;
        }

        SubCommandMethodHandler methodHandler = commandHandlerMapping.getMethodHandler(args);
        if (methodHandler != null) {
            int subCmdLen = methodHandler.getSubCommands() == null ? 0 : methodHandler.getSubCommands().length;
            int argIdx = args.length - 1 - subCmdLen;
            results = methodHandler.completeParam(sender, argIdx, args[args.length - 1]);
        }

        return results != null ? results : new ArrayList<>();
    }
}
