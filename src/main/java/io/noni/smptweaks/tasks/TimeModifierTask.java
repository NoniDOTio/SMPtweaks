package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeModifierTask implements Runnable {
    static final long DAY_START_TIME = 500L;
    static final long NOON_TIME = 6000L;
    static final long DAY_END_TIME = 11500L;
    static final long NIGHT_START_TIME = 13050L;
    static final long MIDNIGHT_TIME = 18000L;
    static final long NIGHT_END_TIME = 22550L;

    final int dayDurationModifier;
    final int nightDurationModifier;
    final World world;

    public TimeModifierTask(int dayDurationModifier, int nightDurationModifier) {
        this.dayDurationModifier = dayDurationModifier;
        this.nightDurationModifier = nightDurationModifier;
        this.world = Bukkit.getWorlds().get(0);
    }

    @Override
    public void run() {
        long time = world.getTime();

        if(time >= DAY_START_TIME && time <= DAY_END_TIME) {
            if(dayDurationModifier > 0) {
                if(time >= NOON_TIME && time < NOON_TIME + 2) pauseDaylightCycle(dayDurationModifier);
            } else {
                tickModifier(time, DAY_START_TIME, DAY_END_TIME, dayDurationModifier);
            }
        } else if(time >= NIGHT_START_TIME && time <= NIGHT_END_TIME) {
            if(nightDurationModifier > 0) {
                if(time >= MIDNIGHT_TIME && time < MIDNIGHT_TIME + 2) pauseDaylightCycle(nightDurationModifier);
            } else {
                tickModifier(time, NIGHT_START_TIME, NIGHT_END_TIME, nightDurationModifier);
            }
        }
    }

    private void tickModifier(long currentTime, long startTime, long endTime, long modifier) {
        long modifiableTicks = endTime - startTime;

        // Skip tick
        if(modifier < 0) {
            long ticksToSkip = -Math.min(modifier, modifiableTicks);
            if(currentTime <= startTime + ticksToSkip * 2) world.setTime(currentTime + 1);
        }
    }

    private void pauseDaylightCycle(long ticksToPause) {
        Bukkit.getScheduler().runTaskLater(SMPtweaks.getPlugin(), new ToggleDaylightCycleTask(false), 2L);
        Bukkit.getScheduler().runTaskLater(SMPtweaks.getPlugin(), new ToggleDaylightCycleTask(true), 2L + ticksToPause);
    }
}
