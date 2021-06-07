package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.tasks.PlayerFoodLevelModifierTask;
import io.noni.smptweaks.tasks.PlayerHealthModifierTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerRespawn implements Listener {

    @EventHandler
    void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        new PlayerHealthModifierTask(player).runTask(SMPtweaks.getPlugin());
        new PlayerFoodLevelModifierTask(player).runTask(SMPtweaks.getPlugin());
    }
}
