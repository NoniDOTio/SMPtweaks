package io.noni.smptweaks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsume implements Listener {

    @EventHandler
    void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        float saturationBuff;
        switch (e.getItem().getType()) {
            case PUMPKIN_PIE:
                saturationBuff = 8.0F;
                break;
            case BEETROOT_SOUP, MUSHROOM_STEW:
                saturationBuff = 4.8F;
                break;
            case BAKED_POTATO, BREAD, COOKIE:
                saturationBuff = 1.2F;
                break;
            default:
                return;
        }

        Player player = e.getPlayer();
        player.setSaturation(player.getSaturation() + saturationBuff);
    }
}
