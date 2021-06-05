package io.noni.smptweaks.commands;

import io.noni.smptweaks.models.Level;
import io.noni.smptweaks.models.PlayerMeta;
import io.noni.smptweaks.utils.ChatUtils;
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

        Player player = (Player) sender;

        if (args.length == 0) {
            printProgress(player, player);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("lookup")) {
            ChatUtils.commandResponse(player, TranslationUtils.get("level-lookup-no-player-specified"));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("lookup")) {
            Player playerToLookUp = Bukkit.getPlayer(args[1]);
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
            PlayerMeta playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(0);
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        } else if (args.length == 2 && args[1].equalsIgnoreCase("show")) {
            PlayerMeta playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(19);
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        } else if (args.length == 3 && args[1].equalsIgnoreCase("show")) {
            ChatUtils.commandResponse(player, TranslationUtils.get("level-progress-no-display-method-specified"));

        } else if (args.length == 4 && args[1].equalsIgnoreCase("show")) {
            String xpDisplayMode = "";
            switch (args[3]) {
                case "chat":
                    xpDisplayMode += 1;
                    break;
                case "actionbar":
                    xpDisplayMode += 2;
                    break;
                default:
                    xpDisplayMode += 0;
                    break;
            }

            switch (args[2]) {
                case "xp":
                    xpDisplayMode += 1;
                    break;
                case "percentages":
                    xpDisplayMode += 2;
                    break;
                case "all":
                    xpDisplayMode += 9;
                    break;
                default:
                    xpDisplayMode += 0;
                    break;
            }
            PlayerMeta playerMeta = new PlayerMeta(player);
            playerMeta.setXpDisplayMode(Integer.parseInt(xpDisplayMode));
            playerMeta.pushToPDC();
            ChatUtils.commandResponse(player, TranslationUtils.get("preferences-saved"));

        }

        return true;
    }

    private void printProgress(Player player, Player playerToLookUp) {
        int totalXp = new PlayerMeta(playerToLookUp).getTotalXp();
        Level level = new Level(totalXp);
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
                        progressBar,
                        firstLine,
                        TranslationUtils.get("xp-current") + ": " + ChatColor.WHITE + level.getCurrentXp() + " XP",
                        TranslationUtils.get("xp-until-next") + ": " + ChatColor.WHITE + level.getUntilXp() + " XP",
                        TranslationUtils.get("xp-total") + ": " + ChatColor.WHITE + level.getTotalXp() + " XP"
                }
        );
    }

    private String makeProgressBar(Level level) {
        String barChar = "█";
        String emptyChar = "  ";
        int columns = 28;

        int percentage = (int) level.getProgessPercentage();
        int barsCount = (int) (percentage / 3.5);
        int emptyCount = columns - (int) (percentage / 3.5);
        String bar = "❯";

        for(int i = 0; i < barsCount; i++) {
            bar += barChar;
        }
        for(int i = 0; i < emptyCount; i++) {
            bar += (i % 7 == 0) ? emptyChar + emptyChar : emptyChar;
        }
        return bar + "❮";
    }
}
