package io.noni.smptweaks.events;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import io.noni.smptweaks.SMPtweaks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PaperPreCreatureSpawn implements Listener {

    @EventHandler
    void onPreCreatureSpawn(PreCreatureSpawnEvent e) {
        if(e.getReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        var entityType = e.getType();
        Float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(entityType);
        if(multiplier != null && Math.random() > multiplier) {
            e.setShouldAbortSpawn(true);
        }

        // Shulker spawn logic
        if(SMPtweaks.getCfg().getBoolean("shulkers_spawn_naturally")) {
            Location loc = e.getSpawnLocation();
            if(Math.random() > 0.2 || loc.getBlock().getBiome() != Biome.END_HIGHLANDS) {
                return;
            }
            if(loc.subtract(0, 1, 0).getBlock().getType() != Material.PURPUR_BLOCK) {
                return;
            }
            for(Entity nearbyEntity : loc.getChunk().getEntities()) {
                if(nearbyEntity.getType() == EntityType.SHULKER) return;
            }
            if(loc.getWorld() != null) {
                e.setShouldAbortSpawn(true);
                loc.getWorld().spawn(e.getSpawnLocation(), Shulker.class);
            }
        }
    }
}