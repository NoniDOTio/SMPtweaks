package io.noni.smptweaks.utils;

import io.noni.smptweaks.models.PDCKey;
import org.bukkit.persistence.PersistentDataHolder;

public class PDCUtils {

    private PDCUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    public static <T> T get(PersistentDataHolder holder, PDCKey<T> key) {
        return holder.getPersistentDataContainer().get(key.namespace, key.type);
    }

    public static <T> void set(PersistentDataHolder holder, PDCKey<T> key, T value) {
        holder.getPersistentDataContainer().set(key.namespace, key.type, value);
    }

}
