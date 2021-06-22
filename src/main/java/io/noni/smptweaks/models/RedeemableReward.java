package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedeemableReward {
    ItemStack itemStack;
    Reward reward;

    int xp;
    int level;

    /**
     * RedeemableReward constructor
     */
    public RedeemableReward(Player player) {
        var serverLevelsEnabled = SMPtweaks.getCfg().getBoolean("server_levels.enabled");
        this.level = new PlayerMeta(player).getLevel();
        List<Reward> availableRewards = new ArrayList<>();

        // Calculate total weight of all rewards
        var totalWeight = 0.0;
        for(Reward singleReward : SMPtweaks.getConfigCache().getRewardsList()) {
            if(!serverLevelsEnabled || (level >= singleReward.getMinLevel() && level <= singleReward.getMaxLevel())) {
                availableRewards.add(singleReward);
                totalWeight += singleReward.getWeight();
            }
        }

        // Choose random item
        var index = 0;
        for (double rand = Math.random() * totalWeight; index < availableRewards.size() - 1; ++index) {
            rand -= availableRewards.get(index).getWeight();
            if (rand <= 0.0) break;
        }
        reward = availableRewards.get(index);

        // Assemble the ItemStack
        itemStack = reward.getItemStack();

        // Calculate amount
        if(serverLevelsEnabled && SMPtweaks.getCfg().getBoolean("rewards.scale_amount_with_level")) {
            float factor = (float)(reward.getMaxAmount() - reward.getMinAmount()) / ((float)(reward.getMaxLevel() + 1) - reward.getMinLevel());
            float adjustedAmount = factor * (level - reward.getMinLevel()) + reward.getMinAmount();
            int amount = Math.round(adjustedAmount);
            itemStack.setAmount(amount);
        } else {
            var rand = new Random();
            int amount = rand.nextInt((reward.getMaxAmount() + 1) - reward.getMinAmount()) + reward.getMinAmount();
            itemStack.setAmount(amount);
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
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     *
     * @return
     */
    public Reward getReward() {
        return reward;
    }
}
