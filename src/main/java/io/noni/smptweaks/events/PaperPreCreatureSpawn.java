package io.noni.smptweaks.events;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import io.noni.smptweaks.SMPtweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PaperPreCreatureSpawn implements Listener {

    @EventHandler
    void onPreCreatureSpawn(PreCreatureSpawnEvent e) {
        if(e.getReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        var entityType = e.getType();
        Float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(entityType);
        if(multiplier != null) {
            if(multiplier == 0) {
                e.setShouldAbortSpawn(true);
                e.setCancelled(true);
            } else if(ThreadLocalRandom.current().nextFloat() > multiplier) {
                e.setCancelled(true);
            }
        }
    }
}