package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.tasks.TrackerRemoverTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TrackedPlayerLeave implements Listener {

    @EventHandler
    void onTrackedPlayerLeave(PlayerQuitEvent e) {
        var player = e.getPlayer();

        // Remove playerTrackers tracking this player
        new TrackerRemoverTask(player.getUniqueId()).runTaskLater(SMPtweaks.getPlugin(), 40L);
    }
}
