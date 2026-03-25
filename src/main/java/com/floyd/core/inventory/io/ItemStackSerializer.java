package com.floyd.core.inventory.io;

import org.bukkit.inventory.ItemStack;

/**
 * 物品序列化接口
 *
 * @author floyd
 * @date 2026/3/24
 */
public interface ItemStackSerializer {

    /**
     * 序列化物品
     *
     * @param itemStack
     * @return
     */
    String serialize(ItemStack itemStack) ;

    /**
     * 反序列化物品
     *
     * @param serializedData
     * @return
     */
    ItemStack deserialize(String serializedData);
}
