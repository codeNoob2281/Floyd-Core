package com.floyd.core.convert.provider;

import com.floyd.core.common.convert.TypeConversionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PlayerTypeConvertProvider unit tests
 */
class PlayerTypeConvertProviderTest {

    private final PlayerTypeConvertProvider provider = new PlayerTypeConvertProvider();

    @Test
    void testSupportPlayerClass() {
        assertTrue(provider.support(Player.class));
    }

    @Test
    void testSupportOtherClass() {
        assertFalse(provider.support(String.class));
        assertFalse(provider.support(Object.class));
    }

    @Test
    void testConvertNullReturnsNull() {
        assertNull(provider.convert(null, Player.class));
    }

    @Test
    void testConvertPlayerFound() {
        Player mockPlayer = mock(Player.class);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Steve")).thenReturn(mockPlayer);

            Object result = provider.convert("Steve", Player.class);
            assertSame(mockPlayer, result);
        }
    }

    @Test
    void testConvertPlayerNotFound() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Unknown")).thenReturn(null);

            TypeConversionException exception = assertThrows(
                    TypeConversionException.class,
                    () -> provider.convert("Unknown", Player.class)
            );
            assertEquals("Player Unknown not found", exception.getMessage());
        }
    }
}
