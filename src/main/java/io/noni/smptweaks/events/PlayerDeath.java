package io.noni.smptweaks.events;

import io.noni.smptweaks.SMPTweaks;
import io.noni.smptweaks.utils.LoggingUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity().getPlayer();

        if(!player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) {
            LoggingUtils.warn("Skipping PlayerDeath logic. You need to set keepInventory to TRUE in order to use this feature.");
            return;
        }

        /**
         * Remove XP from player and drop on ground
         */
        if(SMPTweaks.getCfg().getBoolean("remove_xp_on_death.enabled")) {
            int currentXp = player.getTotalExperience();

            // Calculate how much to drop
            int baseXpToRemove = SMPTweaks.getCfg().getInt("remove_xp_on_death.base");
            int portionXpToRemove = (int) (SMPTweaks.getCfg().getDouble("remove_xp_on_death.portion_of_current") * currentXp);
            int randomizedXpToRemove = SMPTweaks.getCfg().getBoolean("remove_xp_on_death.randomize") ?
                    randomize(portionXpToRemove, 0.3) : portionXpToRemove;
            int resultXpToRemove = baseXpToRemove + randomizedXpToRemove;
            int xpToRemove = Math.min(resultXpToRemove, SMPTweaks.getCfg().getInt("remove_xp_on_death.max"));

            // Remove XP from player
            player.setTotalExperience(Math.max(0, currentXp - xpToRemove));

            // Spawn XP orbs on the ground
            if(SMPTweaks.getCfg().getBoolean("remove_xp_on_death.drop_on_ground")) {
                double xpDropMultiplier = SMPTweaks.getCfg().getDouble("remove_xp_on_death.drop_multiplier");
                spawnOrbs((int) Math.floor(xpToRemove * xpDropMultiplier), 100, player);
            }
        }

        /**
         * Remove inventory items from player and drop on ground
         */
        if(SMPTweaks.getCfg().getBoolean("remove_inventory_on_death.enabled")) {
            double inventoryChancePerSlot = SMPTweaks.getCfg().getDouble("remove_inventory_on_death.chance_per_slot");
            double inventoryPortionPerSlot = SMPTweaks.getCfg().getDouble("remove_inventory_on_death.portion_per_slot");
            boolean inventoryRandomize = SMPTweaks.getCfg().getBoolean("remove_inventory_on_death.randomize");
            int minAffectedStackSize = SMPTweaks.getCfg().getBoolean(
                    "remove_inventory_on_death.include_non_stackable_items"
            ) ? 0 : 1;
            boolean inventoryDropOnGround = SMPTweaks.getCfg().getBoolean("remove_inventory_on_death.drop_on_ground");
            double inventoryDropMultiplier = SMPTweaks.getCfg().getDouble("remove_inventory_on_death.drop_multiplier");

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && Math.random() < inventoryChancePerSlot) {
                    int itemStackSize = itemStack.getAmount();

                    if(itemStackSize > minAffectedStackSize) {
                        double portionToDrop = itemStackSize * inventoryPortionPerSlot;
                        int amountToRemove = inventoryRandomize ? randomize(portionToDrop, 0.6) : (int) portionToDrop;
                        int amountToKeep = itemStackSize - amountToRemove;

                        itemStack.setAmount(amountToKeep);

                        if(inventoryDropOnGround) {
                            ItemStack itemStackToDrop = itemStack.clone();
                            itemStackToDrop.setAmount((int) Math.floor(amountToRemove * inventoryDropMultiplier));
                            player.getWorld().dropItemNaturally(player.getLocation(), itemStackToDrop);
                        }
                    }
                }
            }
        }

        /**
         * Remove equipment from player and drop on ground
         */
        if(SMPTweaks.getCfg().getBoolean("remove_equipment_on_death.enabled")) {
            double equipmentChancePerSlot = SMPTweaks.getCfg().getDouble("remove_equipment_on_death.chance_per_slot");
            boolean equipmentDropOnGround = SMPTweaks.getCfg().getBoolean("remove_equipment_on_death.drop_on_ground");
            ItemStack[] armorContentsBefore = player.getInventory().getArmorContents();
            ItemStack[] armorContentsAfter = new ItemStack[4];

            int i = 0;
            for(ItemStack stack : armorContentsBefore) {
                if(stack != null && Math.random() < equipmentChancePerSlot) {
                    if(equipmentDropOnGround) {
                        player.getWorld().dropItemNaturally(player.getLocation(), stack);
                    }
                    armorContentsAfter[i] = null;
                } else {
                    armorContentsAfter[i] = stack;
                }
                i++;
            }
            player.getInventory().setArmorContents(armorContentsAfter);
        }

        /**
         * Send chat message to dead player
         */
        player.sendMessage(ChatColor.RED + "Du hast einen Teil deiner Gegenstände und Erfahrung verloren :(");
    }

    private int randomize(double amount, double deviation) {
        double multiplier = Math.random() * (2 * deviation) - deviation;
        return (int) (amount * multiplier);
    }

    private void spawnOrbs(int xp, int orbSize, Player player) {
        while(xp > 0) {
            Location loc = player.getLocation().add(Math.random() - 0.5, Math.random(), Math.random() - 0.5);
            ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
            orb.setExperience(Math.min(orbSize, xp));
            xp = xp - orbSize;
        }
    }
}
