package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMetaLoaderTask extends BukkitRunnable {
    private final Player player;

    public PlayerMetaLoaderTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        var playerMeta = SMPtweaks.getDB().getPlayerMeta(player);

        if(playerMeta != null) {
            LoggingUtils.info("Loading meta data for " + player.getName() + " with UUID " + player.getUniqueId());
            playerMeta.pushToPDC();
        } else {
            LoggingUtils.info("Could not find existing meta data for " + player.getName() + " with UUID " + player.getUniqueId());
        }
    }
}
