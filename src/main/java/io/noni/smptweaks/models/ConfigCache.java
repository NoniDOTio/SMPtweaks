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

import java.util.*;

public class ConfigCache {
    private List<Material> alwaysDropMaterials = new ArrayList<>();
    private List<Material> neverDropMaterials = new ArrayList<>();
    private List<Reward> rewardsList = new ArrayList<>();
    private List<ShapedRecipe> shapedRecipes = new ArrayList<>();
    private List<ShapelessRecipe> shapelessRecipes = new ArrayList<>();
    private EnumMap<EntityType, Float> entitySpawnRates = new EnumMap<>(EntityType.class);
    private EnumMap<EntityType, CustomDrop> entityCustomDrops = new EnumMap<>(EntityType.class);

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
        List<?> rewardList = SMPtweaks.getPlugin().getConfig().getList("rewards.contents");

        for (Object rewardSingle : rewardList) {
            Map reward = (Map) rewardSingle;
            int level = reward.get("level") == null ? 0 : Integer.parseInt(reward.get("level").toString());
            int minLevel = reward.get("min_level") == null ? level : Integer.parseInt(reward.get("min_level").toString());
            int maxLevel = reward.get("max_level") == null ? Integer.MAX_VALUE : Integer.parseInt(reward.get("max_level").toString());
            int amount = reward.get("amount") == null ? 1 : Integer.parseInt(reward.get("amount").toString());
            int minAmount = reward.get("min_amount") == null ? amount : Integer.parseInt(reward.get("min_amount").toString());
            int maxAmount = reward.get("max_amount") == null ? amount : Integer.parseInt(reward.get("max_amount").toString());
            int weight = reward.get("weight") == null ? 1 : Integer.parseInt(reward.get("weight").toString());

            var material = Material.getMaterial(reward.get("material").toString().toUpperCase());
            if(material == null) {
                LoggingUtils.warn("Invalid reward '" + reward.get("material").toString() + "'");
                continue;
            }
            var itemStack = new ItemStack(material);

            // Display Name
            Object configDisplayName = reward.get("display_name");
            if(configDisplayName != null) {
                applyDisplayName(configDisplayName, itemStack, "for " + material);
            }

            // Item Lore
            List<String> configLore = reward.get("lore") instanceof String ? List.of(reward.get("lore").toString()) : (List) reward.get("lore");
            if(configLore != null) {
                applyLore(configLore, itemStack, "for reward '" + material + "'");
            }

            // Enchantments
            var enchantmentsList = (List) reward.get("enchantments");
            if(enchantmentsList != null) {
                applyEnchantments(enchantmentsList, itemStack, "for reward '" + material + "'");
            }

            // Potion Effect
            var potionEffect = reward.get("potion_type");
            if(potionEffect != null && (
                    material == Material.POTION ||
                    material == Material.SPLASH_POTION ||
                    material == Material.LINGERING_POTION
            )) {
                applyPotionEffect(potionEffect, itemStack, "for reward '" + material + "'");
            }

            rewardsList.add(new Reward(itemStack, minLevel, maxLevel, minAmount, maxAmount, weight));
        }

