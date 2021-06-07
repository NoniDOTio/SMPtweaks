package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class RewardReminderTask extends BukkitRunnable {
    private Player player;

    public RewardReminderTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        Date lastRewardClaimedDatetime = SMPtweaks.getDB().getLastRewardClaimedDate(player);
        int cooldownBetweenRewards = SMPtweaks.getCfg().getInt("rewards.cooldown");
        int secondsSince = (int) (new Date().getTime() - lastRewardClaimedDatetime.getTime()) / 1000;
        int secondsToWait = cooldownBetweenRewards - secondsSince;

        if(secondsToWait < 0) {
            ChatUtils.commandResponse(player, TranslationUtils.get("reward-available"));
        }
    }
}
