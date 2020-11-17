package io.noni.smptweaks.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleUtils {

    /**
     * Display a title to a specific player
     * @param player
     * @param titleText
     * @param subTitleText
     */
    public static void send(Player player, String titleText, String subTitleText) {
        player.sendTitle(titleText, subTitleText, 8, 80, 16);
    }

    /**
     * Congratulate a player for leveling up
     * @param player
     * @param level
     */
    public static void congratulateLevelUp(Player player, int level, int xpLast, int xpTotal) {
        send(
                player,
                ChatColor.AQUA + "✪ " + level + " ✪",
                ChatColor.GOLD + "Level: " + ChatColor.WHITE + NumberUtils.format(xpLast) + "XP  " +
                ChatColor.GOLD + "Total: " + ChatColor.WHITE + NumberUtils.format(xpTotal) + "XP"
        );
    }
}
