package io.noni.smptweaks.events;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PaperShulkerSpawn implements Listener {

    @EventHandler
    void onPreCreatureSpawn(PreCreatureSpawnEvent e) {
        if(
                e.getReason() != CreatureSpawnEvent.SpawnReason.NATURAL &&
                e.getReason() != CreatureSpawnEvent.SpawnReason.RAID &&
                e.getReason() != CreatureSpawnEvent.SpawnReason.PATROL
        ) {
            return;
        }
        if(ThreadLocalRandom.current().nextFloat() > 0.2) {
            return;
        }
        var loc = e.getSpawnLocation();
        if(loc.getBlock().getBiome() != Biome.END_HIGHLANDS) {
            return;
        }
        if(loc.subtract(0, 1, 0).getBlock().getType() != Material.PURPUR_BLOCK) {
            return;
        }
        for(Entity nearbyEntity : loc.getChunk().getEntities()) {
            if(nearbyEntity.getType() == EntityType.SHULKER) return;
        }
        if(loc.getWorld() != null) {
            e.setCancelled(true);
            loc.getWorld().spawn(e.getSpawnLocation(), Shulker.class);
        }
    }
}