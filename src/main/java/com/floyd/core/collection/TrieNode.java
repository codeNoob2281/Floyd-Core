package com.floyd.core.collection;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trie tree node for command completion
 *
 * @author floyd
 * @date 2026/3/30
 */
@Getter
public class TrieNode {

    private final Map<Character, TrieNode> children;

    private boolean isEndOfWord;

    private String value;

    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.value = null;
    }

    /**
     * Add a child node
     *
     * @param character the character
     * @return the child node
     */
    public TrieNode addChild(Character character) {
        return children.computeIfAbsent(character, k -> new TrieNode());
    }

    /**
     * Check if has child with given character
     *
     * @param character the character
     * @return true if has child
     */
    public boolean hasChild(Character character) {
        return children.containsKey(character);
    }

    /**
     * Get child node with given character
     *
     * @param character the character
     * @return the child node or null
     */
    public TrieNode getChild(Character character) {
        return children.get(character);
    }

    /**
     * Mark this node as end of word
     *
     * @param command the full command
     */
    public void setEndOfWord(String command) {
        this.isEndOfWord = command != null && !command.isEmpty();
        this.value = command;
    }

    /**
     * Set end of word flag
     *
     * @param isEndOfWord the flag
     */
    public void setEndOfWord(boolean isEndOfWord) {
        this.isEndOfWord = isEndOfWord;
        if (!isEndOfWord) {
            this.value = null;
        }
    }

    /**
     * Get all commands from this node
     *
     * @return list of commands
     */
    public List<String> getAllValues() {
        List<String> commands = new ArrayList<>();
        collectVals(this, new StringBuilder(), commands);
        return commands;
    }

    private void collectVals(TrieNode node, StringBuilder current, List<String> commands) {
        if (node.isEndOfWord && node.value != null) {
            commands.add(node.value);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            current.append(entry.getKey());
            collectVals(entry.getValue(), current, commands);
            current.deleteCharAt(current.length() - 1);
        }
    }

}
