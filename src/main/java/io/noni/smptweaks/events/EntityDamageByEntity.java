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

        PotionEffectType potionEffectType = arrow.getBasePotionData().getType().getEffectType();
        int duration;
        boolean isExtended = arrow.getBasePotionData().isExtended();
        if(
                potionEffectType == PotionEffectType.FIRE_RESISTANCE ||
                potionEffectType == PotionEffectType.INVISIBILITY ||
                potionEffectType == PotionEffectType.JUMP ||
                potionEffectType == PotionEffectType.NIGHT_VISION ||
                potionEffectType == PotionEffectType.INCREASE_DAMAGE ||
                potionEffectType == PotionEffectType.SPEED ||
                potionEffectType == PotionEffectType.WATER_BREATHING
        ) {
            duration = isExtended ? 1200 : 440;
        } else if(potionEffectType == PotionEffectType.HEAL) {
            duration = 1;
        } else if(potionEffectType == PotionEffectType.SLOW_FALLING) {
            duration = isExtended ? 600 : 220;
        } else if(potionEffectType == PotionEffectType.REGENERATION) {
            duration = isExtended ? 220 : 100;
        } else {
            return;
        }

        int amplifier = arrow.getBasePotionData().isUpgraded() ? 1 : 0;
        player.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7F,  0.5F + (float) player.getHealth() / 15F);
        arrow.remove();
        e.setCancelled(true);
    }
}
