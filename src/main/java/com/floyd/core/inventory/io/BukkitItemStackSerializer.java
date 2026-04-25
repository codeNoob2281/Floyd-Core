package com.floyd.core.inventory.io;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Objects;

/**
 * Bukkit official supported serialization implementation
 *
 * @author floyd
 */
public class BukkitItemStackSerializer implements ItemStackSerializer {

    @Override
    public String serialize(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        if (itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
            throw new ItemStackSerializeException("Cannot serialize empty or air ItemStack");
        }
        try {
            byte[] bytes = itemStack.serializeAsBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new ItemStackSerializeException("Failed to serialize ItemStack", e);
        }
    }

    @Override
    public ItemStack deserialize(String serializedData) {
        try {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(serializedData));
        } catch (Exception e) {
            throw new ItemStackDeserializeException("Failed to deserialize ItemStack", e);
        }
    }
}
