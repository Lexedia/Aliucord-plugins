/*
 * Copyright (c) 2021 Juby210
 * Licensed under the Open Software License version 3.0
 */

package io.github.juby210.acplugins.pronoundb;

import com.aliucord.Http;
import com.aliucord.Main;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public final class Store {
    public static Map<Long, List<String>> cache = new HashMap<>();

    
    static class PronounDBUserResponse {
        public Map<String, List<String>> sets;
    }
    

    private static final Type resType = new TypeToken<Map<String, PronounDBUserResponse>>(){}.getType();
    private static final List<Long> buffer = new ArrayList<>();
    private static Thread timerThread = new Thread(Store::runThread);
    public static void fetchPronouns(Long id) {
        var state = timerThread.getState();
        if (!timerThread.isAlive() && state != Thread.State.RUNNABLE) {
            if (state == Thread.State.TERMINATED) timerThread = new Thread(Store::runThread);
            try {
                timerThread.start();
            } catch (Throwable e) {
                Main.logger.error("Failed to start timerThread, State: " + state, e);
            }
        }
        if (!buffer.contains(id)) buffer.add(id);
        try {
            timerThread.join();
        } catch (Throwable ignored) {}
    }

    private static void runThread() {
        try {
            Thread.sleep(50);
            var bufferCopy = buffer.toArray(new Long[0]);
            buffer.clear();
            // Map<Long, String> res = Http.simpleJsonGet(Constants.Endpoints.LOOKUP_BULK(bufferCopy), resType);
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", Constants.USER_AGENT);

            Map<String, PronounDBUserResponse> res = Http.simpleJsonGet(Constants.Endpoints.LOOKUP_BULK(bufferCopy), headers, resType);

            if (res != null) {
                for (var id : bufferCopy) {
                    String idStr = id.toString();
                    PronounDBUserResponse response = res.get(idStr);

                    if (response != null && response.sets != null && response.sets.containsKey("en")) {
                        List<String> pronounKeys = response.sets.get("en");
                        if (pronounKeys != null && !pronounKeys.isEmpty()) {
                            cache.put(id, pronounKeys);
                        } else {
                            cache.put(id, Collections.emptyList());
                        }
                    } else {
                        cache.put(id, Collections.emptyList());
                    }
                }
            }
            cache.putAll(res);
            for (var id : bufferCopy) {
                if (!cache.containsKey(id)) cache.put(id, "unspecified");
            }
        } catch (Throwable e) {
            Main.logger.error("PronounDB error", e);
        }
    }
}
