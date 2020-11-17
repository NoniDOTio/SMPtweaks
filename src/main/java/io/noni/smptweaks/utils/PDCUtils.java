package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCUtils {
    PersistentDataContainer container;


    /**
     * Get PersistentDataContainer of a player
     * @param player
     */
    public PDCUtils(Player player) {
        this.container = player.getPersistentDataContainer();
    }


    /**
     * Get PersistentDataContainer of an item
     * @param item
     */
    public PDCUtils(ItemStack item) {
        this.container = item.getItemMeta().getPersistentDataContainer();
    }


    /**
     * Make NamespacedKey
     * @param key
     * @return
     */
    private NamespacedKey makeKey(String key) {
        return new NamespacedKey(SMPTweaks.getPlugin(), key);
    }


    /**
     * Check if integer with key exists
     * @param key
     * @return
     */
    public boolean hasInt(String key) {
        return container.has(makeKey(key), PersistentDataType.INTEGER);
    }


    /**
     * Set integer with key
     * @param key
     * @param value
     */
    public void setInt(String key, int value) {
        container.set(makeKey(key), PersistentDataType.INTEGER, value);
    }


    /**
     * Get integer with key
     * @param key
     * @return
     */
    public int getInt(String key) {
        return container.get(makeKey(key), PersistentDataType.INTEGER);
    }

}
