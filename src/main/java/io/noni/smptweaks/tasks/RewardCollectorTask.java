package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.models.RedeemableReward;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RewardCollectorTask extends BukkitRunnable {
    private Player player;

    public RewardCollectorTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        Date lastRewardClaimedDatetime = SMPTweaks.getDB().getLastRewardClaimedDate(player);
        int cooldownBetweenRewards = SMPTweaks.getCfg().getInt("rewards.cooldown");
        int secondsSince = (int) (new Date().getTime() - lastRewardClaimedDatetime.getTime()) / 1000;
        int secondsToWait = cooldownBetweenRewards - secondsSince;

        if(secondsToWait > 0) {
            String timeToWait;
            if(secondsToWait > 3600) {
                timeToWait = "" + TimeUnit.HOURS.convert(secondsToWait, TimeUnit.SECONDS) + " " + TranslationUtils.get("generic-hours");
            } else if(secondsToWait > 60) {
                timeToWait = "" + TimeUnit.MINUTES.convert(secondsToWait, TimeUnit.SECONDS) + " " + TranslationUtils.get("generic-minutes");
            } else {
                timeToWait = "" + secondsToWait + " " + TranslationUtils.get("generic-seconds");
            }

            // Tell the player how long they have to wait
            ChatUtils.commandResponse(player, TranslationUtils.get("reward-not-available-yet", new String[]{timeToWait}));
            return;
        }

        // Give item reward
        RedeemableReward reward = new RedeemableReward(player);
        player.getInventory().addItem(reward.getItem());

        // Give xp reward
        int xpToAdd = SMPTweaks.getCfg().getInt("rewards.xp");
        if(xpToAdd > 0) {
            player.setTotalExperience(player.getTotalExperience() + xpToAdd);
        }

        // Broadcast that player has claimed their reward
        TextComponent broadcastMessage = new TextComponent();
        TextComponent beginPart = new TextComponent(
                TranslationUtils.get("reward-broadcast", new String[]{
                        player.getName()
                }) + " "
        );
        TextComponent levelPart = new TextComponent(
                ChatColor.GREEN + "[" + TranslationUtils.get("reward-broadcast-hoverable-text") + "]" + ChatColor.RESET
        );
        levelPart.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new Text(
                    TranslationUtils.get("reward-broadcast-hover-text")
        )));
        TextComponent endPart = new TextComponent(
                " " + TranslationUtils.get("reward-broadcast-end")
        );

        // Assemble components
        broadcastMessage.addExtra(beginPart);
        broadcastMessage.addExtra(levelPart);
        broadcastMessage.addExtra(endPart);

        // Send it!
        ChatUtils.broadcastRaw(broadcastMessage);

        // Tell player what they received
        ChatUtils.notify(player, TranslationUtils.get("item-received", new String[] {
                reward.getItem().getAmount() + "x " + reward.getReward().getDisplayName()
        }));
        SMPTweaks.getDB().updateLastRewardClaimedDate(player);
    }
}
