package io.noni.smptweaks.commands;

import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

            var x = "" + targetPlayerLocation.getBlockX();
            var y = "" + targetPlayerLocation.getBlockY();
            var z = "" + targetPlayerLocation.getBlockZ();

            TextComponent mainComponent = new TextComponent(
                    ChatColor.GOLD +
                    TranslationUtils.get("whereis-message", new String[]{
                            targetPlayer.getName(),
                            worldString
                    }) + " "
            );
            TextComponent coordinatesComponent = new TextComponent("" + ChatColor.WHITE + x + " " + y + " " + z);
            coordinatesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    TranslationUtils.get("click-to-copy")
            )));
            coordinatesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, x + " " + y + " " + z));
            mainComponent.addExtra(coordinatesComponent);
            ChatUtils.chatRaw(player, mainComponent);
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
