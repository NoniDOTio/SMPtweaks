package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerBedEnter implements Listener {

    @EventHandler
    void onPlayerBedEnter(PlayerBedEnterEvent e) {
        var player = e.getPlayer();

        if (!e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }
        if (SMPtweaks.getCfg().getBoolean("health_regen_while_in_bed")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 480, 1));
        }
    }
}
