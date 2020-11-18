package io.noni.smptweaks.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBarUtils {


    /**
     * Send a positive or neutral action bar message to a player
     * @param player
     * @param message
     */
    public static void notify(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + message));
    }


    /**
     * Send a negative action bar message to a player
     * @param player
     * @param message
     */
    public static void negativeNotify(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + message));
    }
}
