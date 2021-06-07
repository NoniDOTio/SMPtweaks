package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class TimeSkip implements Listener {

    @EventHandler
    void onTimeSkip(TimeSkipEvent e) {
        if(!SMPtweaks.getPlugin().getConfig().getBoolean("disable_night_skip")) {
            return;
        }
        if(e.getSkipReason().equals(TimeSkipEvent.SkipReason.NIGHT_SKIP)) {
            e.setCancelled(true);
        }
    }
}
