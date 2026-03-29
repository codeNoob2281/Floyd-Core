package com.floyd.core.inventory.io;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

/**
 * Bukkit official supported serialization implementation
 *
 * @author floyd
 * @date 2026/3/24
 */
public class BukkitItemStackSerializer implements ItemStackSerializer {

    @Override
    public String serialize(@NotNull ItemStack itemStack) {
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
