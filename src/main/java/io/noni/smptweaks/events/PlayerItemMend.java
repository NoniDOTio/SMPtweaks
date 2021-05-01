package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

public class PlayerItemMend implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        // Apply repair amount multiplier
        double repairAmoundMultiplier = SMPTweaks.getCfg().getDouble("mending_repair_amount_multiplier");
        int repairAmount = (int) Math.round(e.getRepairAmount() * repairAmoundMultiplier);
        e.setRepairAmount(repairAmount);
    }
}
