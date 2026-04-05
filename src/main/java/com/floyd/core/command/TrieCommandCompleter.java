package com.floyd.core.command;

import com.floyd.core.collection.Trie;
import com.floyd.core.util.StrUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Trie-based command completer implementation
 *
 * @author floyd
 * @date 2026/3/30
 */
@Getter
public class TrieCommandCompleter implements PermCheckCommandCompleter {

    private final Trie trie;

    /**
     * Command permission map
     */
    private final Map<String, String> commandPermissionMap = new ConcurrentHashMap<>(32);

    public TrieCommandCompleter() {
        this.trie = new Trie();
    }

    /**
     * Create with initial commands
     *
     * @param initialCommands the initial commands to add
     */
    public TrieCommandCompleter(List<String> initialCommands) {
        this.trie = new Trie();
        if (initialCommands != null && !initialCommands.isEmpty()) {
            this.trie.insertAll(initialCommands);
        }
    }

    @Override
    public void addCommand(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        trie.insert(command);
    }

    @Override
    public void addCommand(String command, String permission) {
        if (command == null || command.isEmpty()) {
            return;
        }
        addCommand(command);
        commandPermissionMap.put(command, permission);
    }

    @Override
    public void addCommands(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        trie.insertAll(commands);
    }

    @Override
    public boolean removeCommand(String command) {
        return trie.remove(command);
    }

    @Override
    public List<String> complete(String input) {
        if (StrUtil.isEmpty(input)) {
            return trie.getByPrefix(StrUtil.EMPTY);
        }

        // Normalize input: convert to lowercase for case-insensitive completion
        String normalizedInput = input.toLowerCase();
        List<String> completions = trie.getByPrefix(normalizedInput);

        // If no matches found with lowercase, try to match the original case
        if (completions.isEmpty()) {
            completions = trie.getByPrefix(input);
        }
        return completions;
    }

    @Override
    public List<String> complete(@Nullable Player player, String input) {
        List<String> fullCmdList = complete(input);
        if (player == null) {
            return fullCmdList;
        }
        return fullCmdList.stream()
                .filter(cmd -> {
                    String permission = commandPermissionMap.get(cmd);
                    return permission == null || player.hasPermission(permission);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> nextArgs(String command, String... args) {
        return nextArgs(null, command, args);
    }

    @Override
    public List<String> nextArgs(@Nullable Player player, String command, String... args) {
        String cmdWithArgs = StrUtil.emptyIfNull(command);
        if (args.length > 0) {
            String argStr = Arrays.stream(args)
                    .map(StrUtil::emptyIfNull)
                    .collect(Collectors.joining(StrUtil.SPACE));
            cmdWithArgs = cmdWithArgs + StrUtil.SPACE + argStr;
        }

        // Get the possible completions
        final String cmdToComplete = cmdWithArgs;
        List<String> completeCmdList = complete(player, cmdToComplete);

        // Get the next argument
        return completeCmdList.stream()
                .map(completion -> {
                    if (completion.length() <= cmdToComplete.length() || completion.charAt(cmdToComplete.length()) == ' ') {
                        return null;
                    } else {
                        int preSpacePos = completion.lastIndexOf(' ', cmdToComplete.length() - 1);
                        int nextSpacePos = completion.indexOf(' ', cmdToComplete.length());
                        nextSpacePos = nextSpacePos == -1 ? completion.length() : nextSpacePos;
                        return completion.substring(preSpacePos + 1, nextSpacePos);
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsCommand(String command) {
        return trie.contains(command);
    }

    @Override
    public void clear() {
        trie.clear();
    }

}
