package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RedeemableReward {
    private final ItemStack itemStack;
    private final Reward reward;
    private final int xp;

    /**
     * RedeemableReward constructor
     */
    public RedeemableReward(Player player) {
        var serverLevelsEnabled = SMPtweaks.getCfg().getBoolean("server_levels.enabled");
        int level = new PlayerMeta(player).getLevel();
        List<Reward> availableRewards = new ArrayList<>();

        // Calculate total weight of all rewards
        var totalWeight = 0.0;
        for(Reward singleReward : SMPtweaks.getConfigCache().getRewardsList()) {
            if(!serverLevelsEnabled || (level >= singleReward.minLevel() && level <= singleReward.maxLevel())) {
                availableRewards.add(singleReward);
                totalWeight += singleReward.weight();
            }
        }

        // Choose random item
        var index = 0;
        for (double rand = ThreadLocalRandom.current().nextFloat() * totalWeight; index < availableRewards.size() - 1; ++index) {
            rand -= availableRewards.get(index).weight();
            if (rand <= 0.0) break;
        }
        reward = availableRewards.get(index);

        // Assemble the ItemStack
        itemStack = reward.itemStack();

        // Calculate amount
        if(serverLevelsEnabled && SMPtweaks.getCfg().getBoolean("rewards.scale_amount_with_level")) {
            float factor = (float)(reward.maxAmount() - reward.minAmount()) / ((float)(reward.maxLevel() + 1) - reward.minLevel());
            float adjustedAmount = factor * (level - reward.minLevel()) + reward.minAmount();
            int amount = Math.round(adjustedAmount);
            itemStack.setAmount(amount);
        } else {
            var rand = new Random();
            int amount = rand.nextInt((reward.maxAmount() + 1) - reward.minAmount()) + reward.minAmount();
            itemStack.setAmount(amount);
        }

        // Calculate XP
        xp = SMPtweaks.getCfg().getInt("rewards.xp");
    }

    /**
     *
     * @return Amount of XP that is given to the redeeming player
     */
    public int getXp() {
        return xp;
    }

    /**
     *
     * @return ItemStack that is given to the redeeming player
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     *
     * @return Reward that is given to the redeeming player
     */
    public Reward getReward() {
        return reward;
    }
}
