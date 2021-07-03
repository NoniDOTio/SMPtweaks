package io.noni.smptweaks.models;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record CustomDrop(Integer xp, Map<ItemStack, Float> possibleItemDrops, boolean discardVanillaDrops, List<String> commands) {

    public CustomDrop(@Nullable Integer xp, @NotNull Map<ItemStack, Float> possibleItemDrops, boolean discardVanillaDrops, List<String> commands) {
        this.xp = xp;
        this.possibleItemDrops = possibleItemDrops;
        this.discardVanillaDrops = discardVanillaDrops;
        this.commands = commands;
    }

    public Integer getXp() {
        return xp;
    }

    public Map<ItemStack, Float> getPossibleItemDrops() {
        return possibleItemDrops;
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean getDiscardVanillaDrops() {
        return discardVanillaDrops;
    }
}
