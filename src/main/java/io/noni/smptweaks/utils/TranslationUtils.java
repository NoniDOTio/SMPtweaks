package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPTweaks;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TranslationUtils {

    private TranslationUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    public static Map<String, String> loadTranslations(String languageCode) {
        InputStream baseStream = SMPTweaks.getPlugin().getResource("lang/" + "en_US".toLowerCase() + ".yml");
        InputStream translationsStream = SMPTweaks.getPlugin().getResource("lang/" + languageCode.toLowerCase() + ".yml");

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
            LoggingUtils.warn("Unable to load preferred translations, defaulting to en_US (English)");
            return fallbackMap;
        }

        Map<String, String> translatedMap = null;
        try {
            translatedMap = ConfigUtils.parseSimpleConfig(translationsStream);
        } catch (IOException e) {
            LoggingUtils.warn("An error occured while parsing preferred translations, defaulting to en_US (English)");
            e.printStackTrace();
            return fallbackMap;
        }

        // Merge translations
        Map<String, String> mergedMap = new HashMap<>(fallbackMap);
        mergedMap.putAll(translatedMap);
        return mergedMap;
    }

    /**
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        return SMPTweaks.getTranslations().get(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public static String get(String key, String[] variables) {
        return SMPTweaks.getTranslations().get(key);
    }
}
