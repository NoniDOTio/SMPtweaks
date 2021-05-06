package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerBedLeave implements Listener {

    @EventHandler
    void onPlayerBedEnter(PlayerBedLeaveEvent e) {
        if (!SMPTweaks.getPlugin().getConfig().getBoolean("disable_night_skip")) {
            return;
        }

        Player player = e.getPlayer();
        ChatUtils.notify(player, "Du fühlst dich erholt, aber die Nacht ist noch lang...");
        player.removePotionEffect(PotionEffectType.REGENERATION);
    }
}
