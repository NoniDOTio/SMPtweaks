package io.noni.smptweaks.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    public static String format(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        return df.format(number);
    }
}
