package io.noni.smptweaks.events;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffectType;

public class ProjectileLaunch implements Listener {

    @EventHandler
    void onProjectileLaunch(ProjectileLaunchEvent e) {
        if(!(e.getEntity() instanceof Arrow arrow)) {
            return;
        }

        PotionEffectType potionEffectType = arrow.getBasePotionData().getType().getEffectType();
        if(
                potionEffectType == PotionEffectType.FIRE_RESISTANCE ||
                potionEffectType == PotionEffectType.HEAL ||
                potionEffectType == PotionEffectType.INVISIBILITY ||
                potionEffectType == PotionEffectType.JUMP ||
                potionEffectType == PotionEffectType.SLOW_FALLING ||
                potionEffectType == PotionEffectType.NIGHT_VISION ||
                potionEffectType == PotionEffectType.INCREASE_DAMAGE ||
                potionEffectType == PotionEffectType.REGENERATION ||
                potionEffectType == PotionEffectType.SPEED ||
                potionEffectType == PotionEffectType.WATER_BREATHING
        ) {
            arrow.setShooter(null);
        }
    }
    
}
