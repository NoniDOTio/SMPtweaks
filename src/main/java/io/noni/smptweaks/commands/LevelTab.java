package io.noni.smptweaks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelTab implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filterSuggestions(args[0], Arrays.asList("lookup", "progress"));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("progress")) {
            return filterSuggestions(args[1], Arrays.asList("show", "hide"));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("lookup")) {
            List<String> playerNames = new ArrayList<>();
            for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return filterSuggestions(args[1], playerNames);

        } else if (args.length == 3 && !args[1].equalsIgnoreCase("hide")) {
            return filterSuggestions(args[2], Arrays.asList("all", "xp", "percentages", "none"));

        } else if (args.length == 4 && !args[1].equalsIgnoreCase("hide") && !args[2].equalsIgnoreCase("none")) {
            return filterSuggestions(args[3], Arrays.asList("chat", "actionbar"));
        }

        return null;
    }

    public List<String> filterSuggestions(String needle, List<String> haystack) {
        List<String> filteredSuggestions = new ArrayList<>();
        for(String hayball : haystack) {
            if (hayball.toLowerCase().startsWith(needle)) filteredSuggestions.add(hayball);
        }
        return filteredSuggestions;
    }
}
