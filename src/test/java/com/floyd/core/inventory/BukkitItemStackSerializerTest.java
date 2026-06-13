package com.floyd.core.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BukkitItemStackSerializer
 *
 * @author floyd
 */
@ExtendWith(MockitoExtension.class)
class BukkitItemStackSerializerTest {

    private BukkitItemStackSerializer serializer;

    @Mock
    private ItemStack itemStack;

    @Mock
    private Material material;

    @BeforeEach
    void setUp() {
        serializer = new BukkitItemStackSerializer();
    }

    // ========== serialize tests ==========

    @Test
    void testSerialize_nullInput_throwsException() {
        assertThrows(Exception.class, () -> serializer.serialize(null));
    }

    @Test
    void testSerialize_airItem_throwsSerializeException() {
        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(true);

        ItemStackSerializeException exception = assertThrows(
                ItemStackSerializeException.class,
                () -> serializer.serialize(itemStack)
        );
        assertEquals("Cannot serialize empty or air ItemStack", exception.getMessage());
    }

    @Test
    void testSerialize_zeroAmount_throwsSerializeException() {
        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(0);

        ItemStackSerializeException exception = assertThrows(
                ItemStackSerializeException.class,
                () -> serializer.serialize(itemStack)
        );
        assertEquals("Cannot serialize empty or air ItemStack", exception.getMessage());
    }

    @Test
    void testSerialize_negativeAmount_throwsSerializeException() {
        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(-1);

        ItemStackSerializeException exception = assertThrows(
                ItemStackSerializeException.class,
                () -> serializer.serialize(itemStack)
        );
        assertEquals("Cannot serialize empty or air ItemStack", exception.getMessage());
    }

    @Test
    void testSerialize_validItem_returnsBase64String() {
        byte[] fakeBytes = new byte[]{1, 2, 3, 4, 5};
        String expectedBase64 = Base64.getEncoder().encodeToString(fakeBytes);

        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(1);
        when(itemStack.serializeAsBytes()).thenReturn(fakeBytes);

        String result = serializer.serialize(itemStack);

        assertEquals(expectedBase64, result);
    }

    @Test
    void testSerialize_validItemWithAmountGreaterThanOne_returnsBase64String() {
        byte[] fakeBytes = new byte[]{10, 20, 30};
        String expectedBase64 = Base64.getEncoder().encodeToString(fakeBytes);

        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(64);
        when(itemStack.serializeAsBytes()).thenReturn(fakeBytes);

        String result = serializer.serialize(itemStack);

        assertEquals(expectedBase64, result);
    }

    @Test
    void testSerialize_serializeAsBytesThrows_wrapsInSerializeException() {
        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(1);
        when(itemStack.serializeAsBytes()).thenThrow(new RuntimeException("internal error"));

        ItemStackSerializeException exception = assertThrows(
                ItemStackSerializeException.class,
                () -> serializer.serialize(itemStack)
        );
        assertEquals("Failed to serialize ItemStack", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    // ========== deserialize tests ==========

    @Test
    void testDeserialize_validBase64_returnsItemStack() {
        byte[] fakeBytes = new byte[]{1, 2, 3, 4, 5};
        String base64Input = Base64.getEncoder().encodeToString(fakeBytes);
        ItemStack expectedItem = mock(ItemStack.class);

        try (MockedStatic<ItemStack> itemStackStatic = mockStatic(ItemStack.class)) {
            itemStackStatic.when(() -> ItemStack.deserializeBytes(fakeBytes)).thenReturn(expectedItem);

            ItemStack result = serializer.deserialize(base64Input);

            assertSame(expectedItem, result);
        }
    }

    @Test
    void testDeserialize_invalidBase64_throwsDeserializeException() {
        ItemStackDeserializeException exception = assertThrows(
                ItemStackDeserializeException.class,
                () -> serializer.deserialize("!!!invalid-base64!!!")
        );
        assertEquals("Failed to deserialize ItemStack", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void testDeserialize_deserializeBytesThrows_wrapsInDeserializeException() {
        byte[] fakeBytes = new byte[]{1, 2, 3};
        String base64Input = Base64.getEncoder().encodeToString(fakeBytes);

        try (MockedStatic<ItemStack> itemStackStatic = mockStatic(ItemStack.class)) {
            itemStackStatic.when(() -> ItemStack.deserializeBytes(fakeBytes))
                    .thenThrow(new RuntimeException("corrupted data"));

            ItemStackDeserializeException exception = assertThrows(
                    ItemStackDeserializeException.class,
                    () -> serializer.deserialize(base64Input)
            );
            assertEquals("Failed to deserialize ItemStack", exception.getMessage());
            assertNotNull(exception.getCause());
        }
    }

    // ========== round-trip test ==========

    @Test
    void testSerializeAndDeserialize_roundTrip() {
        byte[] fakeBytes = new byte[]{10, 20, 30, 40, 50};
        String expectedBase64 = Base64.getEncoder().encodeToString(fakeBytes);
        ItemStack originalItem = mock(ItemStack.class);

        when(itemStack.getType()).thenReturn(material);
        when(material.isAir()).thenReturn(false);
        when(itemStack.getAmount()).thenReturn(1);
        when(itemStack.serializeAsBytes()).thenReturn(fakeBytes);

        try (MockedStatic<ItemStack> itemStackStatic = mockStatic(ItemStack.class)) {
            itemStackStatic.when(() -> ItemStack.deserializeBytes(fakeBytes)).thenReturn(originalItem);

            String serialized = serializer.serialize(itemStack);
            ItemStack deserialized = serializer.deserialize(serialized);

            assertEquals(expectedBase64, serialized);
            assertSame(originalItem, deserialized);
        }
    }

    // ========== interface contract test ==========

    @Test
    void testImplementsItemStackSerializer() {
        assertInstanceOf(ItemStackSerializer.class, serializer);
    }
}
