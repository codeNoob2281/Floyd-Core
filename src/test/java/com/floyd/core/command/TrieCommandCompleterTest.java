package com.floyd.core.command;

import com.floyd.core.collection.Trie;
import com.floyd.core.util.StrUtil;
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
                "floyd admin",
                "floyd help",
                "floyd reload",
                "floyd version",
                "backpack",
                "backpack open",
                "backpack upgrade",
                "teleport",
                "teleport accept",
                "teleport deny"
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
        assertTrue(completer.containsCommand("floyd admin"));
        assertTrue(completer.containsCommand("floyd help"));
    }

    @Test
    void testComplete_WithPrefix() {
        List<String> completions = completer.complete("floyd");

        assertNotNull(completions);
        assertEquals(5, completions.size());
        assertTrue(completions.contains("floyd"));
        assertTrue(completions.contains("floyd admin"));
        assertTrue(completions.contains("floyd help"));
        assertTrue(completions.contains("floyd reload"));
        assertTrue(completions.contains("floyd version"));
    }

    @Test
    void testComplete_WithPartialPrefix() {
        List<String> completions = completer.complete("floyd a");

        assertNotNull(completions);
        assertEquals(1, completions.size());
        assertTrue(completions.contains("floyd admin"));
    }

    @Test
    void testComplete_WithNoMatches() {
        List<String> completions = completer.complete("xyz");

        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }

    @Test
    void testComplete_WithEmptyInput() {
        List<String> completions = completer.complete(StrUtil.EMPTY);

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
        assertEquals(5, completions.size());
        assertTrue(completions.contains("floyd"));
        assertTrue(completions.contains("floyd admin"));
        assertTrue(completions.contains("floyd help"));
        assertTrue(completions.contains("floyd reload"));
        assertTrue(completions.contains("floyd version"));
    }

    @Test
    void testComplete_BackpackCommands() {
        List<String> completions = completer.complete("backpack");

        assertNotNull(completions);
        assertEquals(3, completions.size());
        assertTrue(completions.contains("backpack"));
        assertTrue(completions.contains("backpack open"));
        assertTrue(completions.contains("backpack upgrade"));
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
        assertTrue(trie.hasPrefix("floyd a"));
        assertFalse(trie.hasPrefix("nonexistent"));
    }

    @Test
    void testAddNullCommand() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommand(null);
        newCompleter.addCommand(StrUtil.EMPTY);

        assertEquals(0, newCompleter.complete(StrUtil.EMPTY).size());
    }

    @Test
    void testRemoveNonExistentCommand() {
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommand("test");

        boolean removed = newCompleter.removeCommand("nonexistent");
        assertFalse(removed);
    }

    @Test
    void testNextArgs_SingleWordCommand() {
        // Test completing a single word command
        List<String> nextArgs = completer.nextArgs("floy");

        assertNotNull(nextArgs);
        // Should return the remaining part of each matching command
        assertEquals(1, nextArgs.size());
        assertTrue(nextArgs.contains("floyd")); // floy -> floyd
    }

    @Test
    void testNextArgs_ExactMatch() {
        // Test when command is exactly matched
        List<String> nextArgs = completer.nextArgs("floyd");

        assertNotNull(nextArgs);
        // After "floyd", should show subcommands
    }

    @Test
    void testNextArgs_WithSpace() {
        // Test completing after space
        List<String> nextArgs = completer.nextArgs("floyd ");

        assertNotNull(nextArgs);
        // Should return the subcommand part
        assertTrue(nextArgs.contains("admin"));
        assertTrue(nextArgs.contains("help"));
        assertTrue(nextArgs.contains("reload"));
        assertTrue(nextArgs.contains("version"));
    }

    @Test
    void testNextArgs_PartialSubcommand() {
        // Test completing partial subcommand
        List<String> nextArgs = completer.nextArgs("floyd hel");

        assertNotNull(nextArgs);
        assertEquals(1, nextArgs.size());
        assertTrue(nextArgs.contains("help")); // floyd hel -> floyd help
    }

    @Test
    void testNextArgs_MultipleWords() {
        // Test with multiple word commands
        List<String> nextArgs = completer.nextArgs("teleport ");

        assertNotNull(nextArgs);
        assertTrue(nextArgs.contains("accept"));
        assertTrue(nextArgs.contains("deny"));
    }

    @Test
    void testNextArgs_NoMatches() {
        // Test with non-existent command
        List<String> nextArgs = completer.nextArgs("nonexistent");

        assertNotNull(nextArgs);
        assertTrue(nextArgs.isEmpty());
    }

    @Test
    void testNextArgs_EmptyInput() {
        // Test with empty input
        List<String> nextArgs = completer.nextArgs(StrUtil.EMPTY);

        assertNotNull(nextArgs);
        // Should return first letters of all commands
        assertEquals(3, nextArgs.size());
        assertTrue(nextArgs.contains("floyd"));
        assertTrue(nextArgs.contains("backpack"));
        assertTrue(nextArgs.contains("teleport"));
    }

    @Test
    void testNextArgs_NullInput() {
        // Test with null input
        List<String> nextArgs = completer.nextArgs((String) null);

        assertNotNull(nextArgs);
        // Should return first letters of all commands
        assertEquals(3, nextArgs.size());
        assertTrue(nextArgs.contains("floyd"));
        assertTrue(nextArgs.contains("backpack"));
        assertTrue(nextArgs.contains("teleport"));
    }

    @Test
    void testNextArgs_CaseInsensitive() {
        // Test case insensitive completion
        List<String> nextArgs = completer.nextArgs("FLOYD", StrUtil.EMPTY);

        assertNotNull(nextArgs);
        assertTrue(nextArgs.contains("admin"));
        assertTrue(nextArgs.contains("help"));
    }

    @Test
    void testNextArgs_EmptyArgArray() {
        // Test with empty arg array
        List<String> nextArgs = completer.nextArgs(StrUtil.EMPTY);

        assertNotNull(nextArgs);
        assertEquals(3, nextArgs.size());
        assertTrue(nextArgs.contains("floyd"));
        assertTrue(nextArgs.contains("backpack"));
        assertTrue(nextArgs.contains("teleport"));
    }

    @Test
    void testNextArgs_WithEmptyArgInArray() {
        // Test with empty string in args (should be treated as space)
        List<String> nextArgs = completer.nextArgs("floyd", StrUtil.EMPTY);

        assertNotNull(nextArgs);
        assertTrue(nextArgs.contains("admin"));
        assertTrue(nextArgs.contains("help"));
    }

    @Test
    void testNextArgs_ComplexMultiLevelCommand() {
        // Add a more complex command structure
        TrieCommandCompleter newCompleter = new TrieCommandCompleter();
        newCompleter.addCommands(Arrays.asList(
                "admin player kick",
                "admin player ban",
                "admin server restart"
        ));

        // Test first level
        List<String> nextArgs1 = newCompleter.nextArgs("admin", StrUtil.EMPTY);
        assertEquals(2, nextArgs1.size());
        assertTrue(nextArgs1.contains("player"));
        assertTrue(nextArgs1.contains("server"));

        // Test second level
        List<String> nextArgs2 = newCompleter.nextArgs("admin", "player", StrUtil.EMPTY);
        assertEquals(2, nextArgs2.size());
        assertTrue(nextArgs2.contains("kick"));
        assertTrue(nextArgs2.contains("ban"));
    }
}
