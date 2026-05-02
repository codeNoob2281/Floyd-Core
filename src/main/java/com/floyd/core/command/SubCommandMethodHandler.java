package com.floyd.core.command;

import com.floyd.core.permission.PermissionUtil;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * @author floyd
 */
@Data
public class SubCommandMethodHandler {

    private Object target;

    private Method method;

    private String[] subCommands;

    private String permission;

    private boolean fallback;

    public SubCommandMethodHandler(Object target, Method method, String[] subCommands, String permission) {
        this.target = target;
        this.method = method;
        this.method.setAccessible(true);
        this.subCommands = subCommands;
        this.permission = permission;
        this.fallback = false;
    }

    public SubCommandMethodHandler(Object target, Method method) {
        this.target = target;
        this.method = method;
        this.subCommands = new String[0];
        this.method.setAccessible(true);
        this.fallback = true;
    }

    public SubCommandInvokeResult invoke(CommandSender commandSender, String[] args) throws CommandInvokeException {
        // check permission
        if (!PermissionUtil.hasPermission(commandSender, permission)) {
            commandSender.sendMessage(
                    Component.text("You don't have permission to execute this command, the permission [" + permission + "] is required.", NamedTextColor.RED)
            );
            return SubCommandInvokeResult.builder()
                    .commandValid(true)
                    .build();
        }

        // execute
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == CommandSender.class) {
                parameters[i] = commandSender;
            } else if (parameterType == String[].class) {
                parameters[i] = args;
            } else {
                throw new CommandInvokeException("Unsupported parameter type in position " + i + ": " + parameterType.getName());
            }
        }
        try {
            Object invokeResult = method.invoke(target, parameters);
            return handleResult(invokeResult);
        } catch (Exception e) {
            throw new CommandInvokeException("Error invoking method: " + method.getName(), e);
        }
    }

    private SubCommandInvokeResult handleResult(Object invokeResult) {
        if (invokeResult instanceof SubCommandInvokeResult) {
            return (SubCommandInvokeResult) invokeResult;
        } else if (invokeResult instanceof Boolean) {
            return SubCommandInvokeResult.builder()
                    .commandValid((Boolean) invokeResult)
                    .build();
        } else {
            return SubCommandInvokeResult.builder()
                    .commandValid(true)
                    .build();
        }
    }
}
