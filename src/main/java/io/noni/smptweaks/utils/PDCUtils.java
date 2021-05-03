package io.noni.smptweaks.utils;

import io.noni.smptweaks.models.PDCKey;
import org.bukkit.persistence.PersistentDataHolder;

public class PDCUtils {

    private PDCUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Check if a variable exist in a players PersistentDataContainer
     * @param holder
     * @param key
     * @param <T>
     * @return
     */
    public static <T> boolean has(PersistentDataHolder holder, PDCKey<T> key) {
        return holder.getPersistentDataContainer().has(key.namespace, key.type);
    }

    /**
     * Set a variables value from the players PersistentDataContainer
     * @param holder
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(PersistentDataHolder holder, PDCKey<T> key) {
        return holder.getPersistentDataContainer().get(key.namespace, key.type);
    }

    /**
     * Set a variable in the players PersistentDataContainer
     * @param holder Player
     * @param key NamespacedKey
     * @param value
     * @param <T>
     */
    public static <T> void set(PersistentDataHolder holder, PDCKey<T> key, T value) {
        holder.getPersistentDataContainer().set(key.namespace, key.type, value);
    }

}
