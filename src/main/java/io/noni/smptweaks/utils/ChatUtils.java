package io.noni.smptweaks.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    /**
     * Message a player a positive or neutral message
     * @param player
     * @param message
     */
    public static void notify(Player player, String message) {
        player.sendMessage(ChatColor.AQUA + message);
    }


    /**
     * Message a player an array of positive or neutral messages
     * @param player
     * @param messages
     */
    public static void notify(Player player, String[] messages) {
        for (String message : messages) {
            notify(player, message);
        }
    }


    /**
     * Message a player a negative message
     * @param player
     * @param message
     */
    public static void negativeNotify(Player player, String message) {
        player.sendMessage(ChatColor.RED + message);
    }


    /**
     * Message a player an array of negative messages
     * @param player
     * @param messages
     */
    public static void negativeNotify(Player player, String[] messages) {
        for (String message : messages) {
            negativeNotify(player, message);
        }
    }


    /**
     * Announce a message
     * @param message
     */
    public static void announce(String message) {
        Bukkit.broadcastMessage(ChatColor.GREEN + message);
    }


    /**
     * Announce an array of messages
     * @param messages
     */
    public static void announce(String[] messages) {
        for (String message : messages) {
            announce(message);
        }
    }


    /**
     * Broadcast message
     * @param message
     */
    public static void chat(String message) {
        Bukkit.broadcastMessage(ChatColor.WHITE + message);
    }


}
