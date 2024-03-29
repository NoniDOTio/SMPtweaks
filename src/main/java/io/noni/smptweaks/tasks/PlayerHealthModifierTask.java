package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerHealthModifierTask extends BukkitRunnable {
    private final Player player;

    public PlayerHealthModifierTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        var health = SMPtweaks.getCfg().getInt("respawn_health");
        player.setHealth(health);
    }
}
