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

        if(arrow.getBasePotionData().getType().getEffectType() != PotionEffectType.HEAL) {
            return;
        }

        arrow.setShooter(null);
    }
    
}
