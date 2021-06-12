package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMetaStorerTask extends BukkitRunnable {
    private final Player player;

    public PlayerMetaStorerTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        LoggingUtils.info("Storing PlayerMeta for " + player.getName() + " with UUID " + player.getUniqueId().toString());
        SMPtweaks.getDB().savePlayerMeta(player);
    }
}