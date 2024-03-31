package com.github.nyuppo.util;

import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class VariantBag {
    private class Entry {
        double accumulatedWeight;
        MobVariant variant;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private final EntityType<?> mob;

    public VariantBag(EntityType<?> mob, List<MobVariant> variants) {
        this.mob = mob;
        for (MobVariant variant : variants) {
            addEntry(variant);
        }
    }

    public void addEntry(MobVariant variant) {
        accumulatedWeight += variant.getWeight();
        Entry e = new Entry();
        e.variant = variant;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public MobVariant getRandomEntry(Random random) {
        double r = random.nextDouble() * accumulatedWeight;

        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.variant;
            }
        }

        // Should only occur when there are no entries
        return Variants.getDefaultVariant(mob);
    }
}
