package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WeatherClearerTask implements Runnable {

    @Override
    public void run() {
        if(SMPTweaks.getPlugin().getConfig().getBoolean("clear_weather_at_dawn") == false) {
            return;
        }

        World world = Bukkit.getWorlds().get(0);
        if(world.getTime() > 23000L && world.getTime() < 23100L) {
            world.setStorm(false);
            LoggingUtils.info("Cleared the weather!");
        }
    }
}
