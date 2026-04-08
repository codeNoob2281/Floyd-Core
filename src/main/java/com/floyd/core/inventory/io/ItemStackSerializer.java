package com.floyd.core.inventory.io;

import org.bukkit.inventory.ItemStack;

/**
 * Item serialization interface
 *
 * @author floyd
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
