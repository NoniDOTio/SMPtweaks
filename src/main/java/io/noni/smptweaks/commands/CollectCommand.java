package io.noni.smptweaks.commands;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.tasks.RewardCollectorTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CollectCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!command.getName().equalsIgnoreCase("collect") || !(sender instanceof Player player)) {
            return false;
        }

        new RewardCollectorTask(player).runTaskAsynchronously(SMPtweaks.getPlugin());
        return true;
    }
}
