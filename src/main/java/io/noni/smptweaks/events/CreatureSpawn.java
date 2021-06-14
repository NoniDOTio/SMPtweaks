package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawn implements Listener {

    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        EntityType entityType = e.getEntity().getType();
        Float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(entityType);
        if(multiplier != null && Math.random() < multiplier) {
            e.setCancelled(true);
        }

    }
}
