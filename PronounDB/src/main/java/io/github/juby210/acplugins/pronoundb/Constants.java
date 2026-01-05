/*
 * Copyright (c) 2021 Juby210
 * Licensed under the Open Software License version 3.0
 */

package io.github.juby210.acplugins.pronoundb;

import android.text.TextUtils;

import java.util.*;

public final class Constants {
    public static final String WEBSITE = "https://pronoundb.org";
    public static final String USER_AGENT = "Aliucord PronounDB Plugin/1.0.11 (+https://github.com/Juby210/Aliucord-plugins)";

    public static class Endpoints {
        public static String LOOKUP_BULK(Object[] ids) { return WEBSITE + "/api/v1/lookup-bulk?platform=discord&ids=" + TextUtils.join(",", ids); }
    }

    Map<String, String[]> pronounParts = new HashMap<>() {{
        put("he", new String[]{"he", "him"});
        put("she", new String[]{"she", "her"});
        put("they", new String[]{"they", "them"});
        put("it", new String[]{"it", "its"});
        put("any", new String[]{"Any pronouns"});
        put("ask", new String[]{"Ask me my pronouns"});
        put("avoid", new String[]{"Avoid pronouns, use my name"});
        put("other", new String[]{"Other pronouns"});
    }};

    public static String getPronouns(List<String> p, int format) {
        if (p == null || p.isEmpty()) {
            return null;
        }

        List<String> formattedParts = new ArrayList<>();

        for (String k : p) {
            String[] parts = p.get(k);

            if (parts != null) {
                if (parts.length == 1) {
                    formattedParts.add(parts[0]);
                } else {
                    String display = (format == 1 ? capitalise(parts[0]) : parts[0]) + "/" + (format == 1 ? capitalise(parts[1]) : parts[1]);
                    formattedParts.add(display);
                }
            }
        }

        return TextUtils.join(", ", formattedParts);
    }

    private static String capitalise(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
