package io.noni.smptweaks.utils;

import io.noni.smptweaks.SMPTweaks;

public class LoggingUtils {

    private LoggingUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Log a debug message
     * @param text
     */
    public static void debug(String text) {
        if(SMPTweaks.getPlugin().getConfig().getBoolean("verbose") == true) {
            SMPTweaks.getPlugin().getLogger().info(text);
        }
    }

    /**
     * Log an info text
     * @param text
     */
    public static void info(String text) {
        SMPTweaks.getPlugin().getLogger().info(text);
    }

    /**
     * Log a warning message
     * @param text
     */
    public static void warn(String text) {
        SMPTweaks.getPlugin().getLogger().warning(text);
    }

    /**
     * Log an error
     * @param text
     */
    public static void error(String text) {
        SMPTweaks.getPlugin().getLogger().severe(text);
    }
}
