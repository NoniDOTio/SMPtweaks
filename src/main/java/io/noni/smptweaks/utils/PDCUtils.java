package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

class PDCUtils {

    private PDCUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    public static <T> T get(PersistentDataHolder holder, Key<T> key) {
        return holder.getPersistentDataContainer().get(key.namespace, key.type);
    }

    public static <T> void set(PersistentDataHolder holder, Key<T> key, T value) {
        holder.getPersistentDataContainer().set(key.namespace, key.type, value);
    }

    public static class Key<T> {
        public static final Key<Integer> SERVER_LEVEL;

        static {
            SERVER_LEVEL = new Key("server_level", PersistentDataType.INTEGER);
        }

        private static final Plugin PLUGIN = SMPTweaks.getPlugin();

        private final NamespacedKey namespace;
        private final PersistentDataType<T, T> type;

        private Key(String namespace, PersistentDataType<T, T> type) {
            this.namespace = new NamespacedKey(PLUGIN, namespace);
            this.type = type;
        }

        public NamespacedKey getKey() {
            return namespace;
        }

        public PersistentDataType<T, T> getType() {
            return type;
        }
    }
}
