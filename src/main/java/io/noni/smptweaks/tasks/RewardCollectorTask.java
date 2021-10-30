package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.models.RedeemableReward;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RewardCollectorTask extends BukkitRunnable {
    private final Player player;

    public RewardCollectorTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        var lastRewardClaimedDatetime = SMPtweaks.getDB().getLastRewardClaimedDate(player);
        var cooldownBetweenRewards = SMPtweaks.getCfg().getInt("rewards.cooldown");
        int secondsSince = (int) (new Date().getTime() - lastRewardClaimedDatetime.getTime()) / 1000;
        int secondsToWait = cooldownBetweenRewards - secondsSince;

        if(secondsToWait > 0) {
            String timeToWait;
            if(secondsToWait > 3600) {
                timeToWait = "" + TimeUnit.HOURS.convert(secondsToWait, TimeUnit.SECONDS) + " " + TranslationUtils.get("hours");
            } else if(secondsToWait > 60) {
                timeToWait = "" + TimeUnit.MINUTES.convert(secondsToWait, TimeUnit.SECONDS) + " " + TranslationUtils.get("minutes");
            } else {
                timeToWait = "" + secondsToWait + " " + TranslationUtils.get("seconds");
            }

            // Tell the player how long they have to wait
            ChatUtils.commandResponse(player, TranslationUtils.get("reward-not-available-yet", timeToWait));
            return;
        }

        // Give item and xp reward to player
        var reward = new RedeemableReward(player);
        var xpToAdd = SMPtweaks.getCfg().getInt("rewards.xp");
        Bukkit.getScheduler().runTask(SMPtweaks.getPlugin(), new RewardDistrubutorTask(player, xpToAdd, reward.getItemStack()));

        // Broadcast that player has claimed their reward
        if(SMPtweaks.getCfg().getBoolean("rewards.broadcast_collection")) {
            var broadcastMessage = new TextComponent();
            var beginPart = new TextComponent(
                    TranslationUtils.get("reward-broadcast", player.getName()) + " "
            );
            var levelPart = new TextComponent(
                    ChatColor.GREEN + "[" + TranslationUtils.get("reward-broadcast-hoverable-text") + "]" + ChatColor.RESET
            );
            levelPart.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, new Text(
                        TranslationUtils.get("reward-broadcast-hover-text")
            )));
            var endPart = new TextComponent(
                    " " + TranslationUtils.get("reward-broadcast-end")
            );

            // Assemble components
            broadcastMessage.addExtra(beginPart);
            broadcastMessage.addExtra(levelPart);
            broadcastMessage.addExtra(endPart);

            // Send it!
            ChatUtils.broadcastRaw(broadcastMessage);
        }

        // Tell player what they received
        ChatUtils.notify(player, TranslationUtils.get("item-received",
                reward.getItemStack().getAmount() + "x " + reward.getReward().getDisplayName()
        ));
        SMPtweaks.getDB().updateLastRewardClaimedDate(player);
    }
}
