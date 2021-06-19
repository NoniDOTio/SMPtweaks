package io.noni.smptweaks.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Reward {
    String displayName;
    Material material;
    int weight;

    int minLevel;
    int maxLevel;
    int minAmount;
    int maxAmount;

    public Reward(Material material, int minLevel, int maxLevel, int minAmount, int maxAmount, int weight) {
        this.material = material;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
        this.displayName = makeDisplayName(material);
    }

    private String makeDisplayName(Material material) {
        var itemStack = new ItemStack(material);
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

    public Material getMaterial() {
        return material;
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
