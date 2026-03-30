package com.floyd.core.command;

import com.floyd.core.collection.Trie;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Trie-based command completer implementation
 *
 * @author floyd
 * @date 2026/3/30
 */
@Getter
public class TrieCommandCompleter implements CommandCompleter {

    private final Trie trie;

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
        if (input == null || input.isEmpty()) {
            input = "";
        }
        // Normalize input: convert to lowercase for case-insensitive completion
        String normalizedInput = input.toLowerCase();
        List<String> completions = trie.getByPrefix(normalizedInput);

        // If no matches found with lowercase, try to match the original case
        if (!normalizedInput.isEmpty() && completions.isEmpty()) {
            completions = trie.getByPrefix(input);
        }

        // Get the next argument
        List<String> formatArgs = Arrays.stream(input.split(" "))
                .filter(arg -> !arg.isEmpty())
                .toList();
        return completions.stream().map(cmd -> {
                    String[] subCmdList = cmd.split(" ");
                    if (formatArgs.isEmpty()) {
                        return subCmdList[0];
                    }
                    String lastArg = formatArgs.getLast();
                    String subCmd = subCmdList[formatArgs.size() - 1];
                    if (subCmd.equalsIgnoreCase(lastArg)) {
                        return subCmdList.length > formatArgs.size() ? subCmdList[formatArgs.size()] : null;
                    } else {
                        return subCmd;
                    }
                })
                .filter(Objects::nonNull).toList();
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
