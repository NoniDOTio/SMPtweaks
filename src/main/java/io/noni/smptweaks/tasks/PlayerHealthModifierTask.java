package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerHealthModifierTask extends BukkitRunnable {
    private Player player;

    public PlayerHealthModifierTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        int health = SMPTweaks.getCfg().getInt("respawn_health");
        player.setHealth(health);
    }
}
