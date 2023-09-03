package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ActionBarUtils;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrackerRemoverTask extends BukkitRunnable {
    private final UUID uuid;

    public TrackerRemoverTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {
        // Get player from UUID
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        // Ensure player has not reconnected in the meanwhile
        if (player.isOnline()) {
            return;
        }

        // Remove playerTracker created by this player (0..1)
        SMPtweaks.getPlayerTrackers().remove(uuid);

        // Remove playerTrackers tracking this player (0..n)
        List<UUID> playerUuidsWithInactiveTrackerUuid = new ArrayList<>();
        SMPtweaks.getPlayerTrackers().forEach((key, value) -> {
            if (value.equals(uuid)) {
                playerUuidsWithInactiveTrackerUuid.add(key);
            }
        });

        for(var playerUuidWithInactiveTrackerUuid : playerUuidsWithInactiveTrackerUuid) {
            SMPtweaks.getPlayerTrackers().remove(playerUuidWithInactiveTrackerUuid);

            Player playerWithInactiveTrackerUuid = Bukkit.getPlayer(playerUuidWithInactiveTrackerUuid);
            if (playerWithInactiveTrackerUuid == null) {
                continue;
            }

            ChatUtils.notify(playerWithInactiveTrackerUuid, TranslationUtils.get("tracker-auto-removed"));
            ActionBarUtils.clear(playerWithInactiveTrackerUuid);
        }
    }
}
