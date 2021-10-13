package io.noni.smptweaks.events;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageByEntity implements Listener {
    @EventHandler
    void oneEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player player)) {
            return;
        }

        if(!(e.getDamager() instanceof Arrow arrow)) {
            return;
        }

        if(arrow.getBasePotionData().getType().getEffectType() != PotionEffectType.HEAL) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7F,  0.5F + (float) player.getHealth() / 15F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
        arrow.remove();
        e.setCancelled(true);
    }
}
