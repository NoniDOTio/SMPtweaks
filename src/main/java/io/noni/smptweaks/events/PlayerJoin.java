package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.tasks.PlayerMetaLoaderTask;
import io.noni.smptweaks.tasks.RewardReminderTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoin implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerMeta playerMeta = new PlayerMeta(player);
        
        if(!playerMeta.isInitialized()) {
            playerMeta.initialize();
            playerMeta.pushToPDC();
        }

        new PlayerMetaLoaderTask(player).runTaskAsynchronously(SMPtweaks.getPlugin());
        new RewardReminderTask(player).runTaskLaterAsynchronously(SMPtweaks.getPlugin(), 240L);
    }
}
