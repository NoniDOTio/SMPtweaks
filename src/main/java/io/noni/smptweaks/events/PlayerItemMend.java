package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

public class PlayerItemMend implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        // Apply repair amount multiplier
        var repairAmoundMultiplier = SMPtweaks.getCfg().getDouble("mending_repair_amount_multiplier");
        var repairAmount = (int) Math.round(e.getRepairAmount() * repairAmoundMultiplier);
        e.setRepairAmount(repairAmount);
    }
}
