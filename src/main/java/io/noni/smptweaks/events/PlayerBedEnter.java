package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerBedEnter implements Listener {

    @EventHandler
    void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK) && SMPTweaks.getPlugin().getConfig().getBoolean("disable_night_skip")) {
            Player player = e.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 480, 1));
        }
    }
}
