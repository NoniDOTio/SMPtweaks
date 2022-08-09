package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigCache {
    private final List<Material> alwaysDropMaterials = new ArrayList<>();
    private final List<Material> neverDropMaterials = new ArrayList<>();
    private final List<Reward> rewardsList = new ArrayList<>();
    private final List<ShapedRecipe> shapedRecipes = new ArrayList<>();
    private final List<ShapelessRecipe> shapelessRecipes = new ArrayList<>();
    private final EnumMap<EntityType, Float> entitySpawnRates = new EnumMap<>(EntityType.class);
    private final EnumMap<EntityType, CustomDropSet> entityCustomDrops = new EnumMap<>(EntityType.class);

    public ConfigCache() {
        //
        // Item Dropping
        //
        List<String> alwaysDropStrings;
        alwaysDropStrings = SMPtweaks.getCfg().getStringList("remove_inventory_on_death.always_drop");

        for(String alwaysDropString : alwaysDropStrings) {
            var materialToAdd = Material.getMaterial(alwaysDropString.toUpperCase());
            if(materialToAdd != null) {
                alwaysDropMaterials.add(materialToAdd);
            } else {
                LoggingUtils.warn("Invalid material '" + alwaysDropString + "' in always drop list");
            }
        }

        List<String> neverDropStrings;
        neverDropStrings = SMPtweaks.getCfg().getStringList("remove_inventory_on_death.never_drop");

        for(String neverDropString : neverDropStrings) {
            var materialToAdd = Material.getMaterial(neverDropString.toUpperCase());
            if(materialToAdd != null) {
                neverDropMaterials.add(materialToAdd);
            } else {
                LoggingUtils.warn("Invalid material '" + neverDropString + "' in never drop list");
            }
        }

        //
        // Rewards
        //
        List<?> configRewards = SMPtweaks.getPlugin().getConfig().getList("rewards.contents");

        if(configRewards != null) {
            for (Object configReward : configRewards) {
                Map<?,?> reward = (Map<?,?>) configReward;
                int level = reward.get("level") == null ? 0 : Integer.parseInt(reward.get("level").toString());
                int minLevel = reward.get("min_level") == null ? level : Integer.parseInt(reward.get("min_level").toString());
                int maxLevel = reward.get("max_level") == null ? Integer.MAX_VALUE : Integer.parseInt(reward.get("max_level").toString());
                int amount = reward.get("amount") == null ? 1 : Integer.parseInt(reward.get("amount").toString());
                int minAmount = reward.get("min_amount") == null ? amount : Integer.parseInt(reward.get("min_amount").toString());
                int maxAmount = reward.get("max_amount") == null ? amount : Integer.parseInt(reward.get("max_amount").toString());
                int weight = reward.get("weight") == null ? 1 : Integer.parseInt(reward.get("weight").toString());

                ItemStack itemStack = makeRewardItem(reward);
                if (itemStack == null) {
                    continue;
                }
                rewardsList.add(new Reward(itemStack, minLevel, maxLevel, minAmount, maxAmount, weight));
            }
        }

        //
        // Custom Recipes
        //
        List<?> customRecipesList = SMPtweaks.getCfg().getList("custom_recipes.recipes");
        var i = 1;
        for (Object customRecipeSingle : customRecipesList) {
            var customRecipe = (Map<?,?>) customRecipeSingle;
            boolean shapeless = customRecipe.get("shape") == null;
            var recipeKey = new NamespacedKey(SMPtweaks.getPlugin(), "custom_recipe_" + i );

            // Material
            var material = Material.getMaterial(customRecipe.get("material").toString().toUpperCase());
            if(material == null) {
                LoggingUtils.warn("Invalid recipe result '" + customRecipe.get("material").toString() + "'");
                continue;
            }
            var itemStack = new ItemStack(material);

            // Display Name
            Object configDisplayName = customRecipe.get("display_name");
            if(configDisplayName != null) {
                applyDisplayName(configDisplayName, itemStack, "for " + material);
            }

            // Item Lore
            List<String> configLore = customRecipe.get("lore") instanceof String ? List.of(customRecipe.get("lore").toString()) : (List<String>) customRecipe.get("lore");
            if(configLore != null) {
                applyLore(configLore, itemStack, "in recipe for " + material);
            }

            // Amount
            String amountString = customRecipe.get("amount") == null ? "1" : customRecipe.get("amount").toString();
            var amount = Integer.parseInt(amountString);
            itemStack.setAmount(Math.min(64, amount));

            // Enchantments
            var enchantmentsList = (List<?>) customRecipe.get("enchantments");
            if(enchantmentsList != null) {
                applyEnchantments(enchantmentsList, itemStack, "in recipe for " + material);
            }

            // Potion Effect
            var potionEffect = customRecipe.get("potion_type");
            if(potionEffect != null && (
                    material == Material.POTION ||
                    material == Material.SPLASH_POTION ||
                    material == Material.LINGERING_POTION
            )) {
                applyPotionEffect(potionEffect, itemStack, "in recipe for " + material);
            }

            // Recipe
            if(shapeless) {
                var shapelessRecipe = new ShapelessRecipe(recipeKey, itemStack);
                var ingredients = (List<?>) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    var ingredient = (Map<?,?>) ingredientSingle;
                    var materialIngredient = Material.getMaterial(ingredient.get("material").toString());
                    var ingredientCountString = ingredient.get("amount") == null ? "1" : ingredient.get("amount").toString();
                    var ingredientAmount = Integer.parseInt(ingredientCountString);

                    if(materialIngredient == null) {
                        LoggingUtils.warn("Invalid ingredient '" + ingredient.get("material").toString() + "' in recipe for " + material);
                        continue;
                    }

                    shapelessRecipe.addIngredient(Math.min(9, ingredientAmount), materialIngredient);
                }
                shapelessRecipes.add(shapelessRecipe);
            } else {
                var shapedRecipe = new ShapedRecipe(recipeKey, itemStack);
                var shape = (ArrayList<?>) customRecipe.get("shape");
                var firstLine = shape.get(0).toString();
                var secondLine = shape.get(1).toString();
                var thirdLine = shape.get(2).toString();

                shapedRecipe.shape(
                        firstLine,
                        secondLine,
                        thirdLine
                );

                var ingredients = (List<?>) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    var ingredient = (Map<?,?>) ingredientSingle;
                    var materialIngredient = Material.getMaterial(ingredient.get("material").toString());

                    if(materialIngredient == null) {
                        LoggingUtils.warn("Invalid ingredient '" + ingredient.get("material").toString() + "' in recipe for " + material);
                        continue;
                    }

                    shapedRecipe.setIngredient(ingredient.get("key").toString().trim().charAt(0), materialIngredient);
                }
                shapedRecipes.add(shapedRecipe);
            }
            i++;
        }

        //
        // Spawn Rates
        //
        List<?> spawnRatesList = SMPtweaks.getCfg().getList("spawn_rates.mobs");
        for (Object spawnRateSingle : spawnRatesList) {
            var spawnRate = (Map<?,?>) spawnRateSingle;
            var typeObject = spawnRate.get("type");
            if(typeObject == null) {
                continue;
            }
            var typeString = typeObject.toString().toUpperCase();
            EntityType type;
            try {
                type = EntityType.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                LoggingUtils.warn("Invalid entity type '" + typeString + "' in mob spawn rates");
                continue;
            }

            var multiplierString = spawnRate.get("multiplier").toString();
            float multiplier;
            try {
                multiplier = Float.parseFloat(multiplierString);
            } catch (NullPointerException e) {
                LoggingUtils.warn("Missing spawn rate multiplier for '" + typeString + "'");
                continue;
            } catch (NumberFormatException e) {
                LoggingUtils.warn("Invalid spawn rate multiplier for '" + typeString + "'");
                continue;
            }

            if(multiplier > 1) {
                multiplier = 1F;
                LoggingUtils.warn("Ignoring spawn rate multiplier '" + multiplierString + "' for '" + typeString + "' because it is higher than allowed (1.0)");
            }

            if(multiplier < 0) {
                multiplier = 0F;
                LoggingUtils.warn("Changing spawn rate multiplier '" + multiplierString + "' for '" + typeString + "' to 0 because it is lower than allowed");
            }
            entitySpawnRates.put(type, multiplier);
        }

        //
        // Custom Drops
        //
        List<?> configDropsMobs = SMPtweaks.getCfg().getList("custom_drops.mobs");
        for (Object configDropsMob : configDropsMobs){
            var dropsMob = (Map<?,?>) configDropsMob;
            var dropsEntityTypeString = dropsMob.get("type").toString();
            if(dropsEntityTypeString == null) continue;

            // Check if entity type is valid
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(dropsEntityTypeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                LoggingUtils.warn("Invalid entity type '" + dropsEntityTypeString + "' in custom drops");
                continue;
            }

            // XP
            Integer xpDrop = null;
            Object configXpDrop = dropsMob.get("xp");
            if(configXpDrop != null) {
                try {
                    xpDrop = Integer.parseInt(configXpDrop.toString());
                } catch (NumberFormatException e) {
                    LoggingUtils.warn("Invalid XP drop amount for entity " + dropsEntityTypeString + " in custom drops");
                }
            }

            // Whether vanilla drops should be discarded
            boolean discardVanillaDrops = false;
            Object configDiscardVanillaDrops = dropsMob.get("discard_vanilla_drops");
            if(configDiscardVanillaDrops != null) {
                discardVanillaDrops = Boolean.parseBoolean(configDiscardVanillaDrops.toString());
            }

            // Commands to run when the mob dies
            List<String> configCommands;
            try {
                configCommands = dropsMob.get("commands") instanceof String ? List.of(dropsMob.get("commands").toString()) : (List<String>) dropsMob.get("commands");
            } catch (ClassCastException e) {
                LoggingUtils.warn("Incorrectly formatted commands for entity " + dropsEntityTypeString + "");
                configCommands = null;
            }

            // Loop through custom drops for this mob
            List<?> configDrops = (List<?>) dropsMob.get("drops");
            var drops = new HashMap<CustomDrop, Float>();
            if(configDrops != null) {
                for (Object configDrop : configDrops) {
                    var drop = (Map<?,?>) configDrop;

                    // Item
                    ItemStack itemStack;
                    String materialString;
                    try {
                        materialString = drop.get("material").toString();
                        itemStack = makeCustomDropItem(drop, entityType);
                    } catch (NullPointerException e) {
                        materialString = null;
                        itemStack = null;
                    }
                    String dropDescription = (materialString == null) ? "command": "item " + materialString;

                    // Commands
                    List<String> configDropCommands;
                    try {
                        configDropCommands = drop.get("commands") instanceof String ? List.of(drop.get("commands").toString()) : (List<String>) drop.get("commands");
                    } catch (ClassCastException e) {
                        LoggingUtils.warn("Incorrectly formatted commands in drops for entity " + dropsEntityTypeString + "");
                        configDropCommands = null;
                    }

                    // Drop Chance
                    var configChance = drop.get("chance").toString();
                    float chance;
                    try {
                        chance = Float.parseFloat(configChance);
                    } catch (NullPointerException | NumberFormatException e) {
                        LoggingUtils.warn("Invalid drop chance for " + dropDescription + " in " + entityType + " custom drops");
                        continue;
                    }
                    if (chance > 1) {
                        chance = 1F;
                        LoggingUtils.warn("Changing drop chance '" + configChance + "' for " + dropDescription + " in " + entityType + " custom drops to 1.0 because it is higher than allowed");
                    }
                    if (chance < 0) {
                        chance = 0F;
                        LoggingUtils.warn("Changing drop chance '" + configChance + "' for '" + dropDescription + " in " + entityType + " custom drops to 0 because it is lower than allowed");
                    }

                    drops.put(new CustomDrop(itemStack, configDropCommands), chance);
                }
            }
            entityCustomDrops.put(entityType, new CustomDropSet(xpDrop, drops, configCommands, discardVanillaDrops));
        }
    }

    /**
     * Parse the reward item described in the config
     * @param reward Raw description of the item from the config
     * @return The parsed item or null if parsing failed
     */
    @Nullable
    private ItemStack makeRewardItem(Map<?,?> reward) {
        var material = Material.getMaterial(reward.get("material").toString().toUpperCase());
        if(material == null) {
            LoggingUtils.warn("Invalid reward '" + reward.get("material").toString() + "'");
            return null;
        }
        var itemStack = new ItemStack(material);

        // Display Name
        Object configDisplayName = reward.get("display_name");
        if(configDisplayName != null) {
            applyDisplayName(configDisplayName, itemStack, "for " + material);
        }

        // Item Lore
        List<String> configLore = reward.get("lore") instanceof String ? List.of(reward.get("lore").toString()) : (List<String>) reward.get("lore");
        if(configLore != null) {
            applyLore(configLore, itemStack, "for reward '" + material + "'");
        }

        // Enchantments
        var configEnchantments = (List<?>) reward.get("enchantments");
        if(configEnchantments != null) {
            applyEnchantments(configEnchantments, itemStack, "for reward '" + material + "'");
        }

        // Potion Effect
        var configPotionEffect = reward.get("potion_type");
        if(configPotionEffect != null && (
                material == Material.POTION ||
                material == Material.SPLASH_POTION ||
                material == Material.LINGERING_POTION
        )) {
            applyPotionEffect(configPotionEffect, itemStack, "for reward '" + material + "'");
        }
        return itemStack;
    }

    /**
     * Parse the custom drop item described in the config
     * @param configDrop Raw description of the item from the config
     * @param entityType EntityType that is supposed to drop the custom drop, used to throw more accurate erros and warnings
     * @return The resulting item or null if parsing failed
     */
    @Nullable
    private ItemStack makeCustomDropItem(Map<?,?> configDrop, EntityType entityType) {
        var materialString = configDrop.get("material").toString();

        // Material
        var material = Material.getMaterial(materialString.toUpperCase());
        if (material == null) {
            LoggingUtils.warn("Invalid material '" + materialString + "' in " + entityType + " custom drops");
            return null;
        }
        var itemStack = new ItemStack(material);
        String context = "for item " + materialString + " in " + entityType + " custom drops";

        // Display Name
        Object configDisplayName = configDrop.get("display_name");
        if (configDisplayName != null) {
            applyDisplayName(configDisplayName, itemStack, context);
        }

        // Item Lore
        List<String> loreList = configDrop.get("lore") instanceof String ? List.of(configDrop.get("lore").toString()) : (List<String>) configDrop.get("lore");
        if (loreList != null) {
            applyLore(loreList, itemStack, context);
        }

        // Amount
        String amountString = configDrop.get("amount") == null ? "1" : configDrop.get("amount").toString();
        try {
            var amount = Integer.parseInt(amountString);
            itemStack.setAmount(Math.min(64, amount));
        } catch (NumberFormatException e) {
            LoggingUtils.warn("Invalid amount '" + amountString + "' for item " + materialString + " in " + entityType + " custom drops. Defaulting to 1");
            itemStack.setAmount(1);
        }

        // Enchantments
        var enchantmentsList = (List<?>) configDrop.get("enchantments");
        if (enchantmentsList != null) {
            applyEnchantments(enchantmentsList, itemStack, context);
        }

        // Potion Effect
        Object potionEffect = configDrop.get("potion_type");
        if (potionEffect != null && (
                material == Material.POTION ||
                material == Material.SPLASH_POTION ||
                material == Material.LINGERING_POTION
        )) {
            applyPotionEffect(potionEffect, itemStack, context);
        }
        return itemStack;
    }

    /**
     * Set lore of an ItemStack
     * @param configLore A list of strings where each entry represents a line
     * @param itemStack The ItemStack to add the lore to
     * @param context Additional information to for more descriptive errors and warnings
     */
    private void applyLore(List<?> configLore, ItemStack itemStack, String context) {
        List<String> lore = new ArrayList<>();
        for(Object configLine : configLore) {
            String line = configLine == null ? "" : configLine.toString();
            lore.add(line);
        }
        var itemMeta = itemStack.getItemMeta();
        if(itemMeta != null && !lore.isEmpty()) {
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        } else {
            LoggingUtils.warn("Invalid lore " + context);
        }
    }

    /**
     * Set display name of an ItemStack
     * @param configDisplayName The display name
     * @param itemStack The ItemStack to name
     * @param context Additional information to for more descriptive errors and warnings
     */
    private void applyDisplayName(Object configDisplayName, ItemStack itemStack, String context) {
        var displayName = configDisplayName.toString();
        var itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || displayName == null || displayName.equals("")) {
            LoggingUtils.warn("Invalid display name " + context);
            return;
        }
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Apply potion effect to ItemStack
     * @param configPotionEffect Raw string of potion effect to apply
     * @param itemStack The ItemStack to apply the potion effect to
     * @param context Additional information to for more descriptive errors and warnings
     */
    private void applyPotionEffect(Object configPotionEffect, ItemStack itemStack, String context) {
        var potionMeta = (PotionMeta) itemStack.getItemMeta();
        PotionType potionType;
        try {
            potionType = PotionType.valueOf(configPotionEffect.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            LoggingUtils.warn("Invalid potion type '" + configPotionEffect + "' " + context);
            return;
        }

        try {
            potionMeta.setBasePotionData(new PotionData(potionType));
            itemStack.setItemMeta(potionMeta);
        } catch (NullPointerException e) {
            LoggingUtils.warn("Unable to apply potion data " + context);
        }
    }

    /**
     * Add enchantments to ItemStack
     * @param enchantmentsList List of Maps that contain enchantment name and
     * @param itemStack The ItemStack to add the enchantment to
     * @param context Additional information to for more descriptive errors and warnings
     */
    private void applyEnchantments(List<?> enchantmentsList, ItemStack itemStack, String context) {
        for (Object enchantmentSingle : enchantmentsList) {
            var enchantmentMap = (Map<?,?>) enchantmentSingle;

            var enchantmentName = enchantmentMap.get("enchantment").toString();
            if(enchantmentName == null) {
                LoggingUtils.warn("Invalid enchantment name " + context);
                return;
            }

            NamespacedKey enchantmentKey;
            try {
                enchantmentKey = NamespacedKey.minecraft(enchantmentName.toLowerCase());
            } catch (IllegalArgumentException e) {
                LoggingUtils.warn("Invalid enchantment '" + enchantmentName + "' " + context);
                return;
            }

            var enchantment = Enchantment.getByKey(enchantmentKey);
            if(enchantment == null) {
                LoggingUtils.warn("Invalid enchantment '" + enchantmentName + "' " + context);
                return;
            }

            int enchantmentLevel;
            try {
                enchantmentLevel = Integer.parseInt(enchantmentMap.get("level").toString());
            } catch (NumberFormatException e) {
                LoggingUtils.warn("Invalid enchantment level " + context);
                return;
            }
            itemStack.addUnsafeEnchantment(enchantment, enchantmentLevel);
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

    public List<ShapedRecipe> getShapedRecipes() {
        return shapedRecipes;
    }

    public List<ShapelessRecipe> getShapelessRecipes() {
        return shapelessRecipes;
    }

    public Map<EntityType, Float> getEntitySpawnRates() {
        return entitySpawnRates;
    }

    public Map<EntityType, CustomDropSet> getEntityCustomDrops() {
        return entityCustomDrops;
    }
}
