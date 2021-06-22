package io.noni.smptweaks.tasks;

import io.noni.smptweaks.utils.ExperienceUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardDistrubutorTask implements Runnable {
    private final Player player;
    private final int xp;
    private final ItemStack itemStack;

    public RewardDistrubutorTask(Player player, int xp, ItemStack itemStack) {
        this.player = player;
        this.xp = xp;
        this.itemStack = itemStack;
    }

    @Override
    public void run() {
        player.getInventory().addItem(itemStack);
        if(xp > 0) {
            ExperienceUtils.spawnOrbs(xp, xp > 100 ? 50 : 10, player);
        }
    }

}