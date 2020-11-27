package io.noni.smptweaks.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    private NumberUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Add thousands separators to number
     * @param number
     * @return formatted number
     */
    public static String format(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        return df.format(number);
    }
}
