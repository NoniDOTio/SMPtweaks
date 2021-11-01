package io.noni.smptweaks.models;

import org.bukkit.inventory.ItemStack;

public class Reward {
    private final String displayName;
    private final ItemStack itemStack;
    private final int weight;
    private final int minLevel;
    private final int maxLevel;
    private final int minAmount;
    private final int maxAmount;

    public Reward(ItemStack itemStack, int minLevel, int maxLevel, int minAmount, int maxAmount, int weight) {
        this.itemStack = itemStack;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
        this.displayName = makeDisplayName();
    }

    private String makeDisplayName() {
        var itemMeta = itemStack.getItemMeta();

        if(itemMeta !=null && itemMeta.hasLocalizedName()) {
            return itemMeta.getLocalizedName();
        } else if(itemMeta !=null && itemMeta.hasDisplayName()) {
            return itemMeta.getDisplayName();
        } else {
            return itemStack.getType().name().replace("_", " ").toLowerCase();
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getWeight() {
        return weight;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}
