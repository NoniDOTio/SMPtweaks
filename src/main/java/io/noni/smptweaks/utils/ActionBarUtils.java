package io.noni.smptweaks.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBarUtils {

    private ActionBarUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Send a positive or neutral action bar message to a player
     * @param player Player to display the action bar to
     * @param message Content of the message
     */
    public static void notify(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + message));
    }

    /**
     * Send a negative action bar message to a player
     * @param player Player to display the action bar to
     * @param message Content of the message
     */
    public static void negativeNotify(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + message));
    }

    /**
     * Send a raw action bar textComponent to a player
     * @param player Player to display the action bar to
     * @param textComponent Content of the textComponent
     */
    public static void raw(@NotNull Player player, @NotNull TextComponent textComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }

    /**
     * Clear action bar by sending an empty message to a player
     * @param player Player whose action bar to clear
     */
    public static void clear(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }
}
