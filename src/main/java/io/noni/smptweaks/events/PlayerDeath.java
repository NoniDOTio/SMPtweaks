package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPtweaks;
import io.noni.smptweaks.utils.ChatUtils;
import io.noni.smptweaks.utils.ExperienceUtils;
import io.noni.smptweaks.utils.LoggingUtils;
import io.noni.smptweaks.utils.TranslationUtils;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        var player = e.getEntity().getPlayer();
        boolean isPvpDeath = player.getKiller() != null;

        //
        // Alter durability of items
        //
        if(
            SMPtweaks.getCfg().getBoolean("decrease_item_durability_on_death.enabled") &&
            (( isPvpDeath && !SMPtweaks.getCfg().getBoolean("decrease_item_durability_on_death.disable_for_pvp_deaths") ) ||
            ( !isPvpDeath && !SMPtweaks.getCfg().getBoolean("decrease_item_durability_on_death.disable_for_non_pvp_deaths") ))
        ) {
            var durabilityMultiplier = SMPtweaks.getCfg().getDouble("decrease_item_durability_on_death.multiplier");
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                multiplyItemDurability(itemStack, durabilityMultiplier);
            }
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                multiplyItemDurability(itemStack, durabilityMultiplier);
            }
        }

        if(!player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) {
            LoggingUtils.warn("Skipping remaining PlayerDeath logic. You need to set keepInventory to TRUE in order to use this feature.");
            return;
        }

        //
        // Remove XP from player and drop on ground
        //
        if(
            SMPtweaks.getCfg().getBoolean("remove_xp_on_death.enabled") &&
            (( isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_xp_on_death.disable_for_pvp_deaths") ) ||
            ( !isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_xp_on_death.disable_for_non_pvp_deaths") ))
        ) {
            var currentXp = ExperienceUtils.getTotalExperience(player);

            // Calculate how much to drop
            var baseXpToRemove = SMPtweaks.getCfg().getInt("remove_xp_on_death.base");
            var portionXpToRemove = (int) (SMPtweaks.getCfg().getDouble("remove_xp_on_death.portion_of_current") * currentXp);
            var randomizedXpToRemove = SMPtweaks.getCfg().getBoolean("remove_xp_on_death.randomize") ?
                    randomize(portionXpToRemove, 0.3) : portionXpToRemove;
            var resultXpToRemove = baseXpToRemove + randomizedXpToRemove;
            var xpToRemove = Math.min(resultXpToRemove, SMPtweaks.getCfg().getInt("remove_xp_on_death.max"));

            // Remove XP from player
            ExperienceUtils.setTotalExperience(player, Math.max(0, currentXp - xpToRemove));

            // Spawn XP orbs on the ground
            if(SMPtweaks.getCfg().getBoolean("remove_xp_on_death.drop_on_ground")) {
                var xpDropMultiplier = SMPtweaks.getCfg().getDouble("remove_xp_on_death.drop_amount_multiplier");
                spawnOrbs(
                        (int) Math.floor(xpToRemove * xpDropMultiplier),
                        Math.max(50, xpToRemove / 10),
                        player.getWorld(),
                        player.getLocation()
                );
            }
        }

        //
        // Remove inventory items from player and drop on ground
        //
        if(
            SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.enabled") &&
            (( isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.disable_for_pvp_deaths") ) ||
            ( !isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.disable_for_non_pvp_deaths") ))
        ) {
            var inventoryChancePerSlot = SMPtweaks.getCfg().getDouble("remove_inventory_on_death.chance_per_slot");
            var inventoryPortionPerSlot = SMPtweaks.getCfg().getDouble("remove_inventory_on_death.portion_per_slot");
            var inventoryRandomize = SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.randomize");
            var minAffectedStackSize = SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.include_non_stackable_items") ? 0 : 1;
            var inventoryDropOnGround = SMPtweaks.getCfg().getBoolean("remove_inventory_on_death.drop_on_ground");
            var inventoryDropMultiplier = SMPtweaks.getCfg().getDouble("remove_inventory_on_death.drop_amount_multiplier");

            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack == null) {
                    continue;
                }

                // Do not drop item if it's listed
                List<Material> materialsToSkip = SMPtweaks.getConfigCache().getNeverDropMaterials();
                if (materialsToSkip.contains(itemStack.getType())) {
                    continue;
                }

                // Always drop item if it's listed
                List<Material> materialsToAlwaysDrop = SMPtweaks.getConfigCache().getAlwaysDropMaterials();
                if (inventoryDropOnGround && materialsToAlwaysDrop.contains(itemStack.getType())) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    player.getInventory().remove(itemStack);
                    continue;
                }

                // Drop item logic
                if (Math.random() < inventoryChancePerSlot) {
                    int itemStackSize = itemStack.getAmount();

                    if(minAffectedStackSize < itemStack.getMaxStackSize()) {
                        double portionToRemove = itemStackSize * inventoryPortionPerSlot;
                        int amountToRemove = inventoryRandomize ? randomize(portionToRemove, 0.6) : (int) portionToRemove;
                        int amountToKeep = itemStackSize - amountToRemove;
                        int amountToDrop = (int) Math.round(amountToRemove * inventoryDropMultiplier);

                        if(amountToKeep < 1) {
                            player.getInventory().remove(itemStack);
                        } else {
                            itemStack.setAmount(amountToKeep);
                        }
                        var itemStackToDrop = itemStack.clone();
                        itemStackToDrop.setAmount(amountToDrop);

                        if(inventoryDropOnGround && !itemStack.getType().isAir() && amountToDrop > 0) {
                            player.getWorld().dropItemNaturally(player.getLocation(), itemStackToDrop);
                        }
                    }
                }
            }
        }

        //
        // Remove equipment from player and drop on ground
        //
        if(
            SMPtweaks.getCfg().getBoolean("remove_equipment_on_death.enabled") &&
            (( isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_equipment_on_death.disable_for_pvp_deaths") ) ||
            ( !isPvpDeath && !SMPtweaks.getCfg().getBoolean("remove_equipment_on_death.disable_for_non_pvp_deaths") ))
        ) {
            var equipmentChancePerSlot = SMPtweaks.getCfg().getDouble("remove_equipment_on_death.chance_per_slot");
            var equipmentDropOnGround = SMPtweaks.getCfg().getBoolean("remove_equipment_on_death.drop_on_ground");

            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack chestplate = player.getInventory().getChestplate();
            ItemStack leggings = player.getInventory().getLeggings();
            ItemStack boots = player.getInventory().getBoots();

            // Helmet
            if(helmet != null && Math.random() < equipmentChancePerSlot) {
                player.getInventory().setHelmet(null);
                if(equipmentDropOnGround) {
                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                }
            }

            // Chestplate
            if(chestplate != null && Math.random() < equipmentChancePerSlot) {
                player.getInventory().setChestplate(null);
                if(equipmentDropOnGround) {
                    player.getWorld().dropItemNaturally(player.getLocation(), chestplate);
                }
            }

            // Leggings
            if(leggings != null && Math.random() < equipmentChancePerSlot) {
                player.getInventory().setLeggings(null);
                if(equipmentDropOnGround) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leggings);
                }
            }

            // Boots
            if(boots != null && Math.random() < equipmentChancePerSlot) {
                player.getInventory().setBoots(null);
                if(equipmentDropOnGround) {
                    player.getWorld().dropItemNaturally(player.getLocation(), boots);
                }
            }
        }

        //
        // Send chat message to dead player
        //
        ChatUtils.negative(player, TranslationUtils.get("playerdeath-lost-xp-and-items"));
    }

    /**
     *
     * @param amount
     * @param deviation
     * @return
     */
    private int randomize(double amount, double deviation) {
        double multiplier = 1 + (Math.random() * 2 * deviation) - deviation;
        return (int) Math.round(amount * multiplier);
    }

    /**
     *
     * @param itemStack
     * @param multiplier
     * @return
     */
    private ItemStack multiplyItemDurability(ItemStack itemStack, double multiplier) {

        if(itemStack == null) {
            return null;
        }

        var itemMeta = itemStack.getItemMeta();
        if(!(itemMeta instanceof Damageable)) {
            return itemStack;
        }

        // Apply multiplier logic
        int maximumDurability = itemStack.getType().getMaxDurability();
        var damageableItemMeta = (Damageable) itemMeta;
        int currentDurability = maximumDurability - damageableItemMeta.getDamage();
        int newDurability = (int) Math.round(currentDurability * multiplier);

        if(newDurability > maximumDurability) {
            newDurability = maximumDurability;
        } else if(newDurability < 1) {
            newDurability = 1;
        }

        int newDamage = maximumDurability - newDurability;
        damageableItemMeta.setDamage(newDamage);
        itemStack.setItemMeta((ItemMeta) damageableItemMeta);

        return itemStack;
    }

    /**
     *
     * @param xp
     * @param orbSize
     * @param world
     * @param loc
     */
    private void spawnOrbs(int xp, int orbSize, World world, Location loc) {
        while(xp > 0) {
            ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
            orb.setExperience(Math.min(orbSize, xp));
            xp = xp - orbSize;
        }
    }
}
