package com.github.nyuppo.util;

import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedRandomBag<T extends Object> {
    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private final Random rand;

    public WeightedRandomBag(Random random) {
        this.rand = random;
    };

    public WeightedRandomBag(Random random, HashMap<T, Integer> weights) {
        this.rand = random;
        for (Map.Entry<T, Integer> entry : weights.entrySet()) {
            addEntry(entry.getKey(), (double)entry.getValue());
        }
    }

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}
