package com.floyd.core.collection;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trie tree node for command completion
 *
 * @author floyd
 */
@Getter
public class TrieNode {

    private final Map<Character, TrieNode> children;

    private boolean isEndOfWord;

    private String value;

    private List<String> allValuesCache;

    private boolean cacheDirty;


    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.value = null;
        this.cacheDirty = true;
    }

    /**
     * Add a child node
     *
     * @param character the character
     * @return the child node
     */
    public TrieNode addChild(Character character) {
        this.cacheDirty = true;
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
        this.cacheDirty = true;
        this.isEndOfWord = command != null && !command.isEmpty();
        this.value = command;
    }

    /**
     * Set end of word flag
     *
     * @param isEndOfWord the flag
     */
    public void setEndOfWord(boolean isEndOfWord) {
        this.cacheDirty = true;
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
        if (cacheDirty) {
            List<String> commands = new ArrayList<>();
            collectVals(this, commands);
            allValuesCache = commands;
            cacheDirty = false;
        }
        return new ArrayList<>(allValuesCache);
    }

    private void collectVals(TrieNode node, List<String> commands) {
        if (node.isEndOfWord && node.value != null) {
            commands.add(node.value);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectVals(entry.getValue(), commands);
        }
    }

}
