package io.noni.smptweaks.tasks;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ActionBarUtils;
import io.noni.smptweaks.utils.NumberUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TrackerUpdateTask implements Runnable {

    @Override
    public void run() {
        SMPtweaks.getPlayerTrackers().forEach((playerUuid, trackedPlayerUuid) -> {
            Player player = Bukkit.getPlayer(playerUuid);

            if (player == null) {
                return;
            }

            Player trackedPlayer = Bukkit.getPlayer(trackedPlayerUuid);

            if (trackedPlayer == null) {
                ActionBarUtils.negativeNotify(player, TranslationUtils.get("tracker-offline"));
                return;
            }

            World.Environment playerDimension = player.getWorld().getEnvironment();
            World.Environment trackedPlayerDimension = trackedPlayer.getWorld().getEnvironment();

            if (playerDimension != trackedPlayerDimension) {
                String world = "";
                if (trackedPlayerDimension.equals(World.Environment.NORMAL)) {
                    world = TranslationUtils.get("whereis-in-overworld");
                } else if (trackedPlayerDimension.equals(World.Environment.NETHER)) {
                    world = TranslationUtils.get("whereis-in-nether");
                } else if (trackedPlayerDimension.equals(World.Environment.THE_END)) {
                    world = TranslationUtils.get("whereis-in-end");
                }
                ActionBarUtils.notify(player, TranslationUtils.get("tracker-player-in-different-dimension", new String[]{
                        trackedPlayer.getName(),
                        world
                }));
                return;
            }

            Location playerLocation = player.getLocation();
            Location trackedPlayerLocation = trackedPlayer.getLocation();

            Vector direction = playerLocation.getDirection();
            Vector trackedPlayerToPlayer = playerLocation.toVector().subtract(trackedPlayerLocation.toVector());

            double distanceInBlocks = playerLocation.distance(trackedPlayerLocation);
            double cross = direction.clone().crossProduct(trackedPlayerToPlayer).getY() / distanceInBlocks;
            double dot = direction.dot(trackedPlayerToPlayer) / distanceInBlocks;

            String relativePositionIndicator = getRelativePositionIndicator(distanceInBlocks, playerLocation, trackedPlayerLocation);
            String arrow = getArrowSymbol(cross, dot);
            ActionBarUtils.notify(player, TranslationUtils.get("tracker-tracking", new String[]{
                    trackedPlayer.getName(),
                    NumberUtils.format((int) distanceInBlocks)
            }) + " " +  arrow + " " +  relativePositionIndicator);
        });
    }


    @NotNull
    private static String getRelativePositionIndicator(double distanceInBlocks, Location playerLocation, Location trackedPlayerLocation) {
        if (distanceInBlocks > 320) {
            return "";
        }

        int playerY = playerLocation.getBlockY();
        int trackedPlayerY = trackedPlayerLocation.getBlockY();
        int distanceY = Math.abs(playerY - trackedPlayerY);

        if(distanceInBlocks > distanceY * 4) {
            return "";
        }
        if (playerY > trackedPlayerY + 3) {
            return  "(" + TranslationUtils.get("tracker-below") + ")";
        } else if (playerY < trackedPlayerY - 3) {
            return  "(" + TranslationUtils.get("tracker-above") + ")";
        }
        return "";
    }


    @NotNull
    private static String getArrowSymbol(double cross, double dot) {
        if (cross > -0.5 && cross < 0.5 && dot < 0) {
            return "↑";
        } else if (cross > 0.5 && dot < -0.5) {
            return "↗";
        } else if (cross > 0 && dot > -0.5 && dot < 0.5) {
            return "→";
        } else if (cross > 0.5 && dot > 0.5) {
            return "↘";
        } else if (cross < 0.5 && cross > -0.5 && dot > 0) {
            return "↓";
        } else if (cross < -0.5 && dot > 0.5) {
            return "↙";
        } else if (cross < 0 && dot > -0.5 && dot < 0.5) {
            return "←";
        } else if (cross < -0.5 && dot < 0.5) {
            return "↖";
        } else {
            return " ";
        }
    }
}
