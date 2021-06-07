package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class PDCKey<T> {
    public static final PDCKey<Integer> LEVEL;
    public static final PDCKey<Integer> TOTAL_XP;
    public static final PDCKey<Integer> XP_DISPLAY_MODE;
    public static final PDCKey<Integer> SPECIAL_DROP_AVAILABLE;

    static {
        LEVEL = new PDCKey("level", PersistentDataType.INTEGER);
        TOTAL_XP = new PDCKey("total_xp", PersistentDataType.INTEGER);
        XP_DISPLAY_MODE = new PDCKey("xp_display_mode", PersistentDataType.INTEGER);
        SPECIAL_DROP_AVAILABLE = new PDCKey("special_drop_available", PersistentDataType.INTEGER);
    }

    public final NamespacedKey namespace;
    public final PersistentDataType<T, T> type;

    private PDCKey(String namespace, PersistentDataType<T, T> type) {
        this.namespace = new NamespacedKey(SMPtweaks.getPlugin(), namespace);
        this.type = type;
    }

    public NamespacedKey getKey() {
        return namespace;
    }

    public PersistentDataType<T, T> getType() {
        return type;
    }
}
