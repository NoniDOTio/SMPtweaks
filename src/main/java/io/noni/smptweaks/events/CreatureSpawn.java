package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CreatureSpawn implements Listener {

    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) {
        if(
                e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL &&
                e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.RAID &&
                e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.PATROL
        ) {
            return;
        }

        var entityType = e.getEntity().getType();
        Float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(entityType);
        if(multiplier != null && ThreadLocalRandom.current().nextFloat() > multiplier) {
            e.setCancelled(true);
        }
    }
}
