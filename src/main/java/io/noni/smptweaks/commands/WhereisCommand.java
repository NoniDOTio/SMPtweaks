package io.noni.smptweaks.commands;

import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhereisCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("whereis")) {

            if(args.length == 0) {
                return false;
            }

            final Player targetPlayer = Bukkit.getPlayer(args[0]);

            if(targetPlayer == null) {
                ChatUtils.negative((Player) sender, TranslationUtils.get("generic-online-player-not-found"));
                return true;
            }

            Location targetPlayerLocation = targetPlayer.getLocation();
            String worldName = targetPlayerLocation.getWorld().getName();
            String worldString = beautifyWorldName(worldName);

            String x = "" + ChatColor.WHITE + targetPlayerLocation.getBlockX() + ChatColor.RESET;
            String y = "" + ChatColor.WHITE + targetPlayerLocation.getBlockY() + ChatColor.RESET;
            String z = "" + ChatColor.WHITE + targetPlayerLocation.getBlockZ() + ChatColor.RESET;

            ChatUtils.commandResponse((Player) sender, TranslationUtils.get("whereis-message", new String[]{
                    targetPlayer.getName(),
                    worldString,
                    x + " " + y + " " + z
            }));
            return true;
        }

        return false;
    }

    /**
     * Beautify world name
     * @param worldName raw world name (eg. world3_the_nether)
     * @return beautified world name (eg. Nether)
     */
    private String beautifyWorldName(String worldName) {
        String beautifiedWorldName;
        if(worldName.contains("nether")) {
            beautifiedWorldName = ChatColor.DARK_RED + TranslationUtils.get("whereis-in-nether") + ChatColor.GOLD;
        } else if(worldName.contains("end")) {
            beautifiedWorldName = ChatColor.DARK_PURPLE + TranslationUtils.get("whereis-in-end") + ChatColor.GOLD;
        } else {
            beautifiedWorldName = ChatColor.GREEN + TranslationUtils.get("whereis-in-overworld") + ChatColor.GOLD;
        }
        return beautifiedWorldName;
    }
}
