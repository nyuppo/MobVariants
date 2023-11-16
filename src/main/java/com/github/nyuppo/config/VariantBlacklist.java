package com.github.nyuppo.config;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class VariantBlacklist {
    private static HashMap<EntityType<?>, ArrayList<Identifier>> blacklistedIdentifiers = new HashMap<>();

    public static void blacklist(EntityType<?> mob, Identifier identifier) {
        if (!blacklistedIdentifiers.containsKey(mob)) {
            blacklistedIdentifiers.put(mob, new ArrayList<>());
        }
        blacklistedIdentifiers.get(mob).add(identifier);
    }

    public static boolean isBlacklisted(EntityType<?> mob, Identifier identifier) {
        if (!blacklistedIdentifiers.containsKey(mob)) {
            return false;
        }

        return blacklistedIdentifiers.get(mob).contains(identifier);
    }

    public static void clearBlacklist(EntityType<?> mob) {
        blacklistedIdentifiers.remove(mob);
        blacklistedIdentifiers.put(mob, new ArrayList<>());
    }

    public static void clearAllBlacklists() {
        Variants.getAllDefaultVariants().keySet().forEach(VariantBlacklist::clearBlacklist);
        clearBlacklist(EntityType.CAT); // Special case for cat since it isn't included in variants list
    }
}
