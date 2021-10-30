package io.noni.smptweaks.tasks;

import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;

public class ToggleDaylightCycleTask implements Runnable {
    boolean newValue;

    public ToggleDaylightCycleTask(boolean newValue) {
        this.newValue = newValue;
    }

    @Override
    public void run() {
        Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, newValue);
        LoggingUtils.debug("Set doDaylightCycle to " + newValue);
    }
}
