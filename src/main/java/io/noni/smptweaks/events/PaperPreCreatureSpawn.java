package io.noni.smptweaks.events;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;


public class PaperPreCreatureSpawn implements Listener {

    @EventHandler
    void onPreCreatureSpawn(PreCreatureSpawnEvent e) {
        if(e.getReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        EntityType entityType = e.getType();
        Float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(entityType);
        if(multiplier != null && Math.random() < multiplier) {
            e.setShouldAbortSpawn(true);
        }
    }
}