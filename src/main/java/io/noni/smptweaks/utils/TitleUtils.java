package io.noni.smptweaks.utils;

import org.bukkit.entity.Player;

public class TitleUtils {

    private TitleUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Display title to a specific player
     * @param player
     * @param titleText
     * @param subTitleText
     */
    public static void send(Player player, String titleText, String subTitleText) {
        player.sendTitle(titleText, subTitleText, 8, 80, 16);
    }
}
