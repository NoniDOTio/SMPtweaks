package io.noni.smptweaks.commands;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.models.Level;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ActionBarUtils;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.NumberUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CoordsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("coords") || !(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("toggle"))) {
            toggleCoordinatesDisplay(player);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
            enableCoordinatesDisplay(player);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
            disableCoordinatesDisplay(player);
        } else if (args[0].equalsIgnoreCase("share")) {
            String title = String.join(" ", args).replace("share", "").trim();

            if (!title.isEmpty()) {
                title = "(" + title + ")";
            }

            ChatUtils.broadcast(
                TranslationUtils.get("coords-share", new String[]{
                    player.getName(),
                    "" + player.getLocation().getBlockX(),
                    "" + player.getLocation().getBlockY(),
                    "" + player.getLocation().getBlockZ(),
                    title
                })
            );
        } else if (args[0].equalsIgnoreCase("copy")) {
            String title = String.join(" ", args).replace("share", "").trim();

            if (!title.isEmpty()) {
                title = "(" + title + ")";
            }

            ChatUtils.broadcast(
                TranslationUtils.get("coords-copy", new String[]{
                    player.getName(),
                    "" + player.getLocation().getBlockX(),
                    "" + player.getLocation().getBlockY(),
                    "" + player.getLocation().getBlockZ(),
                    title
                })
            );
        }

        return true;
    }

    /**
     * Toggle coordinates display for player
     * @param player
     */
    private void toggleCoordinatesDisplay(Player player) {
        if (SMPtweaks.getCoordinateDisplays().contains(player.getUniqueId())) {
            SMPtweaks.getCoordinateDisplays().remove(player.getUniqueId());
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-off"));
            ActionBarUtils.clear(player);
        } else {
            SMPtweaks.getCoordinateDisplays().add(player.getUniqueId());
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-on"));

            // Remove player UUID from playerTrackers to avoid collision
            SMPtweaks.getPlayerTrackers().remove(player.getUniqueId());
        }
    }

    /**
     * Enable coordinates display for player
     * @param player
     */
    private void enableCoordinatesDisplay(Player player) {
        if (SMPtweaks.getCoordinateDisplays().contains(player.getUniqueId())) {
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-already-on"));

            // Remove player UUID from playerTrackers to avoid collision
            SMPtweaks.getPlayerTrackers().remove(player.getUniqueId());
        } else {
            SMPtweaks.getCoordinateDisplays().add(player.getUniqueId());
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-on"));

            // Remove player UUID from playerTrackers to avoid collision
            SMPtweaks.getPlayerTrackers().remove(player.getUniqueId());
        }
    }

    /**
     * Disable coordinates display for player
     * @param player
     */
    private void disableCoordinatesDisplay(Player player) {
        if (SMPtweaks.getCoordinateDisplays().contains(player.getUniqueId())) {
            SMPtweaks.getCoordinateDisplays().remove(player.getUniqueId());
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-off"));
            ActionBarUtils.clear(player);
        } else {
            ChatUtils.commandResponse(player, TranslationUtils.get("coords-already-off"));
            ActionBarUtils.clear(player);
        }
    }

}
