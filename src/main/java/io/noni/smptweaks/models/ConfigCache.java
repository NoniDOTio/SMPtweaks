package io.noni.smptweaks.models;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCache {
    private List<Material> alwaysDropMaterials = new ArrayList<>();
    private List<Material> neverDropMaterials = new ArrayList<>();
    private List<Reward> rewardsList = new ArrayList<>();
    private List<ShapedRecipe> shapedRecipes = new ArrayList<>();
    private List<ShapelessRecipe> shapelessRecipes = new ArrayList<>();

    public ConfigCache() {

        //
        // Item Dropping
        //
        List<String> alwaysDropStrings;
        alwaysDropStrings = SMPtweaks.getCfg().getStringList("remove_inventory_on_death.always_drop");

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
        neverDropStrings = SMPtweaks.getCfg().getStringList("remove_inventory_on_death.never_drop");

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

            Material material = Material.valueOf(reward.get("material").toString().toUpperCase());
            rewardsList.add(new Reward(material,minLevel, maxLevel, minAmount, maxAmount, weight));
        }

        //
        // Custom Recipes
        //
        List<?> customRecipesList = SMPtweaks.getCfg().getList("custom-recipes.recipes");
        int i = 1;
        for (Object customRecipeSingle : customRecipesList) {
            Map customRecipe = (Map) customRecipeSingle;
            boolean shapeless = customRecipe.get("shape") == null;


            Material material = Material.getMaterial(customRecipe.get("material").toString());

            if(material == null) {
                LoggingUtils.warn("Invalid recipe result '" + customRecipe.get("material").toString() + "'");
                continue;
            }
            ItemStack itemStack = new ItemStack(material);

            String amountString = customRecipe.get("amount") == null ? "1" : customRecipe.get("amount").toString();
            int amount = Integer.parseInt(amountString);
            itemStack.setAmount(Math.min(64, amount));

            NamespacedKey recipeKey = new NamespacedKey(SMPtweaks.getPlugin(), "custom_recipe_" + i );

            if(shapeless) {
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeKey, itemStack);
                List ingredients = (List) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    Map ingredient = (Map) ingredientSingle;
                    Material materialIngredient = Material.getMaterial(ingredient.get("material").toString());
                    String ingredientCountString = ingredient.get("amount") == null ? "1" : ingredient.get("amount").toString();
                    int ingredientAmount = Integer.parseInt(ingredientCountString);

                    if(materialIngredient == null) {
                        LoggingUtils.warn("Invalid ingredient '" + ingredient.get("material").toString() + "' in recipe for " + material);
                        continue;
                    }

                    shapelessRecipe.addIngredient(Math.min(9, ingredientAmount), materialIngredient);
                    shapelessRecipes.add(shapelessRecipe);
                }
            } else {
                ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, itemStack);

                ArrayList<?> shape = (ArrayList) customRecipe.get("shape");
                String firstLine = shape.get(0).toString();
                String secondLine = shape.get(1).toString();
                String thirdLine = shape.get(2).toString();

                shapedRecipe.shape(
                        firstLine,
                        secondLine,
                        thirdLine
                );

                List ingredients = (List) customRecipe.get("ingredients");
                for (Object ingredientSingle : ingredients) {
                    Map ingredient = (Map) ingredientSingle;
                    Material materialIngredient = Material.getMaterial(ingredient.get("material").toString());

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
}
