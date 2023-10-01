package com.github.nyuppo.config;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class VariantBlacklist {
    private static HashMap<Variants.Mob, ArrayList<Identifier>> blacklistedIdentifiers = new HashMap<>();

    public static void blacklist(Variants.Mob mob, Identifier identifier) {
        if (!blacklistedIdentifiers.containsKey(mob)) {
            blacklistedIdentifiers.put(mob, new ArrayList<>());
        }
        blacklistedIdentifiers.get(mob).add(identifier);
    }

    public static boolean isBlacklisted(Variants.Mob mob, Identifier identifier) {
        if (!blacklistedIdentifiers.containsKey(mob)) {
            return false;
        }

        return blacklistedIdentifiers.get(mob).contains(identifier);
    }

    public static void clearBlacklist(Variants.Mob mob) {
        blacklistedIdentifiers.remove(mob);
        blacklistedIdentifiers.put(mob, new ArrayList<>());
    }

    public static void clearAllBlacklists() {
        for (Variants.Mob mob : Variants.Mob.values()) {
            clearBlacklist(mob);
        }
    }
}
