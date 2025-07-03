package com.example.hotel.util;

import java.util.Map;

public class Utils {

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String clean(String value) {
        return isNullOrBlank(value) ? "" : value.trim();
    }

    public static String longerOrNonNull(String s1, String s2) {
        if (isNullOrBlank(s1)) return s2;
        if (isNullOrBlank(s2)) return s1;
        return s1.length() >= s2.length() ? s1 : s2;
    }

    /**
     * Converts camel case or Pascal case like "DryCleaning" to "dry cleaning"
     */
    public static String humanize(String raw) {
        String cleaned = clean(raw);

        // Define whitelist words
        Map<String, String> whitelist = Map.of(
                "WiFi", "wifi",
                "BathTub", "bathtub"
        );

        if (whitelist.containsKey(cleaned)) {
            return whitelist.get(cleaned);
        }

        // Insert space before each capital letter (except the first)
        String spaced = cleaned.replaceAll("(?<=[a-z])(?=[A-Z])", " ");

        return spaced.toLowerCase();
    }

    public static Double safeDouble(Object value) {
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
