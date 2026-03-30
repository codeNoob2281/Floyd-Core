package com.floyd.core.collection;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trie tree implementation for command completion
 *
 * @author floyd
 * @date 2026/3/30
 */
@Getter
public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    /**
     * Insert a command into the trie
     *
     * @param command the command to insert
     */
    public void insert(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }

        TrieNode current = root;
        for (char c : command.toCharArray()) {
            current = current.addChild(c);
        }
        current.setEndOfWord(command);
    }

    /**
     * Insert multiple commands
     *
     * @param commands the commands to insert
     */
    public void insertAll(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        for (String command : commands) {
            insert(command);
        }
    }

    /**
     * Check if a command exists in the trie
     *
     * @param command the command to check
     * @return true if exists
     */
    public boolean contains(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }

        TrieNode node = findNode(command);
        return node != null && node.isEndOfWord();
    }

    /**
     * Check if any command starts with the given prefix
     *
     * @param prefix the prefix to check
     * @return true if has prefix
     */
    public boolean hasPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return false;
        }

        return findNode(prefix) != null;
    }

    /**
     * Get all commands that start with the given prefix
     *
     * @param prefix the prefix
     * @return list of matching commands
     */
    public List<String> getByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            // Return all commands if prefix is empty
            return root.getAllValues();
        }

        TrieNode node = findNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        // all commands
        return node.getAllValues();
    }

    /**
     * Find the node corresponding to the end of the given string
     *
     * @param str the string to find
     * @return the node or null if not found
     */
    private TrieNode findNode(String str) {
        if (str == null || str.isEmpty()) {
            return root;
        }

        TrieNode current = root;
        for (char c : str.toCharArray()) {
            if (!current.hasChild(c)) {
                return null;
            }
            current = current.getChild(c);
        }
        return current;
    }

    /**
     * Remove a command from the trie
     *
     * @param command the command to remove
     * @return true if removed successfully
     */
    public boolean remove(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }
        return remove(root, command, 0);
    }

    private boolean remove(TrieNode node, String command, int index) {
        if (index == command.length()) {
            if (!node.isEndOfWord()) {
                return false;
            }
            node.setEndOfWord(false);
            return true;
        }

        char c = command.charAt(index);
        TrieNode child = node.getChild(c);

        if (child == null) {
            return false;
        }

        boolean isValRemoved = remove(child, command, index + 1);

        if (isValRemoved && child.getChildren().isEmpty()) {
            node.getChildren().remove(c);
        }

        return isValRemoved;
    }

    /**
     * Clear all commands from the trie
     */
    public void clear() {
        root.getChildren().clear();
    }

}
