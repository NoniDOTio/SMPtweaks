package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCache {
    private List<Material> alwaysDropMaterials = new ArrayList<>();
    private List<Material> neverDropMaterials = new ArrayList<>();
    private List<Reward> rewardsList = new ArrayList<>();

    public ConfigCache() {
        List<String> alwaysDropStrings;
        alwaysDropStrings = SMPTweaks.getCfg().getStringList("remove_inventory_on_death.always_drop");

        for(String alwaysDropString : alwaysDropStrings) {
            alwaysDropString = alwaysDropString.toUpperCase();
            Material materialToAdd = Material.getMaterial(alwaysDropString);
            if(materialToAdd != null) {
                alwaysDropMaterials.add(materialToAdd);
            } else {
                LoggingUtils.warn(alwaysDropString + " could not be found");
            }
        }

        List<String> neverDropStrings;
        neverDropStrings = SMPTweaks.getCfg().getStringList("remove_inventory_on_death.never_drop");

        for(String neverDropString : neverDropStrings) {
            neverDropString = neverDropString.toUpperCase();
            Material materialToAdd = Material.getMaterial(neverDropString);
            if(materialToAdd != null) {
                neverDropMaterials.add(materialToAdd);
            } else {
                LoggingUtils.warn(neverDropString + " could not be found");
            }
        }

        //
        // Rewards
        //
        List rewardList = SMPTweaks.getPlugin().getConfig().getList("rewards.contents");

        for (Object rewardSingle : rewardList) {
            Map reward = (Map) rewardSingle;
            int level = reward.get("level") == null ? 0 : Integer.parseInt(reward.get("level").toString());
            int minLevel = reward.get("min_level") == null ? level : Integer.parseInt(reward.get("min_level").toString());
            int maxLevel = reward.get("max_level") == null ? Integer.MAX_VALUE : Integer.parseInt(reward.get("max_level").toString());
            int amount = reward.get("amount") == null ? 1 : Integer.parseInt(reward.get("amount").toString());
            int minAmount = reward.get("min_amount") == null ? amount : Integer.parseInt(reward.get("min_amount").toString());
            int maxAmount = reward.get("max_amount") == null ? amount : Integer.parseInt(reward.get("max_amount").toString());
            int weight = reward.get("weight") == null ? 1 : Integer.parseInt(reward.get("weight").toString());

            Material material = Material.valueOf(reward.get("material").toString().toUpperCase());
            rewardsList.add(new Reward(material,minLevel, maxLevel, minAmount, maxAmount, weight));
        }
    }

    public List<Material> getAlwaysDropMaterials() {
        return alwaysDropMaterials;
    }

    public List<Material> getNeverDropMaterials() {
        return neverDropMaterials;
    }

    public List<Reward> getRewardsList() {
        return rewardsList;
    }
}
