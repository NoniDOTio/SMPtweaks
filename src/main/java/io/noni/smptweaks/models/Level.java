package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.PDCUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class Level {
    private final int level;
    private final int totalXp;
    private final int currentXp;
    private final int threshold;
    private final int untilXp;

    final static List<Integer> THRESHOLDS = SMPtweaks.getPlugin().getConfig().getIntegerList("server_levels.thresholds");

    public Level(int totalXp) {
        int i;
        for(i = 0; i < THRESHOLDS.size(); i++) {
            if(totalXp < THRESHOLDS.get(i)) {
                break;
            }
        }
        this.level = i;
        this.threshold = THRESHOLDS.get(i - 1);
        this.totalXp = totalXp;
        this.currentXp = totalXp - this.threshold;
        int untilXp;
        try {
            untilXp = THRESHOLDS.get(i) - totalXp;
        } catch (IndexOutOfBoundsException e) {
            untilXp = 0;
        }
        this.untilXp = untilXp;
    }

    public Level(Player player) {
        this.totalXp = PDCUtils.get(player, PDCKey.TOTAL_XP);
        int i;
        for(i = 0; i < THRESHOLDS.size(); i++) {
            if(totalXp < THRESHOLDS.get(i)) {
                break;
            }
        }
        this.level = i;
        this.threshold = THRESHOLDS.get(i - 1);
        this.currentXp = totalXp - this.threshold;
        int untilXp;
        try {
            untilXp = THRESHOLDS.get(i) - totalXp;
        } catch (IndexOutOfBoundsException e) {
            untilXp = 0;
        }
        this.untilXp = untilXp;
    }

    public boolean hasLevelledUp(int lastAmountAdded) {
        return totalXp - lastAmountAdded < this.threshold;
    }

    public void pushToPDC(Player player) {
        PDCUtils.set(player, PDCKey.LEVEL, this.level);
        PDCUtils.set(player, PDCKey.TOTAL_XP, this.totalXp);
    }

    public int getLevel() {
        return level;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public int getUntilXp() {
        return untilXp;
    }

    public double getProgessPercentage() {
        double progress = (double) this.currentXp / (this.untilXp + this.currentXp);
        return progress * 100;
    }

    /**
     * Get how much xp is needed for the level
     */
    public int getSingleXpRequiredForLevel() {
        return THRESHOLDS.get(this.level - 1) - THRESHOLDS.get(this.level - 2);
    }

    /**
     * Get how much xp is needed in total for the level
     */
    public int getTotalXpRequiredForLevel() {
        return THRESHOLDS.get(this.level - 1);
    }
}
