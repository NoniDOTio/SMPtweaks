package io.noni.smptweaks.models;

import org.bukkit.inventory.ItemStack;

public record Reward(
        ItemStack itemStack,
        int minLevel,
        int maxLevel,
        int minAmount,
        int maxAmount,
        int weight
) {

    public String getDisplayName() {
        var itemMeta = itemStack.getItemMeta();

        if (itemMeta != null && itemMeta.hasLocalizedName()) {
            return itemMeta.getLocalizedName();
        } else if (itemMeta != null && itemMeta.hasDisplayName()) {
            return itemMeta.getDisplayName();
        } else {
            return itemStack.getType().name().replace("_", " ").toLowerCase();
        }
    }
}
