package com.floyd.core.command;

import com.floyd.core.permission.PermissionUtil;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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

    /** Method parameter binding metadata (pre-computed at startup to avoid repeated reflection on each call) */
    private List<ParameterBinding> parameterBindings;

    public SubCommandMethodHandler(Object target, Method method, String[] subCommands, String permission) {
        this.target = target;
        this.method = method;
        this.method.setAccessible(true);
        this.subCommands = subCommands;
        this.permission = permission;
        this.fallback = false;
        this.parameterBindings = ParameterResolver.resolve(method);
    }

    public SubCommandMethodHandler(Object target, Method method) {
        this.target = target;
        this.method = method;
        this.method.setAccessible(true);
        this.subCommands = new String[0];
        this.fallback = true;
        this.parameterBindings = ParameterResolver.resolve(method);
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

        // parameter binding
        Object[] parameters = new Object[parameterBindings.size()];
        for (ParameterBinding binding : parameterBindings) {
            try {
                parameters[binding.getParameterIndex()] =
                        resolveParameter(binding, commandSender, args);
            } catch (ParameterBindingException e) {
                commandSender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
                return SubCommandInvokeResult.builder()
                        .commandValid(true)
                        .build();
            }
        }

        // execute method
        try {
            Object invokeResult = method.invoke(target, parameters);
            return handleResult(invokeResult);
        } catch (Exception e) {
            throw new CommandInvokeException("Error invoking method: " + method.getName(), e);
        }
    }

    /**
     * Resolves a single parameter value based on binding metadata.
     */
    private Object resolveParameter(ParameterBinding binding,
                                    CommandSender sender, String[] args)
            throws ParameterBindingException {
        return switch (binding.getBindingType()) {
            case SENDER -> sender;
            case RAW_ARGS -> args;
            case SINGLE_PARAM -> resolveSingleParam(binding, args);
            case BODY -> resolveBody(binding, args);
        };
    }

    /**
     * Resolves a single @SubCommandParam parameter.
     */
    private Object resolveSingleParam(ParameterBinding binding, String[] args)
            throws ParameterBindingException {
        int idx = binding.getArgIndex();

        // Parameter missing
        if (idx >= args.length || args[idx] == null || args[idx].isEmpty()) {
            if (!binding.isRequired()) {
                // Use default value
                String defaultVal = binding.getDefaultValue();
                if (defaultVal.isEmpty()) {
                    return getDefaultValue(binding.getTargetType());
                }
                return TypeConverter.convert(defaultVal, binding.getTargetType());
            }
            throw new ParameterBindingException(
                    "Missing required parameter: " + formatParamDescription(binding));
        }

        // Type conversion
        try {
            return TypeConverter.convert(args[idx], binding.getTargetType());
        } catch (TypeConversionException e) {
            throw new ParameterBindingException(
                    "Invalid value for " + formatParamDescription(binding)
                            + ": '" + args[idx] + "' (" + e.getMessage() + ")");
        }
    }

    /**
     * Resolves @SubCommandBody POJO object.
     * Creates object instance via reflection and binds @SubCommandParam fields one by one.
     */
    private Object resolveBody(ParameterBinding binding, String[] args)
            throws ParameterBindingException {
        Class<?> bodyType = binding.getBodyType();
        Object body;
        try {
            body = bodyType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ParameterBindingException(
                    "Cannot instantiate body type: " + bodyType.getSimpleName());
        }

        // Scan @SubCommandParam annotations on POJO fields
        for (Field field : bodyType.getDeclaredFields()) {
            SubCommandParam paramAnnotation = field.getAnnotation(SubCommandParam.class);
            if (paramAnnotation == null) {
                continue;
            }

            int idx = paramAnnotation.index();
            field.setAccessible(true);

            // Parameter missing
            if (idx >= args.length || args[idx] == null || args[idx].isEmpty()) {
                if (!paramAnnotation.required()) {
                    // Optional field, skip to keep default value
                    continue;
                }
                throw new ParameterBindingException(
                        "Missing required parameter: "
                                + (paramAnnotation.description().isEmpty()
                                ? field.getName() : paramAnnotation.description()));
            }

            // Type conversion and assignment
            try {
                Object value = TypeConverter.convert(args[idx], field.getType());
                field.set(body, value);
            } catch (TypeConversionException e) {
                throw new ParameterBindingException(
                        "Invalid value for "
                                + (paramAnnotation.description().isEmpty()
                                ? field.getName() : paramAnnotation.description())
                                + ": '" + args[idx] + "' (" + e.getMessage() + ")");
            } catch (IllegalAccessException e) {
                throw new ParameterBindingException(
                        "Cannot set field: " + field.getName());
            }
        }

        return body;
    }

    private String formatParamDescription(ParameterBinding binding) {
        if (binding.getDescription() != null && !binding.getDescription().isEmpty()) {
            return binding.getDescription() + " (index=" + binding.getArgIndex() + ")";
        }
        return "parameter[index=" + binding.getArgIndex() + "]";
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == long.class || type == float.class || type == double.class) {
            return 0;
        }
        if (type == boolean.class) {
            return false;
        }
        return null;
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
