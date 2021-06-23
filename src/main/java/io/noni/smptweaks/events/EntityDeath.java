package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

    @EventHandler
    void onEntityDeath(EntityDeathEvent e) {
        var entityCustomDrops = SMPtweaks.getConfigCache().getEntityCustomDrops();
        var entityType = e.getEntityType();
        var possibleDrops = entityCustomDrops.get(entityType);
        if(possibleDrops == null) return;

        // Select drops
        possibleDrops.forEach((itemStack, dropChance) -> {
            if(dropChance < Math.random()) return;
            if(entityType == EntityType.ENDER_DRAGON) {
                e.getEntity().getWorld().dropItemNaturally(new Location(e.getEntity().getWorld(), 0, 70, 6), itemStack);
            } else {
                e.getDrops().add(itemStack);
            }
        });
    }
}