        //
        // Custom Recipes
        //
        List<?> customRecipesList = SMPtweaks.getCfg().getList("custom_recipes.recipes");
        var i = 1;
        for (Object customRecipeSingle : customRecipesList) {
            var customRecipe = (Map) customRecipeSingle;
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
            List<String> configLore = customRecipe.get("lore") instanceof String ? List.of(customRecipe.get("lore").toString()) : (List) customRecipe.get("lore");
            if(configLore != null) {
                applyLore(configLore, itemStack, "in recipe for " + material);
            }

            // Amount
            String amountString = customRecipe.get("amount") == null ? "1" : customRecipe.get("amount").toString();
            var amount = Integer.parseInt(amountString);
            itemStack.setAmount(Math.min(64, amount));

            // Enchantments
            var enchantmentsList = (List) customRecipe.get("enchantments");
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
                List ingredients = (List) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    Map ingredient = (Map) ingredientSingle;
                    var materialIngredient = Material.getMaterial(ingredient.get("material").toString());
                    var ingredientCountString = ingredient.get("amount") == null ? "1" : ingredient.get("amount").toString();
                    var ingredientAmount = Integer.parseInt(ingredientCountString);

                    if(materialIngredient == null) {
                        LoggingUtils.warn("Invalid ingredient '" + ingredient.get("material").toString() + "' in recipe for " + material);
                        continue;
                    }

                    shapelessRecipe.addIngredient(Math.min(9, ingredientAmount), materialIngredient);
                    shapelessRecipes.add(shapelessRecipe);
                }
            } else {
                var shapedRecipe = new ShapedRecipe(recipeKey, itemStack);

                ArrayList<?> shape = (ArrayList) customRecipe.get("shape");
                var firstLine = shape.get(0).toString();
                var secondLine = shape.get(1).toString();
                var thirdLine = shape.get(2).toString();

                shapedRecipe.shape(
                        firstLine,
                        secondLine,
                        thirdLine
                );

                List ingredients = (List) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    var ingredient = (Map) ingredientSingle;
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
            var spawnRate = (Map) spawnRateSingle;
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
            } catch (NullPointerException | NumberFormatException e) {
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
        List<?> dropsMobList = SMPtweaks.getCfg().getList("custom_drops.mobs");
        for (Object dropsMobSingle : dropsMobList){
            var dropsMob = (Map) dropsMobSingle;
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

            // Command
            List<String> commandList = dropsMob.get("commands") instanceof String ? List.of(dropsMob.get("commands").toString()) : (List) dropsMob.get("commands");

            // Loop through drops
            List<?> dropsList = (List) dropsMob.get("drops");
            var drops = new HashMap<ItemStack, Float>();
            if(dropsList != null) {
                for (Object dropSingle : dropsList) {
                    var drop = (Map) dropSingle;

                    // Material
                    var materialString = drop.get("material").toString();
                    var material = Material.getMaterial(materialString.toUpperCase());
                    if (material == null) {
                        LoggingUtils.warn("Invalid material '" + materialString + "' in " + entityType + " custom drops");
                        continue;
                    }
                    var itemStack = new ItemStack(material);
                    String context = "for item " + materialString + " in " + entityType + " custom drops";

                    // Display Name
                    Object configDisplayName = drop.get("display_name");
                    if (configDisplayName != null) {
                        applyDisplayName(configDisplayName, itemStack, context);
                    }

                    // Item Lore
                    List<String> loreList = drop.get("lore") instanceof String ? List.of(drop.get("lore").toString()) : (List) drop.get("lore");
                    if (loreList != null) {
                        applyLore(loreList, itemStack, context);
                    }

                    // Amount
                    String amountString = drop.get("amount") == null ? "1" : drop.get("amount").toString();
                    var amount = Integer.parseInt(amountString);
                    itemStack.setAmount(Math.min(64, amount));

                    // Enchantments
                    var enchantmentsList = (List) drop.get("enchantments");
                    if (enchantmentsList != null) {
                        applyEnchantments(enchantmentsList, itemStack, context);
                    }

                    // Potion Effect
                    Object potionEffect = drop.get("potion_type");
                    if (potionEffect != null && (
                            material == Material.POTION ||
                            material == Material.SPLASH_POTION ||
                            material == Material.LINGERING_POTION
                    )) {
                        applyPotionEffect(potionEffect, itemStack, context);
                    }

                    // Drop Chance
                    var chanceString = drop.get("chance").toString();
                    float chance;
                    try {
                        chance = Float.parseFloat(chanceString);
                    } catch (NullPointerException | NumberFormatException e) {
                        LoggingUtils.warn("Invalid drop chance for item " + materialString + " in " + entityType + " custom drops");
                        continue;
                    }
                    if (chance > 1) {
                        chance = 1F;
                        LoggingUtils.warn("Changing drop chance '" + chanceString + "' for item " + materialString + " in " + entityType + " custom drops to 1.0 because it is higher than allowed");
                    }
                    if (chance < 0) {
                        chance = 0F;
                        LoggingUtils.warn("Changing drop chance '" + chanceString + "' for item '" + materialString + " in " + entityType + " custom drops to 0 because it is lower than allowed");
                    }
                    drops.put(itemStack, chance);
                }
            }
            entityCustomDrops.put(entityType, new CustomDrop(xpDrop, drops, commandList));
        }
    }

    private void applyLore(List configLore, ItemStack itemStack, String context) {
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

    private void applyPotionEffect(Object potionEffect, ItemStack itemStack, String context) {
        var potionMeta = (PotionMeta) itemStack.getItemMeta();
        PotionType potionType;
        try {
            potionType = PotionType.valueOf(potionEffect.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            LoggingUtils.warn("Invalid potion type '" + potionEffect + "' " + context);
            return;
        }
        potionMeta.setBasePotionData(new PotionData(potionType));
        itemStack.setItemMeta(potionMeta);
    }

    private void applyEnchantments(List enchantmentsList, ItemStack itemStack, String context) {
        for (Object enchantmentSingle : enchantmentsList) {
            var enchantmentMap = (Map) enchantmentSingle;

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

    public Map<EntityType, CustomDrop> getEntityCustomDrops() {
        return entityCustomDrops;
    }
}
