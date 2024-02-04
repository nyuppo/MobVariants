package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SheepHornSettings {
    private static double hasHornsChance;
    private static double inheritParentsChance;
    private static HashMap<SheepHornColour, Integer> weights;
    private static HashMap<SheepHornColour, Integer> defaultWeights;

    public static double getHornsChance() {
        return hasHornsChance;
    }

    public static void setHornsChance(double chance) {
        hasHornsChance = chance;
    }

    public static double getInheritChance() {
        return inheritParentsChance;
    }

    public static void setInheritChance(double chance) {
        inheritParentsChance = chance;
    }

    public static void setWeight(SheepHornColour colour, int weight) {
        if (weight <= 0) {
            weights.remove(colour);
            return;
        }

        weights.put(colour, weight);
    }

    @Nullable
    public static SheepHornColour getRandomSheepHornColour(Random random, @Nullable RegistryEntry<Biome> spawnBiome) {
        if (random.nextDouble() > hasHornsChance) {
            return null;
        }

        if (spawnBiome == null || !spawnBiome.isIn(MoreMobVariants.SHEEP_SPAWN_WITH_HORNS)) {
            return null;
        }

        WeightedRandomBag<SheepHornColour> hornColours = new WeightedRandomBag<>(random);
        weights.forEach(hornColours::addEntry);

        return hornColours.getRandom();
    }

    public static void resetSettings() {
        hasHornsChance = 0.125d;
        inheritParentsChance = 0.8d;

        weights.clear();
        weights.put(SheepHornColour.BROWN, defaultWeights.get(SheepHornColour.BROWN));
        weights.put(SheepHornColour.GRAY, defaultWeights.get(SheepHornColour.GRAY));
        weights.put(SheepHornColour.BLACK, defaultWeights.get(SheepHornColour.BLACK));
        weights.put(SheepHornColour.BEIGE, defaultWeights.get(SheepHornColour.BEIGE));
    }

    public enum SheepHornColour {
        BROWN("brown"),
        GRAY("gray"),
        BLACK("black"),
        BEIGE("beige");

        String id;

        SheepHornColour(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    static {
        hasHornsChance = 0.125d;
        inheritParentsChance = 0.8d;

        weights = new HashMap<>();

        defaultWeights = new HashMap<>();
        defaultWeights.put(SheepHornColour.BROWN, 8);
        defaultWeights.put(SheepHornColour.GRAY, 6);
        defaultWeights.put(SheepHornColour.BLACK, 3);
        defaultWeights.put(SheepHornColour.BEIGE, 1);
    }
}

class WeightedRandomBag<T extends Object> {
    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private final Random random;

    public WeightedRandomBag(Random random) {
        this.random = random;
    }

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = random.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}
