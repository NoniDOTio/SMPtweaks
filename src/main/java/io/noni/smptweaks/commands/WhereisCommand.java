package io.noni.smptweaks.commands;

import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

            final var targetPlayer = Bukkit.getPlayer(args[0]);
            final var player = (Player) sender;

            if(targetPlayer == null) {
                ChatUtils.negative(player, TranslationUtils.get("error-online-player-not-found"));
                return true;
            }

            var targetPlayerLocation = targetPlayer.getLocation();
            var worldName = targetPlayerLocation.getWorld().getName();
            var worldString = beautifyWorldName(worldName);

            var x = "" + ChatColor.WHITE + targetPlayerLocation.getBlockX() + ChatColor.RESET;
            var y = "" + ChatColor.WHITE + targetPlayerLocation.getBlockY() + ChatColor.RESET;
            var z = "" + ChatColor.WHITE + targetPlayerLocation.getBlockZ() + ChatColor.RESET;

            ChatUtils.commandResponse(player, TranslationUtils.get("whereis-message", new String[]{
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
            var completeText = TranslationUtils.get("whereis-in-nether");
            var partToColor = completeText.substring(completeText.lastIndexOf(" ") + 1);
            var remainingText = completeText.replace(partToColor, "");
            beautifiedWorldName = ChatColor.GOLD + remainingText + ChatColor.DARK_RED + partToColor + ChatColor.GOLD;
        } else if(worldName.contains("end")) {
            var completeText = TranslationUtils.get("whereis-in-end");
            var partToColor = completeText.substring(completeText.lastIndexOf(" ") + 1);
            var remainingText = completeText.replace(partToColor, "");
            beautifiedWorldName = ChatColor.GOLD + remainingText + ChatColor.DARK_PURPLE + partToColor + ChatColor.GOLD;
        } else {
            var completeText = TranslationUtils.get("whereis-in-overworld");
            var partToColor = completeText.substring(completeText.lastIndexOf(" ") + 1);
            var remainingText = completeText.replace(partToColor, "");
            beautifiedWorldName = ChatColor.GOLD + remainingText + ChatColor.GREEN + partToColor + ChatColor.GOLD;
        }
        return beautifiedWorldName;
    }
}
