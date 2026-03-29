package com.floyd.core.inventory.io;

import org.bukkit.inventory.ItemStack;

/**
 * Item serialization interface
 *
 * @author floyd
 * @date 2026/3/24
 */
public interface ItemStackSerializer {

    /**
     * Serialize item
     *
     * @param itemStack
     * @return
     */
    String serialize(ItemStack itemStack) ;

    /**
     * Deserialize item
     *
     * @param serializedData
     * @return
     */
    ItemStack deserialize(String serializedData);
}
