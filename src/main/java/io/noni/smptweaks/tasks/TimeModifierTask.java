package io.noni.smptweaks.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeModifierTask implements Runnable {
    int dayDurationModifier;
    int nightDurationModifier;
    World world;

    public TimeModifierTask(int dayDurationModifier, int nightDurationModifier) {
        this.dayDurationModifier = dayDurationModifier;
        this.nightDurationModifier = nightDurationModifier;
        this.world = Bukkit.getWorlds().get(0);
    }

    @Override
    public void run() {
        long time = world.getTime();
        var dayStartTime = 500L;
        var dayEndTime = 11500L;
        var nightStartTime = 13050L;
        var nightEndTime = 22550L;

        if(time >= dayStartTime && time <= dayEndTime) {
            tickModifier(time, dayStartTime, dayEndTime, dayDurationModifier);
        } else if(time >= nightStartTime && time <= nightEndTime) {
            tickModifier(time, nightStartTime, nightEndTime, nightDurationModifier);
        }
    }

    private void tickModifier(long currentTime, long startTime, long endTime, long modifier) {
        long modifiableTicks = endTime - startTime;

        if(modifier > 0) {
            long maxPossibleModifier = modifiableTicks / 2;
            if(modifier > maxPossibleModifier) modifier = maxPossibleModifier;
            long ticksToJumpBack = modifier;

            // Jump back tick
            if(currentTime <= (startTime + ticksToJumpBack) * 2) world.setTime(currentTime - 1);

        } else {
            long minPossibleModifier = -modifiableTicks;
            if(modifier < minPossibleModifier) modifier = minPossibleModifier;
            long ticksToSkip = -modifier;

            // Forward ticks
            if(currentTime <= startTime + (ticksToSkip) * 2)  world.setTime(currentTime + 1);
        }
    }
}
