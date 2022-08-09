package io.noni.smptweaks.placeholders;

import io.noni.smptweaks.models.PDCKey;
import io.noni.smptweaks.utils.NumberUtils;
import io.noni.smptweaks.utils.PDCUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LevelExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "smptweaks";
    }

    @Override
    public String getAuthor() {
        return "noniDOTio";
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if(player == null){
            return "";
        }

        return switch (identifier.toLowerCase()) {
            case "level" -> PDCUtils.get(player, PDCKey.LEVEL).toString();
            case "total_xp" -> NumberUtils.format(PDCUtils.get(player, PDCKey.TOTAL_XP));
            default -> null;
        };
    }
}