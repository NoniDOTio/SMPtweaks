package io.noni.smptweaks.commands;

import io.noni.smptweaks.models.Level;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.NumberUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("level") || !(sender instanceof Player)) {
            return false;
        }

        var player = (Player) sender;

        if (args.length == 0) {
            printProgress(player, player);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("lookup")) {
            ChatUtils.commandResponse(player, TranslationUtils.get("level-lookup-no-player-specified"));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("lookup")) {
            var playerToLookUp = Bukkit.getPlayer(args[1]);
            if(playerToLookUp == null) {
                ChatUtils.commandResponse(player, TranslationUtils.get("error-online-player-not-found"));
                return true;
            }
            printProgress(player, playerToLookUp);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("progress")) {
            printProgress(player, player);

        } else if (
                (args.length == 2 && args[1].equalsIgnoreCase("hide")) ||
                (args.length == 3 && args[1].equalsIgnoreCase("show") && args[2].equalsIgnoreCase("none"))
        ) {
            var playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(0);
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        } else if (args.length == 2 && args[1].equalsIgnoreCase("show")) {
            var playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(19);
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        } else if (args.length == 3 && args[1].equalsIgnoreCase("show")) {
            ChatUtils.commandResponse(player, TranslationUtils.get("level-progress-no-display-method-specified"));

        } else if (args.length == 4 && args[1].equalsIgnoreCase("show")) {
            var xpDisplayModeBuilder = new StringBuilder();
            switch (args[3]) {
                case "chat" -> xpDisplayModeBuilder.append(1);
                case "actionbar" -> xpDisplayModeBuilder.append(2);
                default -> xpDisplayModeBuilder.append(0);
            }

            switch (args[2]) {
                case "xp" -> xpDisplayModeBuilder.append(1);
                case "percentages" -> xpDisplayModeBuilder.append(2);
                case "all" -> xpDisplayModeBuilder.append(9);
                default -> xpDisplayModeBuilder.append(0);
            }
            var playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(Integer.parseInt(xpDisplayModeBuilder.toString()));
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        }

        return true;
    }

    private void printProgress(Player player, Player playerToLookUp) {
        int totalXp = new PlayerMeta(playerToLookUp).getTotalXp();
        var level = new Level(totalXp);
        String progressBar = makeProgressBar(level);

        String firstLine;
        if(player.equals(playerToLookUp)) {
            firstLine = TranslationUtils.get("level-lookup-self", new String[]{
                    "" + level.getLevel(),
                    "" + (int) level.getProgessPercentage(),
                    "" + (level.getLevel() + 1)
            });
        } else {
            firstLine = TranslationUtils.get("level-lookup-player", new String[]{
                    playerToLookUp.getName(),
                    "" + level.getLevel(),
                    "" + (int) level.getProgessPercentage(),
                    "" + (level.getLevel() + 1)
            });
        }

        ChatUtils.commandResponse(
                player,
                new String[]{
                        firstLine,
                        progressBar,
                        TranslationUtils.get("xp-current") + ": " + ChatColor.WHITE + NumberUtils.format(level.getCurrentXp()) + " XP",
                        TranslationUtils.get("xp-until-next") + ": " + ChatColor.WHITE + NumberUtils.format(level.getUntilXp()) + " XP",
                        TranslationUtils.get("xp-total") + ": " + ChatColor.WHITE + NumberUtils.format(level.getTotalXp()) + " XP"
                }
        );
    }

    private String makeProgressBar(Level level) {
        var barChar = "█";
        var emptyChar = "█";
        var columns = 28;

        int percentage = (int) level.getProgessPercentage();
        int barsCount = (int) (percentage / 3.5);
        int emptyCount = columns - (int) (percentage / 3.5);

        return ChatColor.WHITE +
            barChar.repeat(Math.max(0, barsCount)) +
            ChatColor.DARK_GRAY +
            emptyChar.repeat(Math.max(0, emptyCount)) +
            ChatColor.RESET;
    }
}
