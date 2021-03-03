package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMetaLoaderTask extends BukkitRunnable {
    private Player player;

    public PlayerMetaLoaderTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        LoggingUtils.info("Loading PlayerMeta for " + player.getName() + " with UUID " + player.getUniqueId().toString());
        SMPTweaks.getDB().getPlayerMeta(player).pushToPDC();
    }
}
