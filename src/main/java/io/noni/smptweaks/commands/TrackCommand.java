package io.noni.smptweaks.commands;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ActionBarUtils;
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

public class TrackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("track")) {
            return false;
        }

        if(args.length == 0) {
            return false;
        }

        final var player = (Player) sender;
        if(args[0].equalsIgnoreCase("clear")) {
            SMPtweaks.getPlayerTrackers().remove(player.getUniqueId());
            ChatUtils.commandResponse(player, TranslationUtils.get("tracker-removed"));
            ActionBarUtils.clear(player);
            return true;
        }

        final var targetPlayer = Bukkit.getPlayer(args[0]);
        if(targetPlayer == null) {
            ChatUtils.negative(player, TranslationUtils.get("error-online-player-not-found"));
            return true;
        }

        if(targetPlayer.equals(player)) {
            ChatUtils.negative(player, TranslationUtils.get("tracker-cannot-track-yourself"));
            return true;
        }

        SMPtweaks.getPlayerTrackers().put(player.getUniqueId(), targetPlayer.getUniqueId());
        ChatUtils.commandResponse(player, TranslationUtils.get("tracker-success", targetPlayer.getName()));
        return true;
    }
}
