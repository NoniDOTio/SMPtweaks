package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPtweaks;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TranslationUtils {

    private TranslationUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    public static Map<String, String> loadTranslations(@Nullable String languageCode) {
        if(languageCode == null) languageCode = "en-US";
        InputStream baseStream = SMPtweaks.getPlugin().getResource("lang/" + "en-US".toLowerCase() + ".yml");
        InputStream translationsStream = SMPtweaks.getPlugin().getResource("lang/" + languageCode.toLowerCase() + ".yml");

        // Load base translations
        if(baseStream == null) {
            LoggingUtils.error("Default translations are missing, aborting");
            return null;
        }

        Map<String, String> fallbackMap;
        try {
            fallbackMap = ConfigUtils.parseSimpleConfig(baseStream);
        } catch (IOException e) {
            LoggingUtils.error("An error occured while parsing default translations");
            e.printStackTrace();
            return null;
        }

        // Load preferred translations
        if(translationsStream == null) {
            LoggingUtils.warn("Unable to load preferred translations, defaulting to en-US (English)");
            return fallbackMap;
        }

        Map<String, String> translatedMap = null;
        try {
            translatedMap = ConfigUtils.parseSimpleConfig(translationsStream);
        } catch (IOException e) {
            LoggingUtils.warn("An error occured while parsing preferred translations, defaulting to en-US (English)");
            e.printStackTrace();
            return fallbackMap;
        }

        // Merge translations
        Map<String, String> mergedMap = new HashMap<>(fallbackMap);
        mergedMap.putAll(translatedMap);
        return mergedMap;
    }

    /**
     * Get translated string for key
     * @param key Translation key
     * @return Formatted and translated string
     */
    public static String get(@NotNull String key) {
        String result = SMPtweaks.getTranslations().get(key);
        if (result == null) return "";
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Get translated string for key and replace variables
     * @param key Translation key
     * @return Formatted and translated string
     */
    public static String get(@NotNull String key, @NotNull String[] variables) {
        String result = SMPtweaks.getTranslations().get(key);
        if (result == null) return "";

        var i = 1;
        for(String variable : variables) {
            result = result.replace("{$" + i + "}", variable);
            i++;
        }

        return ChatColor.translateAlternateColorCodes('&', result);
    }
}
