package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeModifierTask implements Runnable {

    @Override
    public void run() {
        long ticksToForward = SMPTweaks.getPlugin().getConfig().getInt("shorten_nights_by");
        if(ticksToForward < 100) {
            return;
        }

        World world = Bukkit.getWorlds().get(0);
        long time = world.getTime();
        if(time > 14000L && time < 14000L + ticksToForward * 2) {
            world.setTime(time + 2L);
        }
    }
}
