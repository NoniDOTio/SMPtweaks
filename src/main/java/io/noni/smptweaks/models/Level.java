package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.PDCUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class Level {
    int level;
    int totalXp;
    int currentXp;
    int untilXp;
    int threshold;

    List<Integer> thresholds = SMPtweaks.getPlugin().getConfig().getIntegerList("server_levels.thresholds");

    public Level(int totalXp) {
        int i;
        for(i = 0; i <= thresholds.size(); i++) {
            if(totalXp < thresholds.get(i)) {
                break;
            }
        }
        this.level = i;
        this.threshold = thresholds.get(i - 1);
        this.totalXp = totalXp;
        this.currentXp = totalXp - this.threshold;
        this.untilXp = thresholds.get(i) - totalXp;
    }

    public Level(Player player) {
        int totalXp = PDCUtils.get(player, PDCKey.TOTAL_XP);
        int i;
        for(i = 0; i <= thresholds.size(); i++) {
            if(totalXp < thresholds.get(i)) {
                break;
            }
        }
        this.level = i;
        this.threshold = thresholds.get(i - 1);
        this.totalXp = totalXp;
        this.currentXp = totalXp - this.threshold;
        this.untilXp = thresholds.get(i) - totalXp;
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
        return thresholds.get(this.level - 1) - thresholds.get(this.level - 2);
    }

    /**
     * Get how much xp is needed in total for the level
     */
    public int getTotalXpRequiredForLevel() {
        return thresholds.get(this.level - 1);
    }
}
