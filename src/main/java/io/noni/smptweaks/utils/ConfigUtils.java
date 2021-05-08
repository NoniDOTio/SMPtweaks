package io.noni.smptweaks.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

    /**
     *
     * @param file
     * @return
     */
    private static boolean fixPermissions(@NotNull File file) {
        boolean writeable = true;
        boolean readable = true;
        if (!file.canWrite()) {
            writeable = file.setWritable(true, false);
        }
        if (!file.canRead()) {
            readable = file.setReadable(true, false);
        }
        return writeable && readable;
    }

    /**
     * Parse one dimensional yaml file
     * @param inputStream
     * @return map with key value string pairs
     * @throws IOException
     */
    public static Map<String, String> parseSimpleConfig(@NotNull InputStream inputStream) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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
     *
     * @param value
     * @return
     */
    private static String prepareValue(@NotNull String value) {
        return value.trim().replaceAll("^'|'$", "");
    }
}
