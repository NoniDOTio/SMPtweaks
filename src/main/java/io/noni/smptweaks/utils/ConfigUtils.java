package io.noni.smptweaks.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

    private ConfigUtils() {
        throw new AssertionError("This utility class cannot be instantiated");
    }

    /**
     * Parse one dimensional yaml
     * @param inputStream yaml file
     * @return map with key value string pairs
     * @throws IOException if file cannot be read
     */
    public static Map<String, String> parseSimpleConfig(@NotNull InputStream inputStream) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while(reader.ready()) {
            String line = reader.readLine();
            if(line == null ) continue;

            // Remove comments
            line = line.replaceAll("#.*$", "");

            // Skip line if there is no way it can contain a sane key value pair
            if(line.length() < 2) continue;

            // Parse line
            String[] fields = line.split(":", 2);
            map.put(fields[0], prepareValue(fields[1] != null ? fields[1] : ""));
        }
        return map;
    }

    /**
     * Remove string quotes as well as leading and trailing whitespace
     * @param value
     * @return
     */
    private static String prepareValue(@NotNull String value) {
        return value.trim().replaceAll("^'|'$", "");
    }
}
