package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.tasks.DailyGiftReminderTask;
import io.noni.smptweaks.tasks.PlayerMetaLoaderTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        new PlayerMetaLoaderTask(player).runTaskAsynchronously(SMPTweaks.getPlugin());
        new DailyGiftReminderTask(player).runTaskLaterAsynchronously(SMPTweaks.getPlugin(), 240L);
    }
}
