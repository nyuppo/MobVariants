package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.variant.BiomeDiscardableVariant;
import com.github.nyuppo.variant.MobVariant;
import com.github.nyuppo.variant.ShinyDiscardableVariant;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Variants {
    private static HashMap<Mob, ArrayList<MobVariant>> variants;
    private static HashMap<Mob, ArrayList<MobVariant>> defaultVariants;

    public static void addVariant(Mob mob, MobVariant variant) {
        if (variants.get(mob) == null) {
            variants.put(mob, new ArrayList<MobVariant>());
        }

        variants.get(mob).add(variant);
    }

    public static ArrayList<MobVariant> getVariants(Mob mob) {
        if (variants.get(mob) != null) {
            return variants.get(mob);
        }
        return defaultVariants.get(mob);
    }

    public static void resetVariants(Mob mob) {
        variants.remove(mob);
        variants.put(mob, defaultVariants.get(mob));
    }

    public enum Mob {
        CHICKEN,
        COW,
        PIG,
        SHEEP,
        SPIDER,
        WOLF,
        ZOMBIE
    }

    static {
        defaultVariants = new HashMap<Mob, ArrayList<MobVariant>>();
        defaultVariants.put(Mob.CHICKEN, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("midnight"), 1),
                new MobVariant(MoreMobVariants.id("amber"), 2),
                new MobVariant(MoreMobVariants.id("gold_crested"), 2),
                new MobVariant(MoreMobVariants.id("bronzed"), 2),
                new MobVariant(MoreMobVariants.id("skewbald"), 2),
                new MobVariant(MoreMobVariants.id("stormy"), 2),
                new BiomeDiscardableVariant(MoreMobVariants.id("bone"), 1, 0.83, BiomeTags.IS_NETHER),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
    }
}
