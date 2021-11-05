package io.noni.smptweaks.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AnvilInventoryClickEvent implements Listener {

    @EventHandler
    void onAnvilClick(InventoryClickEvent e) {
        if(!(e.getInventory() instanceof AnvilInventory)) {
            return;
        }

        if(e.getCurrentItem() == null) {
            return;
        }

        if(!(e.getCurrentItem().getItemMeta() instanceof Repairable repairableItem)) {
            return;
        }

        if(!repairableItem.hasRepairCost()) {
            return;
        }

        if(repairableItem.getRepairCost() > 38) {
            repairableItem.setRepairCost(38);
            e.getCurrentItem().setItemMeta((ItemMeta) repairableItem);
        }
    }
}
