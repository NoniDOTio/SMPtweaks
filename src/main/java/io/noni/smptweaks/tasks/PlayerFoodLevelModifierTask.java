package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerFoodLevelModifierTask extends BukkitRunnable {
    private final Player player;

    public PlayerFoodLevelModifierTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        int foodLevel = SMPTweaks.getCfg().getInt("respawn_food_level");
        player.setFoodLevel(foodLevel);
    }
}
