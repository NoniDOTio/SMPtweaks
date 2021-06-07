package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedeemableReward {
    ItemStack item;
    Reward reward;

    int xp;
    int level;


    /**
     * DailyGift constructor
     */
    public RedeemableReward(Player player) {
        this.level = new PlayerMeta(player).getLevel();
        List<Reward> availableRewards = new ArrayList<>();
        boolean serverLevelsEnabled = SMPtweaks.getCfg().getBoolean("server_levels.enabled");

        // Calculate total weight of all rewards
        double totalWeight = 0.0;
        for(Reward reward : SMPtweaks.getConfigCache().getRewardsList()) {
            if(!serverLevelsEnabled || (level >= reward.getMinLevel() && level <= reward.getMaxLevel())) {
                availableRewards.add(reward);
                totalWeight += reward.getWeight();
            }
        }

        // Choose random item
        int index = 0;
        for (double rand = Math.random() * totalWeight; index < availableRewards.size() - 1; ++index) {
            rand -= availableRewards.get(index).getWeight();
            if (rand <= 0.0) break;
        }
        reward = availableRewards.get(index);

        // Calculate amount
        item = new ItemStack(reward.getMaterial());
        if(SMPtweaks.getCfg().getBoolean("rewards.scale_amount_with_level")) {
            float factor = (float)(reward.getMaxAmount() - reward.getMinAmount()) / ((float)(reward.getMaxLevel() + 1) - reward.getMinLevel());
            float adjustedAmount = factor * (level - reward.getMinLevel()) + reward.getMinAmount();
            int amount = Math.round(adjustedAmount);
            item.setAmount(amount);
        } else {
            Random rand = new Random();
            int amount = rand.nextInt((reward.getMaxAmount() + 1) - reward.getMinAmount()) + reward.getMinAmount();
            item.setAmount(amount);
        }

        // Calculate XP
        xp = SMPtweaks.getCfg().getInt("rewards.xp");
    }

    /**
     *
     * @return
     */
    public int getXp() {
        return xp;
    }

    /**
     *
     * @return
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     *
     * @return
     */
    public Reward getReward() {
        return reward;
    }
}
