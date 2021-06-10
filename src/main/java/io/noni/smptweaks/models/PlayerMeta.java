package io.noni.smptweaks.models;

import io.noni.smptweaks.utils.PDCUtils;
import org.bukkit.entity.Player;

public class PlayerMeta {
    Player player;

    Integer level;
    Integer totalXp;
    Integer xpDisplayMode;
    Integer specialDropAvailable;

    public PlayerMeta(Player player) {
        this.player = player;
        this.level = PDCUtils.get(player, PDCKey.LEVEL);
        this.totalXp = PDCUtils.get(player, PDCKey.TOTAL_XP);
        this.xpDisplayMode = PDCUtils.get(player, PDCKey.XP_DISPLAY_MODE);
        this.specialDropAvailable = PDCUtils.get(player, PDCKey.SPECIAL_DROP_AVAILABLE);
    }

    public PlayerMeta(Player player, int level, int totalXp, int xpDisplayMode, boolean specialDropAvailable) {
        this.player = player;
        this.level = level;
        this.totalXp = totalXp;
        this.xpDisplayMode = xpDisplayMode;
        this.specialDropAvailable = specialDropAvailable ? 1 : 0;
    }

    public void pushToPDC() {
        PDCUtils.set(player, PDCKey.LEVEL, level);
        PDCUtils.set(player, PDCKey.TOTAL_XP, totalXp);
        PDCUtils.set(player, PDCKey.XP_DISPLAY_MODE, xpDisplayMode);
        PDCUtils.set(player, PDCKey.SPECIAL_DROP_AVAILABLE, specialDropAvailable);
    }

    public boolean isInitialized() {
        return  PDCUtils.has(player, PDCKey.LEVEL) &&
                PDCUtils.has(player, PDCKey.TOTAL_XP) &&
                PDCUtils.has(player, PDCKey.XP_DISPLAY_MODE) &&
                PDCUtils.has(player, PDCKey.SPECIAL_DROP_AVAILABLE);
    }

    public void initialize() {
        this.level = 1;
        this.totalXp = 0;
        this.xpDisplayMode = 0;
        this.specialDropAvailable = 1;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public int getXpDisplayMode() {
        return xpDisplayMode;
    }

    public boolean isSpecialDropAvailable() {
        return specialDropAvailable == 1;
    }

    public void setXpDisplayMode(Integer xpDisplayMode) {
        this.xpDisplayMode = xpDisplayMode;
    }
}
