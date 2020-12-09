package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class NightShortenerTask implements Runnable {

    @Override
    public void run() {
        long ticksToForward = SMPTweaks.getPlugin().getConfig().getInt("shorten_nights_by");
        if(ticksToForward < 100) {
            return;
        }

        World world = Bukkit.getWorlds().get(0);
        if(world.getTime() > 16000L && world.getTime() < 16100L) {
            world.setTime(16000L + ticksToForward);
            LoggingUtils.info("Skipped " + ticksToForward + " ticks!");
        }
    }
}
