package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEnter implements Listener {

    @EventHandler
    void onBedEnter(PlayerBedEnterEvent e) {
        if(SMPTweaks.getPlugin().getConfig().getBoolean("disable_night_skip")) {
            ChatUtils.negativeNotify(e.getPlayer(),"Du fühlst dich erholt, aber die Nacht ist noch lang...");
        }
    }
}
