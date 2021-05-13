package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ConfigCache {
    private List<Material> alwaysDropMaterials = new ArrayList<Material>();
    private List<Material> neverDropMaterials = new ArrayList<Material>();

    public ConfigCache() {
        List<String> alwaysDropStrings;
        alwaysDropStrings = SMPTweaks.getCfg().getStringList("remove_inventory_on_death.always_drop");

        for(String alwaysDropString : alwaysDropStrings) {
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
            Material materialToAdd = Material.getMaterial(neverDropString);
            if(materialToAdd != null) {
                neverDropMaterials.add(materialToAdd);
            } else {
                LoggingUtils.warn(neverDropString + " could not be found");
            }
        }
    }

    public List<Material> getAlwaysDropMaterials() {
        return alwaysDropMaterials;
    }

    public List<Material> getNeverDropMaterials() {
        return neverDropMaterials;
    }
}
