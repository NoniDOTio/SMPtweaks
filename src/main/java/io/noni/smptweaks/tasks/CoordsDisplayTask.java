package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ActionBarUtils;
import io.noni.smptweaks.utils.NumberUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CoordsDisplayTask implements Runnable {

    @Override
    public void run() {
        SMPtweaks.getCoordinateDisplays().forEach(playerUuid -> {
            Player player = Bukkit.getPlayer(playerUuid);

            if (player == null) {
                return;
            }

            Location playerLocation = player.getLocation();
            ActionBarUtils.raw(player, new TextComponent(
                ChatColor.GOLD + "X: " + ChatColor.WHITE + NumberUtils.format(playerLocation.getBlockX()) + " " +
                ChatColor.GOLD + "Y: " + ChatColor.WHITE + NumberUtils.format(playerLocation.getBlockY()) + " " +
                ChatColor.GOLD + "Z: " + ChatColor.WHITE + NumberUtils.format(playerLocation.getBlockZ())
            ));
        });
    }
}
