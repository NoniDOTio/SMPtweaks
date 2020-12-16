package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class PDCUtils {

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
        public static final Key<Integer> LEVEL;
        public static final Key<Integer> TOTAL_XP;
        public static final Key<Integer> XP_DISPLAY_MODE;
        public static final Key<Integer> SPECIAL_DROP_AVAILABLE;

        static {
            LEVEL = new Key("level", PersistentDataType.INTEGER);
            TOTAL_XP = new Key("total_xp", PersistentDataType.INTEGER);
            XP_DISPLAY_MODE = new Key("xp_display_mode", PersistentDataType.INTEGER);
            SPECIAL_DROP_AVAILABLE = new Key("special_drop_available", PersistentDataType.INTEGER);
        }

        private final NamespacedKey namespace;
        private final PersistentDataType<T, T> type;

        private Key(String namespace, PersistentDataType<T, T> type) {
            this.namespace = new NamespacedKey(SMPTweaks.getPlugin(), namespace);
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
