package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.tasks.PlayerMetaStorerTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();

        // Update SMPtweaks player in DB
        new PlayerMetaStorerTask(player).runTaskAsynchronously(SMPtweaks.getPlugin());
    }
}
