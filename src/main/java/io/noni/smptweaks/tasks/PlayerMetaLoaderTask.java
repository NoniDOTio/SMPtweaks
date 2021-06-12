package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.models.PlayerMeta;
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
        PlayerMeta playerMeta = SMPtweaks.getDB().getPlayerMeta(player);

        if(playerMeta != null) {
            LoggingUtils.info("Loading PlayerMeta for " + player.getName() + " with UUID " + player.getUniqueId().toString());
            playerMeta.pushToPDC();
        } else {
            LoggingUtils.info("Could not find existing PlayerMeta for " + player.getName() + " with UUID " + player.getUniqueId().toString());
        }
    }
}
