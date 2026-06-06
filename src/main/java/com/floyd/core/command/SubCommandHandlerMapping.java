package com.floyd.core.command;

import com.floyd.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author floyd
 */
public class SubCommandHandlerMapping implements MergeableCommandHandlerMapping {

    private final TrieCommandCompleter commandCompleter = new TrieCommandCompleter();

    private final Map<String, SubCommandMethodHandler> methodHandlerMap = new ConcurrentHashMap<>();

    private SubCommandMethodHandler fallbackHandler;

    private final String rootCommand;

    private static final String ROOT_MAPPING_KEY = "#ROOT";

    public SubCommandHandlerMapping(String rootCommand) {
        this.rootCommand = rootCommand;
        this.commandCompleter.addCommand(rootCommand);
    }

    /**
     * Register a handler
     *
     * @param handler the handler to register
     */
    public void registerHandler(@NotNull SubCommandMethodHandler handler) {
        Assert.notNull(handler, "Handler must not be null");
        if (handler.isFallback()) {
            if (this.fallbackHandler != null) {
                throw new IllegalArgumentException("Only one fallback handler is allowed");
            }
            this.fallbackHandler = handler;
            return;
        }

        String mappingKey;
        if (handler.getSubCommands() == null || handler.getSubCommands().length == 0) {
            mappingKey = ROOT_MAPPING_KEY;
        } else {
            mappingKey = String.join(StrUtil.SPACE, handler.getSubCommands());
        }

        methodHandlerMap.compute(mappingKey, (key, oldHandler) -> {
            if (oldHandler != null) {
                throw new IllegalArgumentException("Duplicate mapping key: " + key);
            }
            return handler;
        });

        if (!ROOT_MAPPING_KEY.equals(mappingKey)) {
            commandCompleter.addCommand(rootCommand + StrUtil.SPACE + mappingKey, handler.getPermission());
        }
    }

    @Override
    public PermCheckCommandCompleter getCommandCompleter() {
        return this.commandCompleter;
    }

    @Override
    public String rootCommand() {
        return this.rootCommand;
    }

    @Override
    public void merge(MergeableCommandHandlerMapping handlerMapping) {
        if (!(handlerMapping instanceof SubCommandHandlerMapping handlerMappingToBeMerge)) {
            throw new IllegalArgumentException("SubCommandHandlerMapping type expected but got " + handlerMapping.getClass().getName());
        }
        // Merge methodHandlerMap
        for (SubCommandMethodHandler methodHandler : handlerMappingToBeMerge.methodHandlerMap.values()) {
            registerHandler(methodHandler);
        }
        // Don't forget to merge fallbackHandler
        if (handlerMappingToBeMerge.fallbackHandler != null) {
            registerHandler(handlerMappingToBeMerge.fallbackHandler);
        }
    }

    @Override
    public SubCommandMethodHandler getMethodHandler(String[] args) {
        SubCommandMethodHandler mostMatchHandler = null;
        if (args == null || args.length == 0) {
            mostMatchHandler = methodHandlerMap.get(ROOT_MAPPING_KEY);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                SubCommandMethodHandler handler = methodHandlerMap.get(sb.toString());
                if (handler != null) {
                    mostMatchHandler = handler;
                }
                sb.append(StrUtil.SPACE);
            }
        }
        return mostMatchHandler != null ? mostMatchHandler : this.fallbackHandler;
    }


}
