package io.noni.smptweaks.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if(meta.hasLocalizedName()) {
            return meta.getLocalizedName();
        } else if(meta.hasDisplayName()) {
            return meta.getDisplayName();
        } else {
            return item.getType().name().replace("_", " ").toLowerCase();
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
