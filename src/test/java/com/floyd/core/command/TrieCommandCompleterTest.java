package com.floyd.core.command;

import com.floyd.core.collection.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Trie and TrieCommandCompleter
 *
 * @author floyd
 * @date 2026/3/30
 */
class TrieCommandCompleterTest {
    
    private TrieCommandCompleter completer;
    
    @BeforeEach
    void setUp() {
        // Create completer with some test commands
        List<String> initialCommands = Arrays.asList(
            "floyd",
            "floyd-admin",
            "floyd-help",
            "floyd-reload",
            "floyd-version",
            "backpack",
            "backpack-open",
            "backpack-upgrade",
            "teleport",
            "teleport-accept",
            "teleport-deny"
        );
        completer = new TrieCommandCompleter(initialCommands);
    }
    
    @Test
    void testAddCommand() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommand("test");
        
        assertTrue(newCompleter.containsCommand("test"));
        assertFalse(newCompleter.containsCommand("nonexistent"));
    }
    
    @Test
    void testAddCommands() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        List<String> commands = Arrays.asList("cmd1", "cmd2", "cmd3");
        newCompleter.addCommands(commands);
        
        assertTrue(newCompleter.containsCommand("cmd1"));
        assertTrue(newCompleter.containsCommand("cmd2"));
        assertTrue(newCompleter.containsCommand("cmd3"));
    }
    
    @Test
    void testRemoveCommand() {
        assertTrue(completer.containsCommand("floyd"));
        
        boolean removed = completer.removeCommand("floyd");
        assertTrue(removed);
        assertFalse(completer.containsCommand("floyd"));
        
        // Other floyd commands should still exist
        assertTrue(completer.containsCommand("floyd-admin"));
        assertTrue(completer.containsCommand("floyd-help"));
    }
    
    @Test
    void testComplete_WithPrefix() {
        List<String> completions = completer.complete("floyd");
        
        assertNotNull(completions);
        assertEquals(4, completions.size());
        assertFalse
                (completions.contains("floyd"));
        assertTrue(completions.contains("floyd-admin"));
        assertTrue(completions.contains("floyd-help"));
        assertTrue(completions.contains("floyd-reload"));
        assertTrue(completions.contains("floyd-version"));
    }
    
    @Test
    void testComplete_WithPartialPrefix() {
        List<String> completions = completer.complete("floyd-a");
        
        assertNotNull(completions);
        assertEquals(1, completions.size());
        assertTrue(completions.contains("floyd-admin"));
    }
    
    @Test
    void testComplete_WithNoMatches() {
        List<String> completions = completer.complete("xyz");
        
        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }
    
    @Test
    void testComplete_WithEmptyInput() {
        List<String> completions = completer.complete("");
        
        assertNotNull(completions);
        assertEquals(11, completions.size()); // All commands
    }
    
    @Test
    void testComplete_WithNullInput() {
        List<String> completions = completer.complete(null);
        
        assertNotNull(completions);
        assertEquals(11, completions.size()); // All commands
    }
    
    @Test
    void testComplete_CaseInsensitive() {
        List<String> completions = completer.complete("FLOYD");
        
        assertNotNull(completions);
        assertEquals(4, completions.size());
        assertFalse(completions.contains("floyd"));
        assertTrue(completions.contains("floyd-admin"));
    }
    
    @Test
    void testComplete_BackpackCommands() {
        List<String> completions = completer.complete("backpack");
        
        assertNotNull(completions);
        assertEquals(2, completions.size());
        assertFalse(completions.contains("backpack"));
        assertTrue(completions.contains("backpack-open"));
        assertTrue(completions.contains("backpack-upgrade"));
    }
    
    @Test
    void testClear() {
        assertFalse(completer.complete("floyd").isEmpty());
        
        completer.clear();
        
        assertTrue(completer.complete("floyd").isEmpty());
        assertFalse(completer.containsCommand("floyd"));
    }
    
    @Test
    void testTrieStructure() {
        Trie trie = completer.getTrie();
        assertNotNull(trie);
        
        assertTrue(trie.contains("floyd"));
        assertTrue(trie.hasPrefix("floyd"));
        assertTrue(trie.hasPrefix("floyd-a"));
        assertFalse(trie.hasPrefix("nonexistent"));
    }
    
    @Test
    void testAddNullCommand() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommand(null);
        newCompleter.addCommand("");
        
        assertEquals(0, newCompleter.complete("").size());
    }
    
    @Test
    void testRemoveNonExistentCommand() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommand("test");
        
        boolean removed = newCompleter.removeCommand("nonexistent");
        assertFalse(removed);
    }
}
