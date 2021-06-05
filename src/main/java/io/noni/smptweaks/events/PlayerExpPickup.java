package io.noni.smptweaks.events;

import io.noni.smptweaks.models.Level;
import io.noni.smptweaks.models.PDCKey;
import io.noni.smptweaks.utils.*;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerExpPickup implements Listener {

    /**
     * PlayerExpChangeEvent
     * @param e
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        handleExpPickup(e.getPlayer(), e.getAmount(), false);
    }

    /**
     * PlayerItemMendEvent
     * @param e
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemMend(PlayerItemMendEvent e) {
        handleExpPickup(e.getPlayer(), e.getRepairAmount(), true);
    }

    /**
     * Handle all total XP gains
     * @param player
     * @param amount
     */
    private void handleExpPickup(@NotNull Player player, int amount, boolean mendEvent) {
        // Calculate total xp
        int oldTotalXp = PDCUtils.get(player, PDCKey.TOTAL_XP);
        int newTotalXp = oldTotalXp + amount;

        // Update player PDC with new xp and level values
        Level level = new Level(newTotalXp);
        level.pushToPDC(player);

        // Broadcast levelup message
        if(level.hasLevelledUp(amount)) {
            player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.94F, 1.06F);

            // Build message components
            TextComponent congratulationMessage = new TextComponent();
            TextComponent beginPart = new TextComponent(
                    TranslationUtils.get("levelup-broadcast", new String[]{
                            player.getName()
                    }) + " "
            );
            TextComponent levelPart = new TextComponent(
                    ChatColor.GREEN + "[" +
                    TranslationUtils.get("levelup-broadcast-hoverable-text", new String[]{
                            "" + level.getLevel()
                    }) +
                    "]" + ChatColor.RESET
            );
            levelPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("" +
                    TranslationUtils.get("levelup-broadcast-hover-text", new String[]{
                            NumberUtils.format(level.getSingleXpRequiredForLevel()),
                            NumberUtils.format(level.getTotalXpRequiredForLevel())
                    })
            )));
            TextComponent endPart = new TextComponent(
                    " " + TranslationUtils.get("levelup-broadcast-end")
            );

            // Assemble components
            congratulationMessage.addExtra(beginPart);
            congratulationMessage.addExtra(levelPart);
            congratulationMessage.addExtra(endPart);

            // Send it!
            ChatUtils.broadcastRaw(congratulationMessage);
        }

        // Send progress message to player
        String notificationMessage;
        int xpDisplayMode = PDCUtils.get(player, PDCKey.XP_DISPLAY_MODE);
        int percent = (int) level.getProgessPercentage();

        // Check second digit (message content)
        switch (xpDisplayMode % 10) {
            case 1:
                notificationMessage = mendEvent ?
                        TranslationUtils.get("mending-repair", new String[]{ "" + amount }) :
                        TranslationUtils.get("xp-gained", new String[]{ "" + amount });
                break;
            case 2:
                notificationMessage = "" + percent + "%";
                break;
            case 9:
                notificationMessage = mendEvent ?
                        TranslationUtils.get("mending-repair", new String[]{ "" + amount }) + " (" + percent + "%)" :
                        TranslationUtils.get("xp-gained", new String[]{ "" + amount }) + " (" + percent + "%)";
                break;
            default:
                return;
        }

        // Check first digit (display method)
        switch (xpDisplayMode / 10) {
            case 1:
                ChatUtils.notify(player, notificationMessage);
                break;
            case 2:
                ActionBarUtils.notify(player, notificationMessage);
                break;
            default:
                return;
        }
    }
}
