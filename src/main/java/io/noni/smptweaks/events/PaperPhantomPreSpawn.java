package io.noni.smptweaks.events;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import io.noni.smptweaks.SMPtweaks;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ThreadLocalRandom;

public class PaperPhantomPreSpawn implements Listener {

    @EventHandler
    void onPaperPhantomPreSpawn(PhantomPreSpawnEvent e) {
        float multiplier = SMPtweaks.getConfigCache().getEntitySpawnRates().get(EntityType.PHANTOM);
        if(multiplier == 0) {
            e.setShouldAbortSpawn(true);
            e.setCancelled(true);
        } else if(ThreadLocalRandom.current().nextFloat() > multiplier) {
            e.setCancelled(true);
        }
    }
}
