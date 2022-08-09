package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ExperienceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.ThreadLocalRandom;

public class EntityDeath implements Listener {

    @EventHandler
    void onEntityDeath(EntityDeathEvent e) {
        var killer = e.getEntity().getKiller();
        if(killer == null) return;

        var entityCustomDrops = SMPtweaks.getConfigCache().getEntityCustomDrops();
        var entityType = e.getEntityType();
        var customDrop = entityCustomDrops.get(entityType);
        if(customDrop == null) return;

        var possibleDrops = customDrop.possibleDrops();
        var discardVanillaDrops = customDrop.discardVanillaDrops();
        var xpDrop = customDrop.xp();
        var commands = customDrop.commands();

        // Discard vanilla drops
        if(discardVanillaDrops) e.getDrops().clear();

        // Overwrite XP
        if(xpDrop != null) {
            if(entityType == EntityType.ENDER_DRAGON) {
                xpDrop = xpDrop - 800;
                if(xpDrop > 0) {
                    ExperienceUtils.spawnOrbs(xpDrop, 50, e.getEntity().getWorld(), new Location(e.getEntity().getWorld(), 0, 74, 7));
                }
            } else {
                e.setDroppedExp(xpDrop);
            }
        }

        // Select drops
        if(possibleDrops != null) possibleDrops.forEach((drop, chance) -> {
            if(chance < ThreadLocalRandom.current().nextFloat()) return;

            // Drop ItemStack
            if(drop.itemStack() != null) {
                if(entityType == EntityType.ENDER_DRAGON) {
                    e.getEntity().getWorld().dropItemNaturally(new Location(e.getEntity().getWorld(), 0, 74, 7), drop.itemStack());
                } else {
                    e.getDrops().add(drop.itemStack());
                }
            }

            // Run commands
            var dropCommands = drop.commands();
            if(dropCommands != null) dropCommands.forEach(command ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", killer.getName()))
            );
        });

        // Run commands
        if(commands != null) commands.forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("@p", killer.getName()))
        );
    }
}
