package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerBedLeave implements Listener {

    @EventHandler
    void onPlayerBedEnter(PlayerBedLeaveEvent e) {
        var player = e.getPlayer();

        if (SMPtweaks.getCfg().getBoolean("disable_night_skip")) {
            ChatUtils.notify(player, TranslationUtils.get("bed-leave-no-time-skip"));
        }
        if (SMPtweaks.getCfg().getBoolean("health_regen_while_in_bed")) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }
    }
}
