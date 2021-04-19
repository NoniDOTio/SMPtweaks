package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMetaLoaderTask extends BukkitRunnable {
    private Player player;

    public PlayerMetaLoaderTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        LoggingUtils.info("Loading PlayerMeta for " + player.getName() + " with UUID " + player.getUniqueId().toString());
        PlayerMeta playerMeta = SMPTweaks.getDB().getPlayerMeta(player);

        if(playerMeta != null) {
            playerMeta.pushToPDC();
        } else {
            ChatUtils.negative(player, "Deine Metadaten konnten nicht geladen werden. Dein Fortschritt wird eventuell nicht gespeichert. Bitte trenne die Verbindung mit dem Server und versuche es erneut.");
        }
    }
}
